
public class Statistics {

	public double mean(double[] sample){

		int count = sample.length;
		double mean=0.0;

		for(int i = 0; i<count; i++) {
			mean += sample[i];
		}
		mean=mean/count;
		return mean;
	}


	public double variance(double[] sample,double mean){
		int count = sample.length;
		double variance = 0.0;

		for (int i = 0; i < count; i++) {
			variance += Math.pow(sample[i] - mean, 2); 
		}

		variance = variance/(count-1) ;

		return variance;		

	}

}
