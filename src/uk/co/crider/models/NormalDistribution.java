package uk.co.crider.models;

import uk.co.crider.jablus.gui.jfreechart.GraphPanel;

import java.awt.Color;
import java.util.Random;

public class NormalDistribution extends Random{

	
	public double probOccurance(double x, double mean, double stdev){
		return (1 / Math.sqrt(2 * Math.PI * Math.pow(stdev, 2))) *
				Math.exp(-Math.pow(x - mean, 2) / (2 * Math.pow(stdev, 2)));
	}
	
	public void setSeed(long seed){
		super.setSeed(seed);
		toGen = true;
	}
	public NormalDistribution(long seed){
		super(seed);
		toGen = true;
	}
	// From http://www.taygeta.com/random/gaussian.html
	// The polar form of the Box-Muller transformation is both faster and more robust numerically. The algorithmic description of it is:
	private double y2;
	private boolean toGen;
	public double nextNorm(){
		if(!toGen){ toGen = true; return y2; }
        double x1, x2, w, y1;
        do{
        	x1 = 2.0 * (super.nextDouble() - 0.5);
        	x2 = 2.0 * (super.nextDouble() - 0.5);
        	w = x1 * x1 + x2 * x2;
        }while(w >= 1.0);
        w = Math.sqrt((-2.0 * Math.log(w)) / w);
        y1 = x1 * w;
        y2 = x2 * w;
        toGen = false;
        return y1;
	}

	
	
	
	//
//	 Lower tail quantile for standard normal distribution function.
	//
//	 This function returns an approximation of the inverse cumulative
//	 standard normal distribution function.  I.e., given P, it returns
//	 an approximation to the X satisfying P = Pr{Z <= X} where Z is a
//	 random variable from the standard normal distribution.
	//
//	 The algorithm uses a minimax approximation by rational functions
//	 and the result has a relative error whose absolute value is less
//	 than 1.15e-9.
	//
//	 Author:      Peter J. Acklam
//	 (Javascript version by Alankar Misra @ Digital Sutras (alankar@digitalsutras.com))
//	 Time-stamp:  2003-05-05 05:15:14
//	 E-mail:      pjacklam@online.no
//	 WWW URL:     http://home.online.no/~pjacklam

//	 An algorithm with a relative error less than 1.15*10-9 in the entire region.

	public static double normi(Random rand){ return normi(rand.nextDouble()); }
	public static double normi(double p){
		p = p < 0.0000000000000001 ? 0.0000000000000001 :
			p > 0.9999999999999999 ? 0.9999999999999999 : p;
	    // Coefficients in rational approximations
	    double[] a = {-3.969683028665376e+01,  2.209460984245205e+02,
	                      -2.759285104469687e+02,  1.383577518672690e+02,
	                      -3.066479806614716e+01,  2.506628277459239e+00};

	    double[] b = {-5.447609879822406e+01,  1.615858368580409e+02,
	                      -1.556989798598866e+02,  6.680131188771972e+01,
	                      -1.328068155288572e+01 };

	    double[] c = {-7.784894002430293e-03, -3.223964580411365e-01,
	                      -2.400758277161838e+00, -2.549732539343734e+00,
	                      4.374664141464968e+00,  2.938163982698783e+00};

	    double[] d = {7.784695709041462e-03, 3.224671290700398e-01,
	                       2.445134137142996e+00,  3.754408661907416e+00};

	    // Define break-points.
	    double plow  = 0.02425;
	    double phigh = 1 - plow;

	    // Rational approximation for lower region:
	    if( p < plow ){
	             double q  = Math.sqrt(-2*Math.log(p));
	             return (((((c[0]*q+c[1])*q+c[2])*q+c[3])*q+c[4])*q+c[5]) /
	                                             ((((d[0]*q+d[1])*q+d[2])*q+d[3])*q+1);
	    }

	    // Rational approximation for upper region:
	    if( phigh < p ){
	             double q  = Math.sqrt(-2*Math.log(1-p));
	             return -(((((c[0]*q+c[1])*q+c[2])*q+c[3])*q+c[4])*q+c[5]) /
	                                                    ((((d[0]*q+d[1])*q+d[2])*q+d[3])*q+1);
	    }

	    // Rational approximation for central region:
	    double q = p - 0.5;
	    double r = q*q;
	    return (((((a[0]*r+a[1])*r+a[2])*r+a[3])*r+a[4])*r+a[5])*q /
	                             (((((b[0]*r+b[1])*r+b[2])*r+b[3])*r+b[4])*r+1);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args){
		testBoxMuller();
//		testNormi();
	}
	
