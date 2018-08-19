import java.sql.*;
import java.util.Scanner;
import javax.json.*;

public class Main {
	private static final String url = "jdbc:postgresql://localhost:5400/postgres";
	private static final String user = "animesh";
	private static final String password = "";

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.print("Query: ");
		String query = sc.nextLine();
		toHTML (query);
		toJSON (query);
	}

	private static void toHTML(String query) {
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			try(Statement stmt = conn.createStatement()) {

				ResultSet rSet = stmt.executeQuery(query);
				ResultSetMetaData rSetMetaData = rSet.getMetaData();

				System.out.print("<table>\n");
				System.out.print("    <tr>");

				for (int i=0; i<rSetMetaData.getColumnCount(); i++)
					System.out.print("<th>" + rSetMetaData.getColumnName(i+1) + "</th>");
				System.out.print("</tr>\n");

				while (rSet.next()) {
					System.out.print("    <tr>");
					for (int i=0; i<rSetMetaData.getColumnCount(); i++)
						System.out.print("<td>" + rSet.getString(i+1) + "</td>");
					System.out.print("</tr>\n");
				}

				System.out.print("</table>\n");
			}
			catch(Exception ex) {
				throw ex;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void toJSON(String query) {
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
	    	try(Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

				ResultSet rSet = stmt.executeQuery(query);
				ResultSetMetaData rSetMetaData = rSet.getMetaData();
				
				JsonObjectBuilder json = Json.createObjectBuilder();
				JsonArrayBuilder header = Json.createArrayBuilder();
				JsonArrayBuilder data = Json.createArrayBuilder();
				
				
				

//				System.out.print("{\"header\": [");
				for (int i=0; i<rSetMetaData.getColumnCount(); i++){
					String col = rSetMetaData.getColumnName(i + 1).toString();
					header.add(col);
//					System.out.print("\"" + rSetMetaData.getColumnName(i+1) + "\"");
//					if (i != rSetMetaData.getColumnCount()-1)
//						System.out.print(", ");
				}
//				System.out.print("],\n");

				rSet.last();
				int n = rSet.getRow();
				rSet.first();
				rSet.previous();

//				System.out.print(" \"data\": [");
				while (rSet.next()) {
					JsonObjectBuilder datarow = Json.createObjectBuilder();
//					if (rSet.getRow() == 1)
//						System.out.print("{");
//					else
//						System.out.print("          {");
					for(int i=0; i<rSetMetaData.getColumnCount(); i++){
						datarow.add(rSetMetaData.getColumnName(i+1), rSet.getString(i+1));
//						System.out.print("\"" + rSetMetaData.getColumnName(i+1) + "\":\"" + rSet.getString(i+1) + "\"");
//						if (i != rSetMetaData.getColumnCount()-1)
//							System.out.print(", ");
					}
//					System.out.print("}");
//					if (rSet.getRow() != n)
//						System.out.print(",");
					data.add(datarow);
//					System.out.println();
				}

//				System.out.println("         ]");
//				System.out.println("}");
				json.add("header", header);
				json.add("data", data);
				JsonObject js = json.build();
				System.out.println(js.toString());
			}
			catch(Exception ex) {
				throw ex;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
