package dao.criteria;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EmployeeCriteriaTest {

	/**	テスト対象クラス：system under test */
	EmployeeCriteria sut;

	@Nested
	@DisplayName("EmployeeCriteria#createWherePhrase()メソッドのテストクラス")
	class CreateWherePhraseTest {
		@ParameterizedTest
		@MethodSource("createWherePhraseProvider")
		void プレースホルダ付き検索条件を生成できる(EmployeeCriteria target, String expected) {
			// setup & execute
			sut = target;
			String actual = sut.createWherePhrase();
			// verify
			assertEquals(expected, actual);
		}
		
		/**
		 * placeholdernをもとにプレースホルダ付き検索条件のWHERE句を生成するテストのテスト用パラメータを提供する
		 * @return テストパラメータ
		 *         テストパラメータは以下の項目を返す
		 *         	・検索条件パターン（2進数表示）
		 *         	・プレースホルダ付き検索条件のWHERE句の期待値
		 */
		static Stream<Arguments> createWherePhraseProvider() {
			return
				Stream.of(
					  // 氏名、入社日範囲ともに指定された場合
					  Arguments.of(
						  new EmployeeCriteria("滝本 順三", "2000-4-1", "2019-3-31")
						, "WHERE name LIKE ? AND hired_at >= ? AND hired_at <= ?"
					  )
					  // 氏名、入社日範囲開始日が指定された場合
					, Arguments.of(
						  new EmployeeCriteria("尚", "2000-04-01", "")
						, "WHERE name LIKE ? AND hired_at >= ?"
					  )
					  // 氏名、入社日範囲最終日が指定された場合
					, Arguments.of(
						  new EmployeeCriteria("本", "", "1991-4-1")
						, "WHERE name LIKE ? AND hired_at <= ?"
					  )
					  // 氏名だけが指定された場合
					, Arguments.of(
						  new EmployeeCriteria("岡田", null, "")
						, "WHERE name LIKE ?"
					  )
					  // 入社日範囲検索開始日だけが指定された場合
					, Arguments.of(
						  new EmployeeCriteria("", "2008-4-1", "2009-3-31")
						, "WHERE hired_at >= ? AND hired_at <= ?"
					  )
					  // 入社日範囲検索終了日だけが指定された場合
					, Arguments.of(
						  new EmployeeCriteria("", "2019-4-1", "")
						, "WHERE hired_at >= ?"
					  )
					  // 入社日範囲検索の開始日と終了日が指定された場合
					, Arguments.of(
						  new EmployeeCriteria("", "", "1991-4-1")
						, "WHERE hired_at <= ?"
					  )
					  // 引数なしでインスタンス化した場合
					, Arguments.of(
						  new EmployeeCriteria("", "", "")
						, ""
					  )
				);
		}
	}
	
	@Nested
	@DisplayName("EmployeeCriteria#calcPlaceholderPattern()メソッドのテストクラス")
	class CalcPlaceholderPatternTest {
		@ParameterizedTest
		@MethodSource("calcPlaceholderPatternProvider")
		void プレースホルダ付きSQLを生成するためのplaceholdernのパターンを計算できる(EmployeeCriteria target, int expected) {
			// setup & execute
			sut = target;
			int actual = sut.getPlaceholderPattern();
			// verify
			assertEquals(expected, actual);
		}
		
		/**
		 * プレースホルダ付きSQLを生成するためのplaceholdernのパターンを計算するテストのテスト用パラメータを提供する
		 * @return テストパラメータ
		 *         テストパラメータは以下の項目を返す
		 *         	・検索条件オブジェクト
		 *         	・検索結果の期待値
		 */
		static Stream<Arguments> calcPlaceholderPatternProvider() {
			return
				Stream.of(
					  // 氏名、入社日範囲ともに指定された場合
					  Arguments.of(new EmployeeCriteria("滝本 順三", "2000-4-1", "2019-3-31"), 0b111)
					  // 氏名、入社日範囲開始日が指定された場合
					, Arguments.of(new EmployeeCriteria("尚", "2000-04-01", ""), 0b110)
					  // 氏名、入社日範囲最終日が指定された場合
					, Arguments.of(new EmployeeCriteria("本", "", "1991-4-1"), 0b101)
					  // 氏名だけが指定された場合
					, Arguments.of(new EmployeeCriteria("岡田", null, ""), 0b100)
					  // 入社日範囲検索開始日だけが指定された場合
					, Arguments.of(new EmployeeCriteria("", "2008-4-1", "2009-3-31"), 0b011)
					  // 入社日範囲検索終了日だけが指定された場合
					, Arguments.of(new EmployeeCriteria("", "2019-4-1", ""), 0b010)
					  // 入社日範囲検索の開始日と終了日が指定された場合
					, Arguments.of(new EmployeeCriteria("", "", "1991-4-1"), 0b001)
					  // 引数なしでインスタンス化した場合
					, Arguments.of(new EmployeeCriteria(), 0b000)
				);
		}
	}
	
	@Nested
	@DisplayName("EmployeeCriteria#constructorのテストクラス")
	class ConstructorTest {
		@Test
		void EmployeeCriteriaをインスタンス化できる() {
			// setup
			String name = "車寅次郎";
			String hiredAtFrom = "2025-04-01";
			String hiredAtTo = "2026-03-31";
			Map<String, String> expected = new HashMap<String, String>();
			expected.put("name", name);
			expected.put("hiredAtFrom", hiredAtFrom);
			expected.put("hiredAtTo", hiredAtTo);
			// execute
			EmployeeCriteria actual = new EmployeeCriteria(name, hiredAtFrom, hiredAtTo);
			// verify
			assertEmployeeCriteriaInstance(expected, actual);
		}
		
		/**
		 * インスタンス化を確認する
		 * @param expected フィールド名をキーの期待値マップMap<String, String>
		 * @param actual   インスタンス化した実行値
		 */
		private void assertEmployeeCriteriaInstance(Map<String, String> expected, EmployeeCriteria actual) {
			assertNotNull(actual);
			assertEquals(expected.get("name"), actual.getName());
			assertEquals(expected.get("hiredAtFrom"), actual.getHiredAtFrom());
			assertEquals(expected.get("hiredAtTo"), actual.getHiredAtTo());
		}
	}
	
	
	
}
