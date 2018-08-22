package ConversationDetails;

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
 * Servlet implementation class ConversationDetails
 */
@WebServlet("/ConversationDetails")
public class ConversationDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConversationDetails() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void toHTML(ResultSet rSet, PrintWriter out) {
		try {
			ResultSetMetaData rSetMetaData = rSet.getMetaData();
		

		out.print("<table>\n");
		out.print("    <tr>");

		for (int i=0; i<rSetMetaData.getColumnCount() - 1; i++)
			out.print("<th>" + rSetMetaData.getColumnName(i+1) + "</th>");
		out.print("<th>Posted by</th>");
		out.print("</tr>\n");

		while (rSet.next()) {
			out.print("    <tr>");
			for (int i=0; i<rSetMetaData.getColumnCount(); i++)
				out.print("<td>" + rSet.getString(i+1) + "</td>");
			out.print("</tr>\n");
		}

		out.print("</table>\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect("Login");
		}
		else {
			String uid_self = (String)session.getAttribute("uid");
			String tid = request.getParameter("thread_id");
			if (tid == null) {
				out.println("<body>No thread_id found in request");
				out.println("<a href=\"Home\">Go back to Home</a>");
			}
			else {
				Integer thread_id = Integer.parseInt(tid);
				try (Connection conn = DriverManager.getConnection(Home.url, Home.user, Home.password)){
					conn.setAutoCommit(false);
					try (PreparedStatement stmt1 = conn.prepareStatement("select * from conversations "
							+ "where thread_id = ? and (uid1 = ? or uid2 = ?)");
						PreparedStatement stmt2 = conn.prepareStatement("select text, name from posts natural join users where thread_id = ? "
								+ "order by timestamp desc");){
						stmt1.setInt(1, thread_id);
						stmt1.setString(2, uid_self);
						stmt1.setString(3, uid_self);
						ResultSet rs1 = stmt1.executeQuery();
						conn.commit();
						if (!rs1.next()) {
							out.println("<body>Sorry you are not allowed to view this conversation");
							out.println("<br><a href=\"Home\">Go Back</a></body>");
						}
						else {
							stmt2.setInt(1, thread_id);
							ResultSet rs2 = stmt2.executeQuery();
							conn.commit();
							out.println("<body>");
							toHTML(rs2, out);
							out.println("<br>New message: <form action=\"NewMessage\" method=\"post\">"
									+ "<br>Enter the message: <input type=\"text\" name=\"msg\">"
									+ "<input type=\"hidden\" name=\"thread_id\" value=\"" + Integer.toString(thread_id) + "\">"
									+ "<br><input type=\"submit\"></form>");
							out.println("<br><br><a href=\"Home\">Go back to Home</a>");
							out.println("</body>");
						}
						
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
