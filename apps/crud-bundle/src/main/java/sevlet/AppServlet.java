package sevlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.EmployeeBean;
import dao.DAOException;
import dao.EmployeeDAO;

/**
 * Servlet implementation class AppServlet
 */
@WebServlet("/AppServlet")
public class AppServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String JSP_DIR = "/WEB-INF/views";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1. リクエストパラメータの文字コードを設定
		request.setCharacterEncoding("utf-8");
		// 2. リクエストパラメータからactionキーを取得
		String action = request.getParameter("action");
		
		try {
			// 3. 取得したactionキーの値によって処理を分岐
			EmployeeDAO dao = new EmployeeDAO(); // EmployeeDAOのインスタンス化
			String nextURL = "";
			if (action == null || action.isEmpty()) {
				// 3-1. actionキーが未送信または空文字列の場合：従業員の全件検索表示
				// 3-1.1 すべての従業員を取得
				List<EmployeeBean> list = dao.findAll();
				// 3-1.2 従業員リストをスコープに登録
				request.setAttribute("employees", list);
				// 3-1.3 遷移先画面URLの設定
				nextURL = JSP_DIR + "/employees.jsp";
			} else if (action.equals("search")) {
				// 3-2. actionキーが「search」の場合：氏名あいまい検索
				// 3-2.1 リクエストパラメータを取得
				String name = request.getParameter("name");
				// 3-2.2 氏名あいまい検索を実行
				List<EmployeeBean> list = dao.findByNameLike(name);
				// 3-2.3 従業員リストと検索条件をスコープに登録
				request.setAttribute("employees", list);
				request.setAttribute("name", name);
				// 3-2.4 遷移先画面URLの設定
				nextURL = JSP_DIR + "/employees.jsp";
			}
			// 4. 画面遷移
			this.gotoPage(request, response, nextURL);
		} catch (DAOException e) {
			// スタックトレースを表示
			e.printStackTrace();
			// ServletExceptionをスロー
			throw new ServletException("内部エラーが発生しました。", e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * 指定されたURLにフォワードする
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @param nextPage 遷移先URL
	 * @throws ServletException
	 * @throws IOException
	 */
	private void gotoPage(HttpServletRequest request, HttpServletResponse response, String nextPage) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(nextPage);
		dispatcher.forward(request, response);
	}

}
