package dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.villageworks.app.DateConverter;

import bean.EmployeeBean;

class EmployeeDaoTest extends DbUnitTestHelper {

	/** テスト対象クラス：system under test */
	private EmployeeDAO sut;
	
	private static final String SQL_INSERT_INTO_EMPLOYEES = 
			"""
				INSERT INTO employees (department_id, name, phone, hired_at) VALUES (?, ?, ?, ?)
			""";
	
	private static final List<EmployeeBean> sampleEmployees = 
			List.of(
				  new EmployeeBean(1, 1, "羽生 章洋", "00800", "1998-12-17")
				, new EmployeeBean(2, 1, "釜本 喜美子", "01600", "1991-2-20")
				, new EmployeeBean(3, 2, "安部 弘江", "01250", "1991-2-22")
				, new EmployeeBean(4, 2, "松村 秀和", "02975", "1991-4-2")
				, new EmployeeBean(5, 3, "萩原 恵理子", "01251", "2008-9-28")
				, new EmployeeBean(6, 3, "岡田 奈緒子", "02850", "2007-5-1")
				, new EmployeeBean(7, 3, "井上 尚志", "02450", "2000-11-15")
				, new EmployeeBean(8, 4, "西口 麻衣子", "03000", "2008-12-3")
				, new EmployeeBean(9, 4, "滝本 順三", "05000", "2004-12-18")
				, new EmployeeBean(10, 4, "工藤 新一", "1500", "2009-04-01")
				, new EmployeeBean(11, 5, "岡田 光太郎", "1501", "2019-06-11")
			);
	
	@BeforeEach
	void setUp() throws Exception {
		// テスト対象クラスのインスタンス化
		sut = new EmployeeDAO();
		// テストの準備
		OPERATION_INIT("employees");
	}

	@AfterEach
	void tearDown() throws Exception {
		// テストの後始末
		OPERATION_RESTORE("employees");
	}
	
	@Nested
	@DisplayName("EmployeeDAO#findByNameLikeAndHiredAtBetweenメソッドのテストクラス")
	class FindByNameLikeAndHiredAtBetweenTest {
		@BeforeEach
		void setUp() throws Exception {
			restore();
		}
		
		@ParameterizedTest
		@MethodSource("findByNameAndHiredAtBetweenProvider")
		void 従業員氏名のあいまい検索と入社日範囲検索の複合検索ができる(String name, String hiredAtFrom, String hiredAtTo, List<EmployeeBean> expected) throws Exception {
			// setup & execute
			List<EmployeeBean> actual = sut.findByNameLikeAndHiredAtBetween(name, hiredAtFrom, hiredAtTo);
			// verify
			assertEmployees(expected, actual);
		}
		
		/**
		 * 従業員氏名のあいまい検索と入社日範囲検索ができるテスト用のテストパラメータを提供する
		 * @return テストパラメータ
		 *         テストパラメータは以下の項目を返す
		 *         	・検索キーワード
		 *         	・入社日範囲検索開始日
		 *         	・入社日範囲検索終了日
		 *         	・検索結果の期待値（Liist<EmployeeBean>）
		 */
		static Stream<Arguments> findByNameAndHiredAtBetweenProvider() {
			return
				Stream.of(
					// すべてのパラメータが指定されなかった場合
					Arguments.of(
						"", 
						null, 
						"",
						sampleEmployees
					),
					
					// 入社日範囲終了日が指定された場合
					Arguments.of(
						"", 
						"", 
						"1991-4-1",
						List.of(
							  new EmployeeBean(2, 1, "釜本 喜美子", "01600", "1991-2-20")
							, new EmployeeBean(3, 2, "安部 弘江", "01250", "1991-2-22")
					    )
					),
					
					// 入社日範囲開始日が指定された場合
					Arguments.of(
						"", 
						"2019-4-1", 
						"",
						List.of(
							new EmployeeBean(11, 5, "岡田 光太郎", "1501", "2019-06-11")
					    )
					),
					
					// 入社日範囲の開始日と終了日が指定された場合
					Arguments.of(
						"",
						"2008-4-1",
						"2009-3-31",
						List.of(
							  new EmployeeBean(5, 3, "萩原 恵理子", "01251", "2008-9-28")
							, new EmployeeBean(8, 4, "西口 麻衣子", "03000", "2008-12-3")
						 )

					),
					// 氏名が指定された場合
					Arguments.of(
						"岡田",
						null,
						null,
						List.of(
							  new EmployeeBean(6, 3, "岡田 奈緒子", "02850", "2007-5-1")
							, new EmployeeBean(11, 5, "岡田 光太郎", "1501", "2019-06-11")
						)
					),
					// 氏名と入社日範囲終了日のパラメータが指定された場合
					Arguments.of(
						"本",
						null,
						"1991-4-1",
						List.of(
							new EmployeeBean(2, 1, "釜本 喜美子", "01600", "1991-2-20")
						)
					),
					// 氏名と入社日範囲開始日のパラメータが指定された場合
					Arguments.of(
						"尚",
						"2000-4-1",
						"",
						List.of(
							new EmployeeBean(7, 3, "井上 尚志", "02450", "2000-11-15")
						)
					),
					// すべてのパラメータが指定された場合
					Arguments.of(
						"滝本 順三", 
						"2000-4-1",
						"2019-3-31",
						List.of(
							new EmployeeBean(9, 4, "滝本 順三", "05000", "2004-12-18")
						)
					)
				);
		}
	}
	
