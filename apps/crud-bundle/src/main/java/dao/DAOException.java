package dao;

/**
 * DAOで発生した例外を統一的に処理するための独自例外
 */
public class DAOException extends Exception {

	/**
	 * コンストラクタ
	 * @param message 例外メッセージ
	 * @param cause   下位の例外
	 */
	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}

}
