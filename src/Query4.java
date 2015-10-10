/*
import java.sql.SQLException;
import org.apache.commons.math3.stat.inference.TTest;

public class Query4 {

	static RUN_QUERY runObj = new   RUN_QUERY ();
	static Statistics statObj = new Statistics();

	static double[] sample_1;
	static double[] sample_2;

	static double mean_1;
	static double mean_2;

	static double variance_1;
	static double variance_2;

	static int count_1;
	static int count_2;	


	public static void main(String[] args ) throws SQLException {

		String sqlQuery_1 = 

			"SELECT MF.EXP FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN ("+
			"SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN ( SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN ("+
			"SELECT DS_ID FROM DISEASE WHERE NAME='ALL')) AND S_ID IS NOT NULL ) AND MF.PB_ID IN" +  
			"(SELECT MF.PB_ID FROM MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN"+
			"(SELECT PB.PB_ID FROM PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN"+
			"(SELECT GF.GENE_UID FROM GENE_FACT GF WHERE GO_ID=12502)))";


		String sqlQuery_2 = 
			"SELECT MF.EXP FROM CLINICAL_FACT CF INNER JOIN MICROARRAY_FACT MF ON CF.S_ID=MF.S_ID WHERE CF.S_ID IN ("+
			"SELECT DISTINCT S_ID FROM CLINICAL_FACT WHERE P_ID IN ("+
			"SELECT P_ID FROM CLINICAL_FACT WHERE DS_ID IN (SELECT DS_ID FROM DISEASE WHERE NAME!='ALL')) AND S_ID IS NOT NULL)"+
			"AND MF.PB_ID IN " + 
			"(SELECT MF.PB_ID FROM MICROARRAY_FACT MF INNER JOIN PROBE PB ON MF.PB_ID=PB.PB_ID WHERE PB.PB_ID IN " +
			"(SELECT PB.PB_ID FROM PROBE PB INNER JOIN GENE_FACT GF ON PB.U_ID=GF.GENE_UID WHERE GF.GENE_UID IN " +
			"(SELECT GF.GENE_UID FROM GENE_FACT GF WHERE GO_ID=12502)))";


		sample_1 = runObj.runQuery( sqlQuery_1 );
		sample_2 = runObj.runQuery(sqlQuery_2);

		count_1=sample_1.length;
		count_2=sample_2.length;

		mean_1=statObj.mean(sample_1);
		mean_2=statObj.mean(sample_2);

		variance_1=statObj.variance(sample_1,mean_1);
		variance_2=statObj.variance(sample_2,mean_2);


		System.out.println("--mean--");
		System.out.println(mean_1);
		System.out.println(mean_2);

		System.out.println("--variance--");
		System.out.println(variance_1);
		System.out.println(variance_2);

		double pooledVariance = (((count_1-1) * variance_1) + ((count_2-1) * variance_2 )) / (count_1+count_2-2);      
		double newT= (mean_1 - mean_2) / Math.sqrt( (pooledVariance/count_1) + (pooledVariance/count_2));//1.007

		System.out.println("-----********---------");
		System.out.println(newT);

		TTest t = new TTest();
		double result = t.homoscedasticTTest(sample_1, sample_2); //got 0.314
		System.out.println(result);
	}
}
*/