package dao;

/**
 * すべてのDAOクラスが継承する基底クラス
 */
public class BaseDAO {

	/**
	 * クラス定数：データベース接続情報文字列
	 */
	protected static final String JDBC_DRIVER = "org.postgresql.Driver";
	protected static final String DB_URL      = "jdbc:postgresql:jmaster_crud_db";
	protected static final String DB_USER     = "student";
	protected static final String DB_PASSWORD = "himitu";
	
	/**
	 * コンストラクタ
	 * @throws DAOException 独自DAO例外
	 */
	public BaseDAO() throws DAOException {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			// スタックトレースに表示
			e.printStackTrace();
			// DAOExceptionをスロー
			throw new DAOException("JDBCの読込みに失敗しました。", e);
		}
	}
	
}
