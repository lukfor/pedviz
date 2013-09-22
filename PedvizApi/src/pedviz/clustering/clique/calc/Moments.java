package pedviz.clustering.clique.calc;

import java.text.NumberFormat;

import pedviz.clustering.clique.calc.util.struct.Matrix;


// i dati devono arrivare gia' sortati
public class Moments {
  double num, sum, mean, average_deviation, standard_deviation, variance, median, deviation, min, minNo0, max;
  int i, n, mid;
  boolean pwKin;
  /*GUI*/
  String statistics ="";
  double stat_mean,stat_stdev,stat_min,stat_max;
  
  
  public String getStatistics(){
	  return statistics;
  }
  
  public double getMean(){
	  return stat_mean;
  }
  
  public double getStdev(){
	  return stat_stdev;
  }

  public double getMin(){
	  return stat_min;
  }

  public double getMax(){
	  return stat_max;
  }
 
  /*GUIEND*/
  
  public Moments(boolean pwKin) {
    num = sum = 0.0;
    mean = 0.0;
    average_deviation = 0.0;
    standard_deviation = 0.0;
    variance = 0.0;
    median = 0.0;
    deviation = 0.0;
    i = n = mid = 0;
    this.pwKin=pwKin;
  }

  public void calculate(Matrix matrix, boolean shortReport, int type) {
    double [] data = new double [(matrix.length()*(matrix.length()-1))/2];
    int ind = 0;
    for (int x=0;x<matrix.length();x++) {
      for (int y=x+1;y<matrix.length();y++)
        data[ind++]=matrix.retrieveTriangMatrix(x,y);
    }
    //System.out.println("Begin: " + new java.util.Date());
    quicksort(data,0,data.length-1);
    //System.out.println("End: " + new java.util.Date());

    solve (data, shortReport,type);
  }

  public void calculate(double [] vector, boolean shortReport, int type) {
    double [] data = new double[vector.length];
    int ind = 0;
    for (int x=0;x<vector.length;x++) {
        data[ind++]=vector[x];
    }
    //System.out.println("Begin: " + new java.util.Date());
    quicksort(data,0,data.length-1);
    //System.out.println("End: " + new java.util.Date());

    solve (data, shortReport,type);
  }

  public double calculateVector(double [] data,int type) {
    quicksort(data,0,data.length-1);
    return solve (data, true,type);
  }

  /*
   * type:
   * 0..Inbr
   * 1..Kin
   * 
   */
  public double solve(double [] data, boolean shortReport, int type) {
    NumberFormat nf = NumberFormat.getInstance();
    //nf.setMaximumFractionDigits(13);
    nf.setGroupingUsed(false);
    if (shortReport) nf.setMaximumFractionDigits(3);
    else nf.setMaximumFractionDigits(6);
    nf.setMinimumFractionDigits(3);

    n = data.length;
    max = data[data.length-1];
    for (i=0; i<n; i++) {
      if (minNo0==0) minNo0=data[i];
      sum +=data[i];
    }

    mean = sum/n;
    for (i=0; i<n; i++) {
      deviation = data[i] - mean;
      average_deviation += Math.abs(deviation);
      variance += Math.pow(deviation,2);
    }
    average_deviation /= n;
    variance /= (n - 1);
    standard_deviation = Math.sqrt(variance);
    double nD = (double)n;

    mid = (n/2);
    median = (n % 2 != 0) ?
             data[mid] :
             (data[mid] +(data[mid-1]))/2;


    if (shortReport){
      /*GUI*/
    	stat_mean = mean;
    	stat_stdev = standard_deviation;
    	/*
    	 * TODO revise Stat output min
    	 * 
    	 */
    	stat_min = minNo0;
        stat_max = data[data.length-1];
    	
    	
    	statistics ="Mean(" + nf.format(mean)+")"+" StDev("+nf.format(standard_deviation)+") Min("+ nf.format(data[0])+(data[0]==0?" - " +nf.format(minNo0):"")+") Max("+nf.format(data[data.length-1])+")";
    	
    	//System.out.println(statistics);
      /*GUI END*/
    }
    else {
     
    	if(type==0)
    		statistics = "Total subjects    : " + n+"\n";
    	if(type==1)
    		statistics = "Total pairs       : " + n+"\n";
    	
    	statistics = statistics +
                     "Median            : " + nf.format(median)+"\n"+
                     "Mean              : " + nf.format(mean)+"\n"+
                     "Average_deviation : " + nf.format(average_deviation)+"\n"+
                     "Standard_deviation: " + nf.format(standard_deviation)+"\n"+
                     "Variance          : " + nf.format(variance)+"\n"+
                     "Min               : " + nf.format(data[0])+(data[0]==0?"-" +nf.format(minNo0):"")+"\n"+
                     "Max               : " + nf.format(max)+"\n";
    	//System.out.println(statistics);
        data = new double [0];
    }

    if (pwKin) return (minNo0==0?0.0000001:minNo0);
    else return (max);
  }

 public static void quicksort(double[] data, int left, int right) {

        if (data == null || data.length < 2) return;

        int i = left, j = right;

        double x = data[(left+right)/2];

        do {

            while (data[i] < x) i++;
            while (x < data[j]) j--;

            if (i <= j) {

               double temp = data[i]; data[i] = data[j]; data[j] = temp;
               i++;
               j--;

              }

           } while (i <= j);

        if (left < j)  quicksort(data, left, j);
        if (i < right) quicksort(data, i, right);

    }

}
