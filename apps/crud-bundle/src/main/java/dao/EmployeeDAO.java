package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.villageworks.app.DateConverter;

import bean.EmployeeBean;
import common.Utils;
import dao.criteria.EmployeeCriteria;

/**
 * employeesテーブルアクセスするDAOクラス
 */
public class EmployeeDAO extends BaseDAO {

	/**
	 * クラス定数
	 */
	private static final String SQL_SELECT      = "SELECT * FROM employees ";
	private static final String SQL_ORDER_BY_ID = "ORDER BY id";
	private static final String SQL_FIND_ALL    = SQL_SELECT + SQL_ORDER_BY_ID;
	
	/**
	 * 引数なしコンストラクタ
	 * @throws DAOException 独自DAO例外
	 */
	public EmployeeDAO() throws DAOException {
		super();
	}

	/**
	 * 従業員氏名あいまい検索と入社日範囲検索の複合検索
	 * @param  name 従業員氏名に含まれるキーワード
	 * @param  hiredAtFrom 入社日検索範囲開始日
	 * @param  hiredAtTo   入社日検索範囲終了日
	 * @return List<EmployeeBean> 従業員リスト
	 * @throws DAOException レコードの取得に失敗した場合
	 */
	public List<EmployeeBean> findByNameLikeAndHiredAtBetween(String name, String hiredAtFrom, String hiredAtTo) throws DAOException {
		// 0. 実行するSQLを生成
		EmployeeCriteria criteria = new EmployeeCriteria(name, hiredAtFrom, hiredAtTo); // 検索条件クラスをインスタンス化
		StringBuilder sql = new StringBuilder(); // SQL初期化
		sql.append(SQL_SELECT)
		   .append(criteria.createWherePhrase())
		   .append(SQL_ORDER_BY_ID);
		
		try (// 1. データベース接続オブジェクトを取得
			 Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);	
			 // 2. SQL実行オブジェクトを取得
			 PreparedStatement pstmt = con.prepareStatement(sql.toString());) {
			// 3. パラメータバインディング
			this.bind(pstmt, criteria);
			
			try (// 4. SQLの実行と結果セットの取得
				 ResultSet rs = pstmt.executeQuery();) {
				// 5. 結果セットを従業員リストに変換
				List<EmployeeBean> list = this.convertToList(rs);
				// 6. 従業員リストを返却
				return list;
			}
		} catch (SQLException e) {
			// スタックトレースに表示
			e.printStackTrace();
			// DAO例外をスロー
			throw new DAOException("レコードの取得に失敗しました。", e);
		}
	}

	/**
	 * 全件検索
	 * @return List<EmployeeBean> 従業員リスト
	 * @throws DAOException 独自DAO例外
	 */
	public List<EmployeeBean> findAll() throws DAOException {
		try (// 1. データベース接続オブジェクトを取得
			 Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			 // 2. SQL実行オブジェクトを取得
			 PreparedStatement pstmt = con.prepareStatement(SQL_FIND_ALL);
			 // 3. SQLの実行と結果セットの取得
			 ResultSet rs = pstmt.executeQuery();
			) {
			// 4. 結果セットを従業員リストに返還
			List<EmployeeBean> list = convertToList(rs);
			// 5. 従業員リストを返還
			return list;
		} catch (SQLException e) {
			// スタックトレースに表示
			e.printStackTrace();
			// DAO例外をスロー
			throw new DAOException("レコードの取得に失敗しました。", e);
		}
	}
	
	/**
	 * 結果セットを従業員リストに変換する
	 * @param  rs 結果セット
	 * @return List<EmployeeBean> 従業員リスト
	 * @throws SQLException SQL例外
	 */
	private List<EmployeeBean> convertToList(ResultSet rs) throws SQLException {
		List<EmployeeBean> list = new ArrayList<EmployeeBean>();
		while (rs.next()) {
			EmployeeBean bean = this.convertToBean(rs);
			list.add(bean);
		}
		return list;
	}

	/**
	 * 結果セットの１レコードを従業員インスタンスに変換する
	 * @param  rs 結果セットの1レコード
	 * @return EmployeeBean 従業員インスタンス
	 * @throws SQLException SQL例外
	 */
	private EmployeeBean convertToBean(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int departmentId = rs.getInt("department_id");
		String name = rs.getString("name");
		String phone = rs.getString("phone");
		LocalDate hiredAt = DateConverter.toLocalDate(rs.getDate("hired_at"));
		EmployeeBean bean = new EmployeeBean(id, departmentId, name, phone, hiredAt);
		return bean;
	}

	/**
	 * 検索条件に応じてパラメータバインディングを実行する
	 * @param pstmt         対象となるSQL実行オブジェクト
	 * @param criteria      検索条件オブジェクト
	 * @throws SQLException パラメータバインディングに失敗した場合
	 */
	private void bind(PreparedStatement pstmt, EmployeeCriteria criteria) throws SQLException {
		// プレースホルダのインデックスの初期化
		int index = 0;
		// 氏名あいまい検索用キーワードが指定されている場合
		if (Utils.hasValue(criteria.getName())) {
			index++;
			pstmt.setString(index, "%" + criteria.getName() + "%");
		}
		// 入社日範囲検索の開始日が指定されている場合
		if (Utils.hasValue(criteria.getHiredAtFrom())) {
			index++;
			pstmt.setDate(index, DateConverter.toSqlDate(DateConverter.toLocalDate(criteria.getHiredAtFrom())));
		}
		// 入社日範囲検索の終了日が指定されている場合
		if (Utils.hasValue(criteria.getHiredAtTo())) {
			index++;
			pstmt.setDate(index, DateConverter.toSqlDate(DateConverter.toLocalDate(criteria.getHiredAtTo())));
		}
		
	}
	
}
