package sevlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

//        response.setContentType("text/html; charset=UTF-8");
//        response.getWriter().println("<html><body>");
//        response.getWriter().println("Hello Servlet");
//        response.getWriter().println("</body></html>");
    }
}