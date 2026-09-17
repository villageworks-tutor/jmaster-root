package dao.criteria;

import common.Utils;

/**
 * employeesテーブルに関する検索条件クラス
 */
public class EmployeeCriteria {

	/**
	 * フィールド
	 */
	private String name;            // 氏名あいまい検索用キーワード
	private String hiredAtFrom;     // 入社日範囲検索・範囲開始日
	private String hiredAtTo;       // 入社日範囲検索・範囲終了日
	
	/**
	 * 引数なしコンストラクタ
	 */
	public EmployeeCriteria() {}

	/**
	 * コンストラクタ
	 * @param name        氏名あいまい検索用キーワード
	 * @param hiredAtFrom 入社日範囲検索・範囲開始日
	 * @param hiredAtTo   入社日範囲検索・範囲終了日
	 */
	public EmployeeCriteria(String name, String hiredAtFrom, String hiredAtTo) {
		this.name = name;
		this.hiredAtFrom = hiredAtFrom;
		this.hiredAtTo = hiredAtTo;
	}

	public String getName() {
		return name;
	}

	public String getHiredAtFrom() {
		return hiredAtFrom;
	}

	public String getHiredAtTo() {
		return hiredAtTo;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeCriteria [")
			   .append("name=").append(name).append(", ")
			   .append("hiredAtFrom=").append(hiredAtFrom).append(", ")
			   .append("hiredAtTo=").append(hiredAtTo)
			   .append("]");
		return builder.toString();
	}

	/**
	 * 検索条件によってWHERE句を生成する
	 * @param  criteria 検索条件オブジェクト
	 * @return WHERE句
	 */
	public String createWherePhrase() {
		// WHERE句を初期化
		String where = "WHERE 1 = 1 ";
		// 氏名あいまい検索の検索キーワードが指定されている場合
		if (Utils.hasValue(this.name)) {
			where += "AND name LIKE ? ";
		}
		// 入社日範囲検索の開始日が指定されている場合
		if (Utils.hasValue(this.hiredAtFrom)) {
			where += "AND hired_at >= ? ";
		}
		// 入社日範囲検索の終了日が指定されている場合
		if (Utils.hasValue(this.hiredAtTo)) {
			where += "AND hired_at <= ? ";
		}
		// WHERE句を返却
		return where;
		
	}

}
