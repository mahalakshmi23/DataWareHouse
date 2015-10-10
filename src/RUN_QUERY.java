//STEP 1. Import required packages
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;	
import java.util.ArrayList;

public class RUN_QUERY {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";  
	static final String DB_URL = "jdbc:oracle:thin:@//dbod-scan.acsu.buffalo.edu:1521/cse00000.buffalo.edu";

	//  Database credentials
	static final String USER = "mvenkata";
	static final String PASS = "cse601";

	public double[ ] runQuery( String sql) throws SQLException{

		Connection conn = null;
		Statement stmt = null;  
		int count = 0;
		ArrayList<Double> list = new ArrayList<Double>();
		double[]  sample = null;

		try{
			//STEP 2: Register JDBC driver
			Class.forName("oracle.jdbc.driver.OracleDriver");

			//STEP 3: Open a connection     
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			System.out.println("*** Connected to database ***");

			//STEP 4: Execute a query
			stmt = conn.createStatement();
			System.out.println(sql);

			ResultSet rs = stmt.executeQuery(sql);
			System.out.println("*** Query executed successfully ***");

			//STEP 5: Extract data from result set     

			while(rs.next()){
				Double exp = rs.getDouble("EXP");         
				list.add(exp);
			}

			count = list.size();

			sample = new double[ count];

			for(int i = 0; i < count; i++) {
				sample[i] = list.get(i);
			}

			//STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
			System.out.println("*** Closed the connection successfully ***");

		} catch ( SQLException se ) {
			se.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}finally {
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
				se2.printStackTrace();
			}
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}

		return sample;

	}

}