	@Nested
	@DisplayName("EmployeeDAO#finsByHiredAtBetweenメソッドのテストクラス")
	class FindByHiredAtBetweenTest {
		@BeforeEach
		void setUp() throws Exception {
			restore();
		}
		
		@ParameterizedTest
		@MethodSource("findByHiredAtBetweenProvider")
		void 入社日の範囲検索ができる(String hiredAtFrom, String hiredAtTo, List<EmployeeBean> expeected) throws Exception {
			// setup & execute
			List<EmployeeBean> actual = sut.findByHiredAtBetween(hiredAtFrom, hiredAtTo);
			// verify
			assertEmployees(expeected, actual);
		}
		
		static Stream<Arguments> findByHiredAtBetweenProvider() {
			return
				Stream.of(
					// 入社範囲を指定しない場合は全件検索
					Arguments.of(
						"",
						null,
						sampleEmployees
					),
					// 「1991-4-1」以前に入社した従業員は従業員番号が「2」と「3」の従業員である
					Arguments.of(
						"",
						"1991-4-1",
						List.of(
							  new EmployeeBean(2, 1, "釜本 喜美子", "01600", "1991-2-20")
							, new EmployeeBean(3, 2, "安部 弘江", "01250", "1991-2-22")
						)
					),
					// 「2019-4-1」以降に入社した従業員は従業員番号が「11」の従業員である
					Arguments.of(
						"2019-4-1",
						"",
						List.of(
							new EmployeeBean(11, 5, "岡田 光太郎", "1501", "2019-06-11")
						)
					),
					// 2008年度に入社した従業員は従業員番号が「5」と「8」の従業員である
					Arguments.of(
						"2008-4-1",
						"2009-3-31",
						List.of(
							  new EmployeeBean(5, 3, "萩原 恵理子", "01251", "2008-9-28")
							, new EmployeeBean(8, 4, "西口 麻衣子", "03000", "2008-12-3")
						)
					)
				);
		}
		
	}
	
	@Nested
	@DisplayName("EmployeeDAO#findByNameLikeメソッドのテストクラス")
	class FindByNameLikeTest {
		@BeforeEach
		void setUp() throws Exception {
			restore();
		}
		
		@ParameterizedTest
		@MethodSource("findByNameLikeProvider")
		void 従業員氏名のあいまい検索ができる(String target, List<EmployeeBean> expected) throws Exception {
			// setup & execute
			List<EmployeeBean> actual = sut.findByNameLike(target);
			// verify
			assertEmployees(expected, actual);
		}
		
