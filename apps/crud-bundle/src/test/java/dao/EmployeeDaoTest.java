package dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
		DbUnitTestHelper.OPERATION_INIT("employees");
	}

	@AfterEach
	void tearDown() throws Exception {
		// テストの後始末
		DbUnitTestHelper.OPERATION_INIT("employees");
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
