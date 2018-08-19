package displayGrades;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.io.PrintWriter;
import Home.Home;

/**
 * Servlet implementation class displayGrades
 */
@WebServlet("/displayGrades")
public class displayGrades extends HttpServlet {
private static final long serialVersionUID = 1L;
//private static final String url = "jdbc:postgresql://localhost:5400/postgres";
//private static final String user = "labuser";
//private static final String password = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public displayGrades() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
    private void toHTML(ResultSet rSet, PrintWriter out) {
//		try (Connection conn = DriverManager.getConnection(url, user, password)) {
//			try(Statement stmt = conn.createStatement()) {

//				ResultSet rSet = stmt.executeQuery(query);
    			try {
    				ResultSetMetaData rSetMetaData = rSet.getMetaData();
    			

				out.print("<table>\n");
				out.print("    <tr>");

				for (int i=0; i<rSetMetaData.getColumnCount(); i++)
					out.print("<th>" + rSetMetaData.getColumnName(i+1) + "</th>");
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
//			}
//			catch(Exception ex) {
//				throw ex;
//			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		HttpSession session = request.getSession(false);
		PrintWriter out = response.getWriter();
		if (session == null) {
			response.sendRedirect("Login");
		}
		else {
			try(Connection conn = DriverManager.getConnection(Home.url, Home.user, Home.password)){
				try (PreparedStatement stmt = conn.prepareStatement("select course_id, title, sec_id, semester, year, grade from takes natural join course where id = ?")){
					String id = (String)session.getAttribute("id");
					stmt.setString(1, id);
					ResultSet rs = stmt.executeQuery();
					toHTML(rs, out);
//					while (rs.next()) {
//						String course_id = rs.getString(1);
//						String title = rs.getString(2);
//						String sec_id = rs.getString(3);
//						String semester = rs.getString(4);
//						Integer year = rs.getInt(5);
//						String grade = rs.getString(6);
//						out.println(course_id + " ");
//					}
				} catch (Exception ex) {
					throw ex;
				}
			} catch (Exception e) {
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
