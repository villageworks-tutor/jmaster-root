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

/**
 * employeesテーブルアクセスするDAOクラス
 */
public class EmployeeDAO extends BaseDAO {

	private static final String SQL_FIND_ALL = "SELECT * FROM employees ORDER BY id";
	private static final String SQL_FIND_BY_NAME_LIKE = "SELECT * FROM employees WHERE name LIKE ? ORDER BY id";
	
	/**
	 * 引数なしコンストラクタ
	 * @throws DAOException 独自DAO例外
	 */
	public EmployeeDAO() throws DAOException {
		super();
	}

	/**
	 * 入社日範囲検索
	 * @param hiredAtFrom 入社日検索範囲開始日
	 * @param hiredAtTo   入社日検索範囲終了日
	 * @return List<EmployeeBean> 従業員リスト
	 * @throws DAOException レコードの取得に失敗した場合
	 */
	public List<EmployeeBean> findByHiredAtBetween(String hiredAtFrom, String hiredAtTo) throws DAOException {
		// 0. 実行するSQLの設定
		String sql = "SELECT * FROM employees";
		if (hasValue(hiredAtFrom) && hasValue(hiredAtTo)) {
			sql += " WHERE hired_at BETWEEN ? AND ? ";
		} else if (hasValue(hiredAtFrom) && !hasValue(hiredAtTo)) {
			sql += " WHERE hired_at >= ? ";
		} else if (!hasValue(hiredAtFrom) && hasValue(hiredAtTo)) {
			sql += " WHERE hired_at <= ? ";
		} else {
			sql += " ";
		}
		sql += "ORDER BY id;";
		
		try (// 1. データベース接続オブジェクトを取得
 			 Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			 // 2. SQL実行オブジェクトを取得
			 PreparedStatement pstmt = con.prepareStatement(sql);) {
			// 3. パラメータバインディング
			if (hasValue(hiredAtFrom) && hasValue(hiredAtTo)) {
				pstmt.setDate(1, DateConverter.toSqlDate(DateConverter.toLocalDate(hiredAtFrom)));
				pstmt.setDate(2, DateConverter.toSqlDate(DateConverter.toLocalDate(hiredAtTo)));
			} else if (hasValue(hiredAtFrom) && !hasValue(hiredAtTo)) {
				pstmt.setDate(1, DateConverter.toSqlDate(DateConverter.toLocalDate(hiredAtFrom)));
			} else if (!hasValue(hiredAtFrom) && hasValue(hiredAtTo)) {
				pstmt.setDate(1,DateConverter.toSqlDate(DateConverter.toLocalDate(hiredAtTo)));
			}
			
			try (// 4. SQLの実行と結果セットの取得
				 ResultSet rs = pstmt.executeQuery();) {
				// 5. 結果セットを従業員リストに返還
				List<EmployeeBean> list = convertToList(rs);
				// 6. 従業員リストを返還
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
	 * 従業員指名あいまい検索
	 * @param  name 従業員氏名に含まれるキーワード
	 * @return List<EmployeeBean> 従業員リスト
	 * @throws DAOException レコードの取得に失敗した場合
	 */
	public List<EmployeeBean> findByNameLike(String name) throws DAOException {
		try (// 1. データベース接続オブジェクトを取得
			 Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			 // 2. SQL実行オブジェクトを取得
			 PreparedStatement pstmt = con.prepareStatement(SQL_FIND_BY_NAME_LIKE);) {
			// 3. パラメータバインディング
			pstmt.setString(1,"%" +  name + "%");
			
			try (// 4. SQL実行オブジェクトを取得
				 ResultSet rs = pstmt.executeQuery();) {
				// 5. 結果セットを従業員リストに返還
				List<EmployeeBean> list = convertToList(rs);
				// 6. 従業員リストを返還
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
	
	private boolean hasValue(String target) {
		return (target != null && !target.isEmpty());
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

}
