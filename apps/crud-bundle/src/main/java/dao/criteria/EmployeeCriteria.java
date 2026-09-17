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
	private int placeholderPattern; // プレースホルダと検索条件のフィールドのパターン（0bxxx：3bitの2進数表現） 
	
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
		this.placeholderPattern = this.calcPlaceholderPatttern();
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

	public int getPlaceholderPattern() {
		return placeholderPattern;
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
	 * プレースホルダとフィールドのパターンを計算する
	 * @return プレースホルダとフィールドのパターン：パターンのリテラル表現は3bitの2進数
	 */
	private int calcPlaceholderPatttern() {
		if (Utils.hasValue(name) && Utils.hasValue(hiredAtFrom) && Utils.hasValue(hiredAtTo)) {
			return 0b111;
		} else if (Utils.hasValue(name) && Utils.hasValue(hiredAtFrom) && !Utils.hasValue(hiredAtTo)) {
			return 0b110;
		} else if (Utils.hasValue(name) && !Utils.hasValue(hiredAtFrom) && Utils.hasValue(hiredAtTo)) {
			return 0b101;
		} else if (Utils.hasValue(name) && !Utils.hasValue(hiredAtFrom) && !Utils.hasValue(hiredAtTo)) {
			return 0b100;
		} else if (!Utils.hasValue(name) && Utils.hasValue(hiredAtFrom) && Utils.hasValue(hiredAtTo)) {
			return 0b011;
		} else if (!Utils.hasValue(name) && Utils.hasValue(hiredAtFrom) && !Utils.hasValue(hiredAtTo)) {
			return 0b010;
		} else if (!Utils.hasValue(name) && !Utils.hasValue(hiredAtFrom) && Utils.hasValue(hiredAtTo)) {
			return 0b001;
		} else {
			return 0b000;
		}
	}

	/**
	 * プレースホルダと検索条件のフィールドのパターンをもとにWHERE句を生成する
	 * @param  criteria 検索条件オブジェクト
	 * @return WHERE句
	 */
	public String createWherePhrase() {
		String condition;
		switch (this.getPlaceholderPattern()) {
		case 0b111:
			condition = "WHERE name LIKE ? AND hired_at >= ? AND hired_at <= ?";
			break;
		case 0b110:
			condition = "WHERE name LIKE ? AND hired_at >= ?";
			break;
		case 0b101:
			condition = "WHERE name LIKE ? AND hired_at <= ?";
			break;
		case 0b100:
			condition = "WHERE name LIKE ?";
			break;
		case 0b011:
			condition = "WHERE hired_at >= ? AND hired_at <= ?";
			break;
		case 0b010:
			condition = "WHERE hired_at >= ?";
			break;
		case 0b001:
			condition = "WHERE hired_at <= ?";
			break;
		default:
			condition = "";
			break;
		}
		condition += " ";
		return condition;
	}

}
