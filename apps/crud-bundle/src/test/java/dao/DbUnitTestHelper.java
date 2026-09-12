package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * CRUD操作単体テスト用ヘルパークラス
 */
public abstract class DbUnitTestHelper {

	/**
	 * クラス変数：データベース接続オブジェクト
	 */
	protected static final String JDBC_DRIVER = "org.postgresql.Driver";
	protected static final String DB_URL      = "jdbc:postgresql:jmaster_crud_db";
	protected static final String DB_USER     = "student";
	protected static final String BD_PASSWORD = "himitu";
	
	static final String SQL_TRUNCATE = "TRUNCATE TABLE @table_name RESTART IDENTITY CASCADE";
	
	/** テスト補助変数 */
	static Connection testConnection; // テストデータ管理用データベース接続オブジェクト
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// JDBCドライバの読込み
		Class.forName(JDBC_DRIVER);
		// テスト用データベース接続オブジェクトを取得
		testConnection = DriverManager.getConnection(DB_URL, DB_USER, BD_PASSWORD);
	}
	
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// テスト用データベース接続オブジェクトの解放
		testConnection.close();
	}

	/**
	 * テーブルを初期化する：自動付番のフィールドも初期化される
	 * @param  tableName 対象となるテーブル名
	 * @throws Exception テーブルの初期化に失敗した場合
	 */
	public static void OPERATION_INIT(String tableName) throws Exception {
		String sql = SQL_TRUNCATE.replace("@table_name", tableName);
		try (PreparedStatement pstmt = testConnection.prepareStatement(sql);) {
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * テストデータを復元する：復元するテストデータは継承クラスで定義する。
	 * @throws Exception テストデータの復元に失敗した場合
	 */
	protected abstract void restore() throws Exception;
	
	/**
	 * テーブルのレコードをす復元する：復元するレコードは継承クラスで定義する
	 * @param  tableName 対象となるテーブル名
	 * @throws Exception テストデータの復元に失敗した場合
	 */
	public final void OPERATION_RESTORE(String tableName) throws Exception {
		OPERATION_INIT(tableName);
		restore();
	}

}
