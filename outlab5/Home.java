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
	public static final String user = "labuser";
	public static final String password = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Home() {
        super();
        // TODO Auto-generated constructor stub
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
			String id = (String)session.getAttribute("id");
			try (Connection conn = DriverManager.getConnection(url, user, password)){
				conn.setAutoCommit(false);
				try (PreparedStatement stmt1 = conn.prepareStatement("select name, dept_name from student where id = ?");
					PreparedStatement stmt2 = conn.prepareStatement("select name, dept_name from instructor where id = ?")){
					stmt1.setString(1, id);
					stmt2.setString(1, id);
					ResultSet rs1 = stmt1.executeQuery();
					ResultSet rs2 = stmt2.executeQuery();
					conn.commit();
					Boolean is_student = false;
					String name = "";
					String dept_name = "";
					if (rs1.next()) {
						is_student = true;
						name = rs1.getString(1);
						dept_name = rs1.getString(2);
					}
					else {
						rs2.next();
						name = rs2.getString(1);
						dept_name = rs2.getString(2);
					}
					out.println("<body>");
					out.println("Name: " + name + "<br>");
					out.println("Department name: " + dept_name + "<br>");
					if (is_student) {
						out.println("<a href = \"displayGrades\">Grades</a><br>");
					}
					
					out.println("<br><form action = \"Logout\" method = \"post\">"
							+ "<input type = \"submit\" value = \"Logout\">");
					out.println("</body>");
					
					
				} catch (Exception ex) {
					conn.rollback();
					throw ex;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
