package NewMessage;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.sql.*;
import Home.Home;

/**
 * Servlet implementation class NewMessage
 */
@WebServlet("/NewMessage")
public class NewMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewMessage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect("Login");
		}
		else {
			String uid_self = (String)session.getAttribute("uid");
			String tid = request.getParameter("thread_id");
			if (tid == null) {
				out.println("<body>No thread_id found");
				out.println("<br><a href=\"Home\">Go back to Home</a></body>");
			}
			else {
				Integer thread_id = Integer.parseInt(tid);
				String msg = request.getParameter("msg");
				try (Connection conn = DriverManager.getConnection(Home.url, Home.user, Home.password)){
					conn.setAutoCommit(false);
					try (PreparedStatement stmt = conn.prepareStatement("insert into posts (thread_id, uid, timestamp, text) "
							+ "values (?, ?, current_timestamp, ?)")){
						stmt.setInt(1, thread_id);
						stmt.setString(2, uid_self);
						stmt.setString(3, msg);
						stmt.executeUpdate();
						conn.commit();
						response.sendRedirect("ConversationDetails?thread_id=" + Integer.toString(thread_id));
						
					} catch (Exception ex) {
						conn.rollback();
						throw ex;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
