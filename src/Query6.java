//STEP 1. Import required packages

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class Query6 {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";  
	static final String DB_URL = "jdbc:oracle:thin:@//dbod-scan.acsu.buffalo.edu:1521/cse00000.buffalo.edu";

	//  Database credential
	static final String USER = "mvenkata";
	static final String PASS = "cse601";

	public static void main(String[] args ) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			Class.forName("oracle.jdbc.driver.OracleDriver");

			//STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			System.out.println("connected..");

			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;

			sql = "SELECT p_id, exp FROM CLINICAL_FACT CF INNER JOIN " +"	MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN ( " +
			"SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN (" +
			"SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN (SELECT DS_ID FROM DISEASE" +
			" WHERE NAME='ALL')) AND S_ID IS NOT NULL )" +
			"AND MF.PB_ID IN " +
			"(SELECT MF.PB_ID FROM MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN " +
			"(SELECT PB.PB_ID FROM PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN" +
			"(SELECT GF.GENE_UID FROM GENE_FACT GF WHERE GO_ID=0007154)))";

			//get p_ID in array
			
			ResultSet rs = stmt.executeQuery(sql);
			
			ArrayList<Integer> patients =new ArrayList<Integer>();
			ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
			//STEP 5: Extract data from result set
			ArrayList<Double> list1 = new ArrayList<Double>();
			ArrayList<Double> list2 = new ArrayList<Double>();

			double sample1Sum = 0.0;

			while(rs.next()){
				Integer p_id = rs.getInt("P_ID");
				if(!patients.contains(p_id)) {
					patients.add(p_id);
					data.add(new ArrayList<Double>());
				}

				Double exp = rs.getDouble("EXP");
				data.get(patients.indexOf(p_id)).add(exp);
				
			}
			
			PearsonsCorrelation p = new PearsonsCorrelation();
		      double cor = 0.0;
		      int count = 0;
		      int length = data.size();
		      double[] sample1, sample2;
		      ArrayList<Double> dataList1;
		      ArrayList<Double> dataList2;
		      for(int i = 0; i < length - 1; i++) {
		      	for(int j = i + 1; j < length; j++) {
		      		count++;
		      		
		      		dataList1 = data.get(i);
		      		sample1 = new double[dataList1.size()];
		      		for(int a = 0; a < dataList1.size(); a++) {
		      			System.out.println(dataList1.get(a));
		      			sample1[a] = dataList1.get(a);
		      		}
		      		dataList2 = data.get(j);
		      		sample2 = new double[dataList2.size()];
		      		for(int a = 0; a < dataList2.size(); a++) {
		      			sample2[a] = dataList2.get(a);
		      		}
		      		list2 = data.get(j);
		      		cor += p.correlation(sample1, sample2); 
		      	}
		      }
		      
		      double avgCor = cor/count;
		      
		      System.out.println("result for ALL to ALL is ");
		      System.out.println(avgCor);
			/*
			int count1=list1.size();
			double mean1 = sample1Sum/count1;
			double[] sample1 = new double[count1];

			double variance1=0.0;

			for(int i = 0; i < count1; i++) {
				sample1[i] = list1.get(i);
				variance1 += Math.pow(sample1[i] - mean1, 2); 
			}
			variance1 = variance1 / (count1 - 1);     

			//Similarly get the next sample

			sql = "SELECT MF.EXP FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN ( " +
			"SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN ( " +
			"SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN (SELECT DS_ID FROM DISEASE WHERE NAME='AML')) AND S_ID IS NOT NULL) " + 
			"AND MF.PB_ID IN " +
			"(SELECT MF.PB_ID FROM MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN " +
			"(SELECT PB.PB_ID FROM PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN " +
			"(SELECT GF.GENE_UID FROM GENE_FACT GF WHERE GO_ID=0007154)))";




			rs = stmt.executeQuery(sql);
			while(rs.next()){
				double exp = rs.getDouble("EXP");
				list2.add(exp);
				//System.out.println(" EXP: " + exp);
			}
			double[] sample2 = new double[list2.size()];

			int count2=list2.size();
			// double variance2 = 0;
			int i = 0;
			for(; i < count2; i++) {
				sample2[i] = list2.get(i);
				//variance2 += Math.pow( sample2[i] - mean2, 2);
			}

			System.out.println("-----List 1---------");
			System.out.println(list1);




			System.out.println("-------List 2-------");
			System.out.println(list2);


			System.out.println("result is ");
			PearsonsCorrelation p = new PearsonsCorrelation();
			double result = p.correlation(sample1, sample1); 
			//double result = t.pairedT(sample1, sample2); //got 0314
			System.out.println(result);
			*/
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