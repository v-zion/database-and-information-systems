import java.sql.*;
import java.util.*;


public class Main {

	private static final String url = "jdbc:postgresql://localhost:5400/postgres";
	private static final String user = "animesh";
	private static final String password = "";
	
	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		
		
		try (Connection conn = DriverManager.getConnection(url, user, password)){
			conn.setAutoCommit(false);
			try (PreparedStatement stmt1 = conn.prepareStatement("with recursive rec_height(id, height) as ("
					+ "select id, 1 from part "
					+ "union "
					+ "select pid, height + 1 from rec_height join subpart on spid = id where height < 100)"
					+ "select id, max(height) as h from rec_height group by id order by h");
				PreparedStatement stmt2 = conn.prepareStatement("select * from part");
				PreparedStatement stmt3 = conn.prepareStatement("select * from subpart", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				PreparedStatement stmt4 = conn.prepareStatement("create temporary table costtable(id varchar(20) primary key, cost int)");
				PreparedStatement stmt5 = conn.prepareStatement("insert into costtable values (?, ?)");
				PreparedStatement stmt6 = conn.prepareStatement("select cost from costtable where id = ?");){
//				stmt.setString(1, "student");
				ResultSet rs1 = stmt1.executeQuery();
				ResultSet rs2 = stmt2.executeQuery();
				ResultSet rs3 = stmt3.executeQuery();
				conn.commit();
				Map<String, Integer> cost = new HashMap<String, Integer>();
				while (rs2.next()) {
					String part_name = rs2.getString(1);
					Integer part_cost = rs2.getInt(2);
					cost.put(part_name, part_cost);
				}
				while (rs1.next()) {
					String part_name = rs1.getString(1);
//					System.out.println("here is " + part_name);
					while (rs3.next()) {
						String pid = rs3.getString(1);
						String spid = rs3.getString(2);
						Integer quantity = rs3.getInt(3);
						if (!pid.equals(part_name)) {
							continue;
						}
						Integer new_cost = cost.get(pid) + quantity * cost.get(spid);
						cost.put(pid, new_cost);
//						System.out.println(pid + " and " + new_cost);
					}
					rs3.beforeFirst();
				}
//				System.out.println(cost);
				stmt4.executeUpdate();
				for (Map.Entry m : cost.entrySet()) {
					String pid = (String)m.getKey();
					Integer cst = (Integer)m.getValue();
					stmt5.setString(1, pid);
					stmt5.setInt(2, cst);
					stmt5.executeUpdate();
				}
				conn.commit();
				while (true) {
					String id = "";
					try {
						id = scanner.next();
					} catch (Exception NoSuchElementException) {
						System.exit(0);
					}
					stmt6.setString(1, id);
					ResultSet rs = stmt6.executeQuery();
					conn.commit();
					if (rs.next()) {
						System.out.println(id + " " + rs.getInt(1));
					}
					else {
						System.out.println("Not found");
					}
				}
				
				
			}
			catch (Exception ex) {
				conn.rollback();
				throw ex;
			}
			finally {
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public Main() {
		super();
	}

}