	public static void testBoxMuller(){
		int seed = 5;
		Random rand = new Random(seed);
		NormalDistribution norm = new NormalDistribution(seed);
		
		// Test predictability
/*		for(int j = 0; j < 2; j++){
			norm.setSeed(2);
			rand.setSeed(2);
			for(int i = 0; i < 10; i++){
				System.out.println(norm.nextDouble() + "\t" + rand.nextDouble());
			}
			System.out.println();
		}
		System.exit(0);
*/		
		GraphPanel graph = new GraphPanel("BoxMuller random numbers", "x", "y", true, true);
		graph.addSeries("Box Muller", Color.RED.darker());
		graph.addSeries("Uniform", Color.GREEN.darker());
		double stdev = 1.5;
		double[] histMuller = new double[20];
		double[] histUniform = new double[20];
		double totalMuller = 0;
		double totalUniform = 0;
		int freqMuller = 0;
		int freqUniform = 0;
		for(int i = 0; i < 1000; i++){
			double bm = norm.nextDouble() * stdev;
			double un = (rand.nextDouble() - 0.5) * stdev * stdev * stdev * stdev;
			totalMuller += bm;
			totalUniform += un;
			freqMuller += bm > 0 ? 1 : bm < 0 ? -1 : 0;
			freqUniform += un > 0 ? 1 : un < 0 ? -1 : 0;
			if(bm >= -5 && bm < 5) histMuller[(int)((bm + 5) * histMuller.length / 10)]++;
			if(un >= -5 && un < 5) histUniform[(int)((un + 5) * histUniform.length / 10)]++;
			graph.addData("Box Muller", bm + 10);
			graph.addData("Uniform", un - 10);
		}
		System.out.println("Total muller:" + totalMuller + "\nTotal uniform:" + totalUniform);
		System.out.println("Freq muller:" + freqMuller + "\nFreq uniform:" + freqUniform);
		graph.displayWindow();

		GraphPanel hist = new GraphPanel("BoxMuller vs Uniform histogram", "val", "freq", true, true);
		hist.addSeries("Box Muller", Color.RED.darker());
		hist.addSeries("Uniform", Color.GREEN.darker());
		for(int i = 0; i < histMuller.length; i++){
			hist.addData("Box Muller", histMuller[i]);
			hist.addData("Uniform", histUniform[i]);
		}
		hist.displayWindow();
	}
	
	public static void testNormi(){
		Random rand = new Random(0);
/*		for(int i = 0; i < 10; i++){
			double r = rand.nextDouble();
			System.out.println(r + " = " + normi(r));
		}
*/		
		GraphPanel graph = new GraphPanel("distros", "x", "y", true, true);
		graph.addSeries("regular", Color.GREEN.darker());
		graph.addSeries("modified", Color.BLUE);
		for(int i = 1; i < 99; i++){
			double r = (double)i / 100;
			graph.addData("regular", normi(r));
			graph.addData("modified", normi(Math.pow(r * 1.0, Math.exp(1))));
		}
		graph.displayWindow();
/*		System.out.println("0+ = " + normi(0.0000000000000001));
		System.out.println("0.5 = " + normi(0.5));
		System.out.println("1- = " + normi(0.9999999999999999));
*/
	}

}
