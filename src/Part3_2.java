//STEP 1. Import required packages

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TTest;

public class Part3_2 {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";  
	static final String DB_URL = "jdbc:oracle:thin:@//dbod-scan.acsu.buffalo.edu:1521/cse00000.buffalo.edu";

	//  Database credential
	static final String USER = "mvenkata";
	static final String PASS = "cse601";

	public static void main(String[] args) throws SQLException {
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

			sql = "SELECT MF.EXP,pb.U_ID FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID " +
			"inner join probe pb on pb.pb_id=mf.pb_id " +
			"WHERE CF.S_ID IN (" +
			"SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN (" +
			"SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN (" +
			"SELECT DS_ID FROM DISEASE WHERE NAME='ALL')) AND S_ID IS NOT NULL ) and MF.PB_ID in ( " +
			"Select pb.pb_id from probe pb inner join gene_fact gf on gf.gene_uid=pb.u_id ) order by pb.U_ID";
			//get p_ID in array

			ResultSet rs = stmt.executeQuery(sql);

			ArrayList<Integer> genes =new ArrayList<Integer>();
			ArrayList<ArrayList<Double>> allData = new ArrayList<ArrayList<Double>>();
			//STEP 5: Extract data from result set

			double sample1Sum = 0.0;

			while(rs.next()){
				Integer gene_uid = rs.getInt("U_ID");
				if(!genes.contains(gene_uid)) {
					genes.add(gene_uid);
					allData.add(new ArrayList<Double>());
				}

				Double exp = rs.getDouble("EXP");
				//System.out.println(gene_uid + ":" + exp +" - " +genes.indexOf(gene_uid));
				allData.get(genes.indexOf(gene_uid)).add(exp);	
			}
			

			// get not all data
			sql = "SELECT MF.EXP,pb.U_ID FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID " +
			"inner join probe pb on pb.pb_id=mf.pb_id " +
			"WHERE CF.S_ID IN (" +
			"SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN (" +
			"SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN (" +
			"SELECT DS_ID FROM DISEASE WHERE NAME<>'ALL')) AND S_ID IS NOT NULL ) and MF.PB_ID in ( " +
			"Select pb.pb_id from probe pb inner join gene_fact gf on gf.gene_uid=pb.u_id ) order by pb.U_ID";
			//get p_ID in array

			rs = stmt.executeQuery(sql);


			ArrayList<ArrayList<Double>> notData = new ArrayList<ArrayList<Double>>();
			//STEP 5: Extract data from result set
			ArrayList<Integer> notGenes = new ArrayList<Integer>();

			while(rs.next()){
				Integer gene_uid = rs.getInt("U_ID");
				if(!notGenes.contains(gene_uid)) {
					notGenes.add(gene_uid);
					notData.add(new ArrayList<Double>());
				}

				Double exp = rs.getDouble("EXP");
				//System.out.println(gene_uid + ":" + exp +" - " +notGenes.indexOf(gene_uid));
				notData.get(notGenes.indexOf(gene_uid)).add(exp);	
			}

			

			ArrayList<Integer> informativeGene = new ArrayList<Integer>();
			TTest t = new TTest();
			double[] sample1, sample2;
			ArrayList<Double> list1, list2;
			for(int i = 0; i < genes.size(); i++) {
				list1 = allData.get(i);
				sample1 = new double[list1.size()];
				for(int a = 0; a < list1.size(); a++) {
					sample1[a] = list1.get(a);
				}
				list2 = notData.get(i);
				sample2 = new double[list2.size()];
				for(int a = 0; a < list2.size(); a++) {
					sample2[a] = list2.get(a);
				}
				double pValue = t.homoscedasticTTest(sample1, sample2);
				if(pValue <0.01) {
					informativeGene.add(genes.get(i));
				}
			}
			

			//part 3_2
			sql= "select * from test_samples";

			rs = stmt.executeQuery(sql);

			ArrayList<ArrayList<Double>> testSamples = new ArrayList<ArrayList<Double>>();

			for (int i =  0; i < 5; i++) {
				testSamples.add(new ArrayList<Double>());
			}


			while(rs.next()){
				Integer gene_uid = rs.getInt("GENE_UID");
				if(informativeGene.contains(gene_uid)) {
					Double exp = rs.getDouble("TEST1");
					(testSamples.get(0)).add(exp);
					exp = rs.getDouble("TEST2");
					(testSamples.get(1)).add(exp);
					exp = rs.getDouble("TEST3");
					(testSamples.get(2)).add(exp);
					exp = rs.getDouble("TEST4");
					(testSamples.get(3)).add(exp);
					exp = rs.getDouble("TEST5");
					(testSamples.get(4)).add(exp);

				}


			}
			

			//get the patients data with all
			sql = "SELECT MF.EXP,pb.U_ID, CF.P_ID FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID " +
			"inner join probe pb on pb.pb_id=mf.pb_id " +
			"WHERE CF.S_ID IN ( " +
			"SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN ( " +
			"SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN (  " +
			"SELECT DS_ID FROM DISEASE WHERE NAME='ALL')) AND S_ID IS NOT NULL ) and MF.PB_ID in ( "+
			"Select pb.pb_id from probe pb inner join gene_fact gf on gf.gene_uid=pb.u_id ) ";
			

			rs = stmt.executeQuery(sql);
			ArrayList<ArrayList<Double>> personsInformativeGenes = new ArrayList<ArrayList<Double>>();
			ArrayList<Integer> persons = new ArrayList<Integer>();
			while(rs.next()){
				Double exp = rs.getDouble("EXP");
				Integer gene_uid = rs.getInt("U_ID");
				Integer p_id = rs.getInt("P_ID");
				if(informativeGene.contains(gene_uid)) {
					
					
					//System.out.println(p_id+ ":" + exp  + ":" +  gene_uid);
					if(!persons.contains(p_id)) {
						//add new person
						persons.add(p_id);
						personsInformativeGenes.add(new ArrayList<Double>());
					}
					int index = persons.indexOf(p_id);
					personsInformativeGenes.get(index).add(exp);
				}
			}
			
			
			//for not ALL values
			sql = "SELECT MF.EXP,pb.U_ID, CF.P_ID FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID " +
			"inner join probe pb on pb.pb_id=mf.pb_id " +
			"WHERE CF.S_ID IN ( " +
			"SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN ( " +
			"SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN (  " +
			"SELECT DS_ID FROM DISEASE WHERE NAME<>'ALL')) AND S_ID IS NOT NULL ) and MF.PB_ID in ( "+
			"Select pb.pb_id from probe pb inner join gene_fact gf on gf.gene_uid=pb.u_id ) ";
			

			rs = stmt.executeQuery(sql);
			
			ArrayList<ArrayList<Double>> notPersonsInformativeGenes = new ArrayList<ArrayList<Double>>();
			ArrayList<Integer> notPersons = new ArrayList<Integer>();
			while(rs.next()){
				Integer gene_uid = rs.getInt("U_ID");
				if(informativeGene.contains(gene_uid)) {
					Integer p_id = rs.getInt("P_ID");
					Double exp = rs.getDouble("EXP");
					if(!notPersons.contains(p_id)) {
						//add new person
						notPersons.add(p_id);
						notPersonsInformativeGenes.add(new ArrayList<Double>());
					}
					int index = notPersons.indexOf(p_id);
					notPersonsInformativeGenes.get(index).add(exp);
				}
			}
			System.out.println(notPersonsInformativeGenes.size());
			
			//calculating correlation for pa and pn
			ArrayList<Double> ra, rb;
			PearsonsCorrelation p = new PearsonsCorrelation();
			double[] s1, s2;
			ArrayList<Double> l1,l2;
			for(int j = 0; j < 5; j++) {
				ra =  new ArrayList<Double>();
				l2 = testSamples.get(j);
				int count2 = l2.size();
				s2 = new double[count2];
				for(int a = 0; a < count2; a++) {
					s2[a] = l2.get(a);
				}
				for(int i = 0;i < personsInformativeGenes.size(); i++) {
					l1 = personsInformativeGenes.get(i);
					int count1 = l1.size();
					s1 = new double[count1];
					for(int a = 0; a < count1; a++) {
						s1[a] = l1.get(a);
					}
						
					ra.add(p.correlation(s1, s2));          
				
				}
			
				rb = new ArrayList<Double>();
				for(int i = 0;i < notPersonsInformativeGenes.size(); i++) {
					l1 = notPersonsInformativeGenes.get(i);
					int count1 = l1.size();
					s1 = new double[count1];
					for(int a = 0; a < count1; a++) {
						s1[a] = l1.get(a);
					}
					
						rb.add(p.correlation(s1, s2));     
				}
				double[] result1 = new double[ra.size()];
				for(int x = 0; x < ra.size(); x++) {
					result1[x] = ra.get(x);
				}
				double[] result2 = new double[rb.size()];
				for(int x = 0; x < rb.size(); x++) {
					result2[x] = rb.get(x);
				}
				double finalP = t.homoscedasticTTest(result1, result2);
				System.out.println("pval is "+ finalP);
				if(finalP < 0.01) {
					System.out.println( (j + 1) + " is classified as ALL");
				} else {
					System.out.println( (j + 1) + " is NOT Classified");
				}
			}
			/*
			for(int i = 0;i < personsInformativeGenes.size(); i++) {
				l1 = personsInformativeGenes.get(i);
				int count1 = l1.size();
				s1 = new double[count1];
				for(int a = 0; a < count1; a++) {
					s1[a] = l1.get(a);
				}
				//for(int j = 2; j < 3; j++) {
					l2 = testSamples.get(4);
					int count2 = l2.size();
					s2 = new double[count2];
					for(int a = 0; a < count2; a++) {
						s2[a] = l2.get(a);
					}
					ra.add(p.correlation(s1, s2));          
				//}
			}
			*/
			/*
			rb = new ArrayList<Double>();
			for(int i = 0;i < notPersonsInformativeGenes.size(); i++) {
				l1 = notPersonsInformativeGenes.get(i);
				int count1 = l1.size();
				s1 = new double[count1];
				for(int a = 0; a < count1; a++) {
					s1[a] = l1.get(a);
				}
				//for(int j = 2; j < 3; j++) {
					l2 = testSamples.get(4);
					int count2 = l2.size();
					s2 = new double[count2];
					for(int a = 0; a < count2; a++) {
						s2[a] = l2.get(a);
					}
					rb.add(p.correlation(s1, s2));          
				//}
			}
			result1 = new double[ra.size()];
			for(int i = 0; i < ra.size(); i++) {
				result1[i] = ra.get(i);
			}
			result2 = new double[rb.size()];
			for(int i = 0; i < rb.size(); i++) {
				result2[i] = rb.get(i);
			}
			finalP = t.homoscedasticTTest(result1, result2);
			System.out.println("pval is "+ finalP);
			if(finalP < 0.01) {
				System.out.println("classified as ALL");
			} else {
				System.out.println("NOT Classified");
			}
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