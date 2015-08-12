package uk.co.crider.jablus.models.basic;


/** Mathematical models used to generate revenue from a land use arrangement */
public class MathModels {

	public static final int RANDOM = 1;
	public static final int BASIC_ECONOMIC = 2;
	public static final int EVANS = 3;
	public static final int HOOVER_GIARRATANI = 4;

	// Land revenue models -----------------------------------------------------
	
	/** Simple case - revenue is independent of factors and simply random */
	public static double randomRevenue(){
		return Math.random(); // Using evans
	}

	/** Basic economic model, in which revenue is unit price multiplied by output */
	public static double basicEconomic(
		double P,   // Unit price at market
		double Q    // Units of output per unit area
	){
		return P * Q;
	}
	
	/** Land revenue model used in Evan's basic experiments */
	public static double evans(
			double P, // Unit price at market
			double S  // Suitability of land use
	){
		return P + S; // Using evans, basicEconomic is more realistic however
	}
	
	/** Land rent model (as in hoover and giarratani) */
	public static double hooverGiarratani(
			double P, // Unit price at market
			double Q, // Units of output per acre
			double F, // Fixed costs per acre
			double a, // Linear variable costs incurred achiving output Q
			double b, // Exponential variable costs incurred achieving output Q
			double t, // Unit transfer cost
			double x  // Distance to market
	){ // Defined for 0 < Q < inf
		return (P - t * x) * Q - Math.pow(a * Q, b) - F; 
	}
	
	
	
	// Reward mechanisms --------------------------------------------------

	public static final int REWARD_BASIC        = 10;
	public static final int REWARD_THRESHOLD    = 11;
	public static final int REWARD_POWER        = 12;
	public static final int REWARD_SIGMOID      = 13;
	
	/** Using thresholds weight payoff*/
	public static double rewardThreshold(
		double Rev,  // Gross revenue
		double Tmin, // Minimum threshold for making money
		double Tmax  // Maximum threshold above which penalties are made
	){
		if(Rev > Tmax)
			Rev = Tmax * 2 - Rev;
		return Rev < Tmin ? 0.0 : Rev;
	}

	/** Using a power function to determine payoff */
	public static double rewardPower(
		double Rev,  // Gross revenue
		double power // power revenue is raised to
	){
		return Math.pow(Rev, power);
	}
	/** Basic economic model, using a sigmoid function to determine payoff */
	public static double rewardSigmoid(
		double Rev,    // Gross revenue
		double offset, // point of highest gradient (default 0.5)
		double gradient// Steepness of gradient     (default 10)
	){
		// Apply sigmoid function to payoff
		return 1 / (1 + Math.pow(Math.E, (offset - Rev) * gradient));
	}
	

	/** Returns the produce output of each land parcel */
/*	public static double landOutput(
			double o, // Maximum possible output of land parcel
			double p  // Current productivity of land parcel
	){
		return o < 0 || p < 0 ? 0 : o * p;
	}
*/
	
	/** Describes the change in productivity on each time step */
/*	public static double landProductivity_nolimit(
			double p, // Productivity
			double d  // Change in productivity
	){
		return p + d;
	}
*/
	
	/** Describes the change in productivity on each time step 
	 * The logistic map is used to produce population growth behaviour with a carrying capacity
	 * */
	public static double landProductivity(
			double p, // Current productivity
			double r, // Change in productivity
			double l  // Maximum possible productivity
	){
		if(r == 1.0) return p; // No growth
//		if(p <= 0.0) return 0.0;
//		if(p >= l) return l;
//		if(l <= 0.00001) return 0.0;
		r = r < 0.0 ? 0.0 : r > 3.0 ? 3.0 : r;
//		p = p == 0 ? p += 0.001 : p == l ? p -= 0.001 : p;
//		p = p == 0 ? p += Double.MIN_VALUE : p == l ? p -= Double.MIN_VALUE : p;
		double K = l * r/(r - 1.0); // This sets the carrying capacity to a value such that p -> l as t -> inf
		if(K < 0)
			K = -K; // Cannot have negative K (Caused by having r < 1.0 (make positive) 
//System.out.print("r:" + r + "|l:" + l + "|K:" + K + "|P:" + p + "->");
		p = r * p * (1.0 - (p/K)); // Verhulst Equation
//		if(p <= 0.0 && r > 1.0) p = 0.00001;
//		if(p >= l && r < 1.0) p = l - 0.00001;
//if(p < 0.0 || p > l + 0.00001) System.out.println("ERROR|P:" + p + "|l:" + l + "|K:" + K + "|r:" + r);
//System.out.println(p);
		return p; 
	}
	
	// Using the Verhulst equation dP/dt = rP(1-P/K)
	// To ensure decline can hapen at max, and growth can happen at min we shift the value by
	// 0.01 to get the process started
	public static double verhulstGrowth(
			double P,
			double K,
			double r){
		if(r == 0) return P;
		P += r * P * (1 - P/K);
		return P <= 0.01 ? P + 0.01 : P > K ? K : P;
	}
	public static double verhulstDecline(
			double P,
			double K,
			double r){
		if(r == 0) return P;
		P -= r * P * (1 - P/K);
		return P >= K - 0.01 ? P - 0.01 : P < 0 ? 0 : P;
	}
	
	public static double sigmoid(double offset, double gradient, double x){
		return 1 / (1 + Math.pow(Math.E, (offset - x) * gradient));
	}
	
	
	
	/** For testing the functions */ 
	public static void main(String[] args) {
	}
}
