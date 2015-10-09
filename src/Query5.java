//STEP 1. Import required packages
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.math3.stat.inference.TTest;

public class Query5 {
	
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";  
   static final String DB_URL = "jdbc:oracle:thin:@//dbod-scan.acsu.buffalo.edu:1521/cse00000.buffalo.edu";

   //  Database credential
   static final String USER = "mvenkata";
   static final String PASS = "cse601";
   
   public static void main(String[] args) throws SQLException {	   
   
   double[] sample_1;
   double[] sample_2;
   double[] sample_3;
   double[] sample_4;
   
   Query5 obj = new  Query5();
   
   String sqlQuery_1 = 
	   " SELECT (MF.EXP) FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN " +
	   " (SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN" + 
	   " (SELECT DS_ID FROM DISEASE WHERE NAME='ALL')) AND S_ID IS NOT NULL) AND MF.PB_ID IN (SELECT MF.PB_ID FROM" +
	   " MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN (SELECT PB.PB_ID FROM " + 
	   "PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN (SELECT GF.GENE_UID FROM " +
	   "GENE_FACT GF WHERE GO_ID=0007154)))";
   
   String sqlQuery_2 = 
	   " SELECT (MF.EXP) FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN " +
	   " (SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN" + 
	   " (SELECT DS_ID FROM DISEASE WHERE NAME='AML')) AND S_ID IS NOT NULL) AND MF.PB_ID IN (SELECT MF.PB_ID FROM" +
	   " MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN (SELECT PB.PB_ID FROM " + 
	   "PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN (SELECT GF.GENE_UID FROM " +
	   "GENE_FACT GF WHERE GO_ID=0007154)))";
   
   String sqlQuery_3= 
	   " SELECT (MF.EXP) FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN " +
	   " (SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN" + 
	   " (SELECT DS_ID FROM DISEASE WHERE NAME='Colon tumor')) AND S_ID IS NOT NULL) AND MF.PB_ID IN (SELECT MF.PB_ID FROM" +
	   " MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN (SELECT PB.PB_ID FROM " + 
	   "PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN (SELECT GF.GENE_UID FROM " +
	   "GENE_FACT GF WHERE GO_ID=0007154)))";
   
   String sqlQuery_4=
	   " SELECT (MF.EXP) FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN " +
	   " (SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN" + 
	   " (SELECT DS_ID FROM DISEASE WHERE NAME='Breast tumor')) AND S_ID IS NOT NULL) AND MF.PB_ID IN (SELECT MF.PB_ID FROM" +
	   " MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN (SELECT PB.PB_ID FROM " + 
	   "PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN (SELECT GF.GENE_UID FROM " +
	   "GENE_FACT GF WHERE GO_ID=0007154)))";
  
   sample_1 = obj.runQuery( sqlQuery_1 );
   sample_2 = obj.runQuery( sqlQuery_2 );
   sample_3 = obj.runQuery( sqlQuery_3 );
   sample_4 = obj.runQuery( sqlQuery_4 );
   
   //<ArrayList> 
   
   System.out.println("Goodbye!");
   }
   
   public double[ ] runQuery( String sql){
	   
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
		      System.out.println("*** Connected to database ***");
		      
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
