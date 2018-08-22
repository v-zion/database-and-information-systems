package Login;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;
import Home.Home;

import java.io.PrintWriter;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
//	private static final String url = "jdbc:postgresql://localhost:5400/postgres";
//	private static final String user = "labuser";
//	private static final String password = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		PrintWriter out = response.getWriter();
		if (session == null) {
			if (request.getParameter("id") == null || request.getParameter("pass") == null) {
				out.println("<head><title>Login page</title></head>");
				out.println("<body>");
				out.println("<form action = \"Login\" method = \"post\">\n" + 
						"ID: <input type = \"text\" name = \"id\"><br>\n" + 
						"Password: <input type = \"text\" name = \"pass\"><br>\n" + 
						"<input type = \"submit\" value = \"Login\">\n" + 
						"</form>");
				out.println("</body>");
			}
			else {
				String id = request.getParameter("id");
				String pass = request.getParameter("pass");
				try(Connection conn = DriverManager.getConnection(Home.url, Home.user, Home.password)){
					try (PreparedStatement stmt = conn.prepareStatement("select password from password where id = ?")){
						stmt.setString(1, id);
						ResultSet rs = stmt.executeQuery();
						if (rs.next()) {
							String p = rs.getString(1);
							if (p.equals(pass)) {
								session = request.getSession(true);
								session.setAttribute("uid", id);
								session.setAttribute("pass", pass);
								out.println("Authentication successful");
								response.sendRedirect("Home");
							}
							else {
								out.println("<body>Authentication failed<br>");
								out.println("<form action = \"Login\" method = \"post\">\n" + 
										"ID: <input type = \"text\" name = \"id\"><br>\n" + 
										"Password: <input type = \"text\" name = \"pass\"><br>\n" + 
										"<input type = \"submit\" value = \"Login\">\n" + 
										"</form></body>");
							}
						}
						else {
							out.println("<body>Authentication failed<br>");
							out.println("<form action = \"Login\" method = \"post\">\n" + 
									"ID: <input type = \"text\" name = \"id\"><br>\n" + 
									"Password: <input type = \"text\" name = \"pass\"><br>\n" + 
									"<input type = \"submit\" value = \"Login\">\n" + 
									"</form></body>");
						}
					} catch (Exception ex) {
						throw ex;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else {
//			out.println("Already logged in");
			response.sendRedirect("Home");
		}
//		doGet(request, response);
	}

}