package createConversation;

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
 * Servlet implementation class createConversation
 */
@WebServlet("/createConversation")
public class createConversation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public createConversation() {
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
		if (session == null){
			response.sendRedirect("Login");
		}
		else{

			String uid = request.getParameter("uid");
			if (uid == null) {
				out.println("<body>No User id found in the request");
				out.println("<br><a href=\"Home\">Go back to Home</a></body>");
			}
			else {
				try (Connection conn = DriverManager.getConnection(Home.url, Home.user, Home.password)){
					conn.setAutoCommit(false);
					try (PreparedStatement stmt3 = conn.prepareStatement("select * from users where uid = ?");
						PreparedStatement stmt1 = conn.prepareStatement("select * from conversations where uid1 = ? and uid2 = ?");
						PreparedStatement stmt2 = conn.prepareStatement("insert into conversations values (?, ?)")){
						String uid_self = (String)session.getAttribute("uid");
						// System.out.println(uid_self);
						stmt3.setString(1, uid);
						ResultSet rs3 = stmt3.executeQuery();
						conn.commit();
						if (!rs3.next()) {
							out.println("<body>No such user id exists");
						}
						else {
							if (uid_self.compareTo(uid) < 0){
								stmt1.setString(1, uid_self);
								stmt1.setString(2, uid);
							}
							else{
								stmt1.setString(1, uid);
								stmt1.setString(2, uid_self);
							}
							ResultSet rs1 = stmt1.executeQuery();
							conn.commit();
							if (rs1.next()){
								out.println("<body>Conversation already exists</body>");
							}
							else{
								Boolean cont = true;
								if (uid_self.equals(uid)){
									out.println("<body>Cannot create conversation with self</body>");
									cont = false;
								}
								else if (uid_self.compareTo(uid) < 0){
									stmt2.setString(1, uid_self);
									stmt2.setString(2, uid);
								}
								else{
									stmt2.setString(1, uid);
									stmt2.setString(2, uid_self);
								}
								if (cont) {
									stmt2.executeUpdate();
									conn.commit();
									out.println("<body>Conversation successfully created</body>");
								}
							}
						}
	
						out.println("<br><a href=\"Home\">Go Back to Home</a>");
	
					} catch (Exception ex){
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