		/**
		 * 従業員氏名のあいまい検索ができるテスト用のテストパラメータを提供する
		 * @return テストパラメータ
		 *         テストパラメータは以下の項目を返す
		 *         	・検索キーワード
		 *         	・検索結果の期待値（Liist<EmployeeBean>）
		 */
		static Stream<Arguments> findByNameLikeProvider() {
			return
				Stream.of(
					// キーワードが空文字列「」の場合のテスト：キーワードが空文字「」で全件取得できる
					Arguments.of(
						""
						, sampleEmployees
					),
						
					// キーワードが従業員氏名に含まれない場合のテスト：キーワード「￥」で空リストを取得する
					Arguments.of(
						"￥"
						, List.of()
					),
					
					// 後方一致検索のテスト：キーワード「子」で４件取得できる
					Arguments.of(
						"子"
						, List.of(
							  new EmployeeBean(2, 1, "釜本 喜美子", "01600", "1991-2-20")
							, new EmployeeBean(5, 3, "萩原 恵理子", "01251", "2008-9-28")
							, new EmployeeBean(6, 3, "岡田 奈緒子", "02850", "2007-5-1")
							, new EmployeeBean(8, 4, "西口 麻衣子", "03000", "2008-12-3")
						)
					),
					
					// 中間一致検索のテスト：キーワード「本」で２件取得できる
					Arguments.of(
						"本"
						, List.of(
							  new EmployeeBean(2, 1, "釜本 喜美子", "01600", "1991-2-20")
							, new EmployeeBean(9, 4, "滝本 順三", "05000", "2004-12-18")
						)
					),
					
					// 前方一致検索のテスト：キーワード「岡田」で２件取得できる
					Arguments.of(
						  "岡田"
						, List.of(
							  new EmployeeBean(6, 3, "岡田 奈緒子", "02850", "2007-5-1")
							, new EmployeeBean(11, 5, "岡田 光太郎", "1501", "2019-06-11")
						)
					)
				);
		}
	}

	@Nested
	@DisplayName("EmployeeDAO#findAllメソッドのテストクラス")
	class FindAllTest {
		@Test
		void テーブルの全件を取得できる() throws Throwable {
			// setup
			for (EmployeeBean employee : sampleEmployees) {
				try (PreparedStatement pstmt = testConnection.prepareStatement(SQL_INSERT_INTO_EMPLOYEES);) {
					pstmt.setInt(1, employee.getDepartmentId());
					pstmt.setString(2, employee.getName());
					pstmt.setString(3, employee.getPhone());
					pstmt.setDate(4, DateConverter.toSqlDate(employee.getHiredAt()));
					pstmt.executeUpdate();
				}
			}
			List<EmployeeBean> expectedList = sampleEmployees;
			// execute
			List<EmployeeBean> actualList = sut.findAll();
			// verify
			assertEmployees(expectedList, actualList);
		}
		
		@Test
		void テーブルに登録されていない場合は空リストを取得する() throws Exception {
			// setup
			int expected = 0;
			// execute
			List<EmployeeBean> actual = sut.findAll();
			// verify
			assertEquals(expected, actual.size());
		}
	}
	
	/**
	 * テスト前サンプルレコードを復元する
	 * @throws Exception
	 */
	
	@Override
	protected void restore() throws Exception {
		for (EmployeeBean employee : sampleEmployees) {
			try (PreparedStatement pstmt = testConnection.prepareStatement(SQL_INSERT_INTO_EMPLOYEES);) {
				pstmt.setInt(1, employee.getDepartmentId());
				pstmt.setString(2, employee.getName());
				pstmt.setString(3, employee.getPhone());
				pstmt.setDate(4, DateConverter.toSqlDate(employee.getHiredAt()));
				pstmt.executeUpdate();
			}
		}
	}
	
	/**
	 * 期待値と実行値を比較する
	 * @param expectedList 期待値のList<EmployeeBean>
	 * @param actualList   実行値のList<EmployeeBean>
	 */
	private void assertEmployees(List<EmployeeBean> expectedList, List<EmployeeBean> actualList) {
		// 期待値と実行値のサイズの比較
		assertEquals(expectedList.size(), actualList.size());
		// 期待値のすべての要素と実効値のすべての要素の比較
		for (int i = 0; i < expectedList.size(); ++i) {
			EmployeeBean actual = actualList.get(i);
			EmployeeBean expected = expectedList.get(i);
			assertEquals(expected.toString(), actual.toString());
		}
		
	}

}
