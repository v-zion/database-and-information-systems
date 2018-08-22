package Home;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.sql.*;

/**
 * Servlet implementation class Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String url = "jdbc:postgresql://localhost:5400/postgres";
	public static final String user = "animesh";
	public static final String password = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Home() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void toHTML(ResultSet rSet, PrintWriter out) {
    			try {
    				ResultSetMetaData rSetMetaData = rSet.getMetaData();
    			

				out.print("<table border=\"1\">\n");
				out.print("    <tr>");

				for (int i=0; i<rSetMetaData.getColumnCount() - 1; i++)
					out.print("<th>" + rSetMetaData.getColumnName(i+1) + "</th>");
				out.print("<th>details</th>");
				out.print("</tr>\n");

				while (rSet.next()) {
					out.print("    <tr>");
					int i;
					for (i=0; i<rSetMetaData.getColumnCount() - 1; i++)
						out.print("<td>" + rSet.getString(i+1) + "</td>");
					Integer thread_id = rSet.getInt(i + 1);
					out.print("<td><form action=\"ConversationDetails\"><input type=\"hidden\" name=\"thread_id\" value=\"" + Integer.toString(thread_id) + "\">"
							+ "<input type=\"submit\" value=\"Details\"></form></td>");
					
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
			String uid = (String)session.getAttribute("uid");
			out.println("<body>");
			out.println("<form action=\"createConversation\" method=\"post\">");
			out.println("Enter ID of user: <input type=\"text\" name=\"uid\">");
			out.println("<input type=\"submit\" value=\"Create\"></form>");

			out.println("<br><form action = \"Logout\" method = \"post\">"
				+ "<input type = \"submit\" value = \"Logout\"></form>");
			out.println("</body>");

			try (Connection conn = DriverManager.getConnection(url, user, password)){
				conn.setAutoCommit(false);
				try (PreparedStatement stmt1 = conn.prepareStatement("with convs_with_name as (select * "
						+ "from (select uid1, uid2, thread_id, name1, name "
						+ "from (select uid1, uid2, thread_id, name "
						+ "from conversations join users on uid1 = uid) as cu1(uid1, uid2, thread_id, name1) "
						+ "join users on uid2 = uid) as cu2(uid1, uid2, thread_id, name1, name2)), "
						+ "convs as (select convs_with_name.thread_id as tid, *, rank() over (partition by (uid1, uid2) order by timestamp desc) as rank "
						+ "from convs_with_name left outer join posts on convs_with_name.thread_id = posts.thread_id "
						+ "where (uid1 = ? or uid2 = ?)), "
						+ "recent as (select "
						+ "name1, name2, timestamp, text, tid from convs where rank = 1), "
						+ "cranked as (select name1, name2, timestamp, text, tid, rank() over (order by timestamp desc nulls last) as crank from recent "
						+ "order by text) "
						+ "select case when name1 = ? then name2 else name1 end as name, text, timestamp, tid from cranked order by crank");
					PreparedStatement stmt2 = conn.prepareStatement("select name from users where uid = ?");){
					stmt2.setString(1, uid);
					ResultSet rs1 = stmt2.executeQuery();
					conn.commit();
					String name = "";
					if (rs1.next()) {
						name = rs1.getString(1);
					}					
					stmt1.setString(1, uid);
					stmt1.setString(2, uid);
					stmt1.setString(3, name);
					ResultSet rs = stmt1.executeQuery();
					conn.commit();
					toHTML(rs, out);
				} catch (Exception ex) {
					conn.rollback();
					throw ex;
				}
			} catch (Exception e){
				e.printStackTrace();
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