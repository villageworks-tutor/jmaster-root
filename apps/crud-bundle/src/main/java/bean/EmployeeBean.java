package bean;

import java.time.LocalDate;

import org.villageworks.app.DateConverter;

/**
 * employeesテーブルの１レコードを管理するJavaBean
 */
public class EmployeeBean extends BaseBean {
	
	/**
	 * フィールド
	 */
	private int id;            // 従業員因番号
	private int departmentId;  // 部署番号
	private String name;       // 従業員氏名
	private String phone;      // 内線番号
	private LocalDate hiredAt; // 入社日
	
	/**
	 * 引数なしコンストラクタ
	 */
	public EmployeeBean() {}

	/**
	 * コンストラクタ
	 * @param departmentId 部署番号
	 * @param name         従業員氏名
	 * @param phone        内線番号
	 * @param hiredAt      入社日
	 */
	public EmployeeBean(int departmentId, String name, String phone, LocalDate hiredAt) {
		this.departmentId = departmentId;
		this.name = name;
		this.phone = phone;
		this.hiredAt = hiredAt;
	}
	
	/**
	 * コンストラクタ
	 * @param departmentId 部署番号
	 * @param name         従業員氏名
	 * @param phone        内線番号
	 * @param hiredAt      入社日
	 */
	public EmployeeBean(int departmentId, String name, String phone, String hiredAt) {
		this(departmentId, name, phone, DateConverter.toLocalDate(hiredAt));
	}

	/**
	 * コンストラクタ
	 * @param id           従業員番号
	 * @param departmentId 部署番号
	 * @param name         従業員氏名
	 * @param phone        内線番号
	 * @param hiredAt      入社日
	 */
	public EmployeeBean(int id, int departmentId, String name, String phone, LocalDate hiredAt) {
		this(departmentId, name, phone, hiredAt);
		this.id = id;
	}
	
	/**
	 * コンストラクタ
	 * @param id           従業員番号
	 * @param departmentId 部署番号
	 * @param name         従業員氏名
	 * @param phone        内線番号
	 * @param hiredAt      入社日
	 */
	public EmployeeBean(int id, int departmentId, String name, String phone, String hiredAt) {
		this(id, departmentId, name, phone, DateConverter.toLocalDate(hiredAt));
	}

	public int getId() {
		return id;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public LocalDate getHiredAt() {
		return hiredAt;
	}
	
	public String getHiredAtString() {
		return DateConverter.toDateString(hiredAt);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeBean [")
			   .append("id=").append(id)
			   .append(", departmentId=").append(departmentId)
			   .append(", name=").append(name)
			   .append(", phone=").append(phone)
			   .append(", hiredAt=").append(hiredAt)
			   .append("]");
		return builder.toString();
	}
	
}
