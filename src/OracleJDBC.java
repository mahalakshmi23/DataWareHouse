//STEP 1. Import required packages
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.math3.stat.inference.TTest;

public class OracleJDBC {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";  
   static final String DB_URL = "jdbc:oracle:thin:@//dbod-scan.acsu.buffalo.edu:1521/cse00000.buffalo.edu";

   //  Database credential
   static final String USER = "mvenkata";
   static final String PASS = "cse601";
   
   public static void main(String[] args) throws SQLException {
   Connection conn = null;
   Statement stmt = null;
   
   //Driver myDriver = new oracle.jdbc.driver.OracleDriver();
   //DriverManager.registerDriver( myDriver );
   
   
   try{
      //STEP 2: Register JDBC driver
      Class.forName("oracle.jdbc.driver.OracleDriver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL,USER,PASS);

      //STEP 4: Execute a query
      System.out.println("Creating statement...");
      stmt = conn.createStatement();
      String sql;
       
      sql = "SELECT MF.EXP FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN ("+
      "SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN ( SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN ("+
      "SELECT DS_ID FROM DISEASE WHERE NAME='ALL')) AND S_ID IS NOT NULL ) AND MF.PB_ID IN" +  
      "(SELECT MF.PB_ID FROM MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN"+
      "(SELECT PB.PB_ID FROM PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN"+
      "(SELECT GF.GENE_UID FROM GENE_FACT GF WHERE GO_ID=12502)))";
      
      
      ResultSet rs = stmt.executeQuery(sql);
      //STEP 5: Extract data from result set
      ArrayList<Double> list = new ArrayList<Double>();
      ArrayList<Double> list2 = new ArrayList<Double>();
      
      double sample1Sum = 0.0;
      while(rs.next()){
    	  
         //Retrieve by column name
        
         Double exp = rs.getDouble("EXP");
         sample1Sum += exp;
         list.add(exp);
      }
      
      double mean1 = sample1Sum/list.size();
      double variance1 = 0.0;
      
      System.out.println(list);
      //public double pairedT(double[] sample1,
      //double[] sample2)
      double[] sample1 = new double[list.size()];
     
      int i = 0;
      for(; i < list.size(); i++) {
    	  sample1[i] = list.get(i);
    	  variance1 += Math.pow(sample1[i] - mean1, 2); 
      }
      System.out.println("list size is " + list.size());
      variance1 = variance1 / (list.size() - 1);     
      
      //Similarly get the next sample
      
      sql = "SELECT MF.EXP FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN ("+
		"SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN ("+
   	 	"SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN (SELECT DS_ID FROM DISEASE WHERE NAME!='ALL')) AND S_ID IS NOT NULL)"+
   	 	"AND MF.PB_ID IN " + 
		"(SELECT MF.PB_ID FROM MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN " +
		"(SELECT PB.PB_ID FROM PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN " +
		"(SELECT GF.GENE_UID FROM GENE_FACT GF WHERE GO_ID=12502)))";

      
 
      
      rs = stmt.executeQuery(sql);
      //STEP 5: Extract data from result set
      double sample2Sum=0;
      
      while(rs.next()){
         double exp = rs.getDouble("EXP");
         sample2Sum += exp;
         list2.add(exp);
         //System.out.println(" EXP: " + exp);
      }
      
      double mean2=sample2Sum/list2.size();
      
      
      //System.out.println(list);
      //public double pairedT(double[] sample1,
      //double[] sample2)
      double[] sample2 = new double[list2.size()];
      double variance2 = 0;
      i = 0;
      for(; i < list2.size(); i++) {
    	  sample2[i] = list2.get(i);
    	  variance2 += Math.pow( sample2[i]-mean2, 2);
      }
      
      variance2 = variance2 / (list2.size()-1);
      //System.out.println(sample2);
      
      double tt = Math.abs(mean1 - mean2) / (Math.sqrt(variance1/list.size() + variance2/list2.size()));
      
      System.out.println("--------------");
      System.out.println(tt);
      
      
      System.out.println("result is ");
      TTest t = new TTest();
      double result = t.homoscedasticTTest(sample1, sample2); //got 0314
      //double result = t.pairedT(sample1, sample2); //got 0314
      System.out.println(result);
      //STEP 6: Clean-up environment
      rs.close();
      stmt.close();
      conn.close();
   }catch(SQLException se){
      se.printStackTrace();
   }catch(Exception e){
      e.printStackTrace();
   }finally{
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }
   }
   System.out.println("Goodbye!");
   }
}
