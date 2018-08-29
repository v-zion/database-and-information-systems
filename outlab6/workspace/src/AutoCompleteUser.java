

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Servlet implementation class AutoCompleteUser
 */
@WebServlet("/AutoCompleteUser")


public class AutoCompleteUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	
    public AutoCompleteUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.sendRedirect("LoginServlet");
		}
		else {
//			String userid = (String)session.getAttribute("id");
			String term = request.getParameter("term");
			String query = "select uid, name, phone from users where uid like ? or name like ? or phone like ?";
			String like = term + "%";
			String res = myDbHelper.executeQueryJson(query, 
					new myDbHelper.ParamType[] {myDbHelper.ParamType.STRING, 
							myDbHelper.ParamType.STRING,
							myDbHelper.ParamType.STRING}, 
					new String[] {like, like, like});
			PrintWriter out = response.getWriter();
			out.print(res);
			System.out.println(res);
		}
	}

}

final class myDbHelper extends DbHelper{
	protected static String executeQueryJson(String query, ParamType[] paramTypes, Object[] params) {
    	ArrayNode json = null;
    	try (Connection conn = DriverManager.getConnection(Config.url, Config.user, Config.password))
        {
            conn.setAutoCommit(false);
            try(PreparedStatement stmt = conn.prepareStatement(query)) {
            	setParams(stmt, paramTypes, params);
                ResultSet rs = stmt.executeQuery();
                json = resultSetToJson(rs);
                conn.commit();
            }
            catch(Exception ex)
            {
                conn.rollback();
                throw ex;
            }
            finally{
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            return errorJson(e.getMessage()).toString();
        }
    	
    	
    	return json.toString();
    }
	
	public static ArrayNode resultSetToJson(ResultSet rs) throws SQLException {
		ArrayNode arr = mapper.createArrayNode();

		while(rs.next()) {
			ObjectNode obj = mapper.createObjectNode();
			
			String id = rs.getString(1);
			String name = rs.getString(2);
			String phone = rs.getString(3);
			String label = id + " " + name + " " + phone;
			if (id == null || name == null || phone == null) {
				obj.putNull("label");
			}
			else {
				obj.put("label", label);
			}
			if (id == null) {
				obj.putNull("value");
			}
			else {
				obj.put("value", id);
			}
			
 			arr.add(obj);
		}
		return arr;
	}

	
	private static void setParams(PreparedStatement stmt,
			ParamType[] paramTypes, 
			Object[] params) throws SQLException {
		List<ParamType> paramTypesList = Arrays.asList(paramTypes);
		List<Object> paramsList = Arrays.asList(params);
		
		for(int i=0;i<paramsList.size();i++) {
			ParamType type = paramTypesList.get(i);
			Object param = paramsList.get(i);
			
			if(type.equals(ParamType.STRING)) {
				stmt.setString(i+1, (String)param);
			}
			else if(type.equals(ParamType.INT)) {
				stmt.setInt(i+1, (Integer)param);
			}
		}
	}
}

