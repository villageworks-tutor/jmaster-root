package common;

/**
 * 共通処理用ユーティリティクラス
 */
public class Utils {
	
	/**
	 * 文字列変数にリテラル値が格納されているかどうかを判定する
	 * @param  target  判定対象の文字列変数
	 * @return boolean 対象の文字列変数にリテラル値が格納されている場合はtrue
	 *                 それ以外（nullまたは空文字列「」が格納されている場合）はfalse
	 */
	public static boolean hasValue(String target) {
		return (target != null && !target.isEmpty());
	}
	
}
