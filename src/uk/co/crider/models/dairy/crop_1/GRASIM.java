package uk.co.crider.models.dairy.crop_1;

import java.awt.Color;

import uk.co.crider.jablus.gui.jfreechart.GraphPanel;

public class GRASIM {

	// Symbols
	// W	Total simulated weight (kgC/m²)
	// Ws	Storage dry weight (kgC/m²)
	// Wg	Structure dry weight (kgC/M²)
	// θ	Conversion from CO₂ to C (0.273 kgC/kgCO₂)
	// ϕ	Fraction available for shoot growth (0.9)
	// Y	Yield factor, then units of structure that result from the use for syntheseis of one unit of storage material, the rest being respired (0.75)
	// μ	Gross structural specific growth rate (/day)
	// μm	Upper bound of μ as Ws gets large compared to Wg (0.5 /day)
	// ɣ	Recycling coefficient (0.1 /day)
	// β	Senescence rate (0.05 /day)
	// Pg	Leaf gross photosynthetic rate
	// L	Leaf area index
	// h	Day length
	// a	Structural specific leaf area (40.0 m² leaf area / kgC in Wg)
	// I1	Light intensity over a leaf (W/m²)
	// Pm	Limit of Pg as I1 gets large (kgCO₂/m²/s)
	// α	Leaf photosynthetic efficiency (2E-9 kgCO₂/J)
	// k	Extinction coefficient of the canopy (0.5)
	// m	Leaf transmission coefficient (0.1)
	// I0	Irradiance (W/m²)
	// P0	Constant (0.5E-5 kgCO₂/m²/s)
	// P1	Constant (0.05E-5 kgCO₂/m²/s)
	// J	Day light integral (J/m²/day)
	
	// Proportion of C fixed by photosynthesis available for shoot growth: 0.9
	// Fractional C content in carbohydrate: 0.4 kgC/kgDM
	// Fraction of photosynthetic material partitioned into leaf: 0.5
	// Yield goal: 2500 kgDW/hae
	// Total N uptake: 0.02 kgN/kg harvest DM
	// Length of growing season: 90 days

	// Constants
	private static final double a = 40; // Structural specific leaf area (40.0 m² leaf area / kgC in Wg)
	private static final double α = 2E-9; // Leaf photosynthetic efficiency (2E-9 kgCO₂/J)
	private static final double k = 0.5; // Extinction coefficient of the canopy (0.5)
	private static final double m = 0.1; // Leaf transmission coefficient (0.1)
	private static final double P0 = 0.5E-5; // Constant (0.5E-5 kgCO₂/m²/s)
	private static final double P1 = 0.05E-5; // Constant (0.05E-5 kgCO₂/m²/s)
	private static final double θ = 0.273; // Conversion from CO₂ to C (0.273 kgC/kgCO₂)
	private static final double ϕ = 0.9; // Fraction available for shoot growth (0.9)
	private static final double μm = 0.5; // Upper bound of μ as Ws gets large compared to Wg (0.5 /day)
	private static final double Y = 0.75; // Yield factor, then units of structure that result from the use for syntheseis of one unit of storage material, the rest being respired (0.75)
	private static final double ɣ = 0.1; // Recycling coefficient (0.1 /day)
	private static final double β = 0.05; // Senescence rate (0.05 /day)
	// Used in temperature correction
	private static final double Tref = 18; 
	private static final double Qn = 1.5;
	private static final double n = 10;
	
	// Stocs (temporal variables)
	private double Wg = 0.05; // 1.7;
	private double Ws = 0.1 - Wg; // 1; 
	
	private GraphPanel graph;
	
	public GRASIM(){
		graph = new GraphPanel("Herbage Mass", "dat", "kgC/m2", true, true);
		graph.addSeries("W", Color.GREEN.darker());
		graph.addSeries("Wg", Color.BLUE.darker());
		graph.addSeries("Ws", Color.RED.darker());
		graph.displayWindow();
	}
	
	public void execDay(
			// Inputs
			double h, // Day length (hours)
			double J, // Day light integral (J/m²/day)
			double T, // Soil Temperature °F
			double WSF, // Water stress factor (0 to 1)
			double NSF  // Nitrogen stress factor (0 to 1)
	){
		// Convert T to Farenheight
		//T = T * 9 / 5 + 32;
		
		
/*		
		// Accumulated total sward weight is represented by the sum:
		W = Ws + Wg;
		
		// The photosynthetic input of carbon allocated to the shoot is
		ϕθP;
		
		// The gross specific growth rate for the structural component (/day) is defined as:
		μ = 1/Wg * dWg/Dt;
		// and is estimated using the relation:
		μ = μm * (Ws / W);
		
		// The rate of utilisation of storage into structure is
		(1/Y) * μ * Wg;
		
		// The recycling of structure into storage is:
		ɣWg
		
		// The respiration rate is defined as:
		R = ((1 - Y) / Y) * μ * Wg;
		
		// The senesence is calculated using the following:
		S = βWg
		
		// The daily photosynthetic rate over the plant canopy for a day length is expressed as:
		P = Integral (0 to L) Integral (0 to h) Pg dL dt
		
		// The leaf are index, L, is determined from the structure component weight using the following relation:
		L = aWg
		
		// The leaf gross photosynthetic rate is determined as:
		Pg = (α * I1 * Pm) / (α * I1 * Pm);
		
		// The light intensity over a leaf is determined from the irradiance using the following
		I1 = (k /  (1 - m)) * I0 * Math.exp(-k * L);
		
		// The maximum gross photosynthetic rate of a leaf Pm is a temperature dependent parameter snd also varies according to the relation:
		Pm = P0 + P1 * T;
		
		// The irradiance throught the day is assumed to follow the relation:
		I0 = (2 * J / h) * Math.sin(Math.PI * t / h);
		
		P = (Pm * h / k) * ??;
*/
		// Tmperature correction factors (using Q-type function
		double β = Math.pow(this.β * Tref * Qn, (T - Tref) / n);
		double μm = Math.pow(this.μm * Tref * Qn, (T - Tref) / n);
		double ɣ = Math.pow(this.ɣ * Tref * Qn, (T - Tref) / n);
		
		// The maximum gross photosynthetic rate of a leaf Pm is a temperature dependent parameter snd also varies according to the relation:
		double Pm = P0 + P1 * T;
		
		// The leaf are index, L, is determined from the structure component weight using the following relation:
		double L = a * Wg;

		// Then analytical integration of Pg over day length and canopy is possible and yield:
		double αKJh = α * k * J / h;
		double ekl = Math.exp(-k * L);
		double mPm = (1 - m) * Pm;
		double rt1 = 2 * αKJh * mPm;
		double rt2 = mPm * mPm;
		double rtn = rt1 + rt2;
		double rtd = rt1 * ekl + rt2;
		double num = αKJh + mPm + Math.sqrt(rtn);
		double den = αKJh * ekl + mPm + Math.sqrt(rtd);
		double P = (Pm * h / k) * Math.log(num/den);

		// μ	Gross structural specific growth rate (/day)
		double μ = μm * (Ws / (Ws + Wg));
		
		// Change in storage weight
		double GDNG = ϕ * θ * P - (μ * Wg / Y) + ɣ * Wg;
		
		// Change in structure weight
		double RDNG = μ * Wg * Ws / (Wg + Ws) - ɣ * Wg - β * Wg;

		// Simulate growth
		Wg = Ws + WSF * NSF * RDNG; // g = structure
		Ws += WSF * NSF * GDNG; // s = storage
		// Yield target = 2500 kg/ha = 0.25kg/m²
		double W = Wg + Ws;
		if(W > 0.25){
			double surplus = W - 0.25;
			Wg -= surplus * Wg / W;
			Ws -= surplus * Ws / W;
		}
		
		graph.addData("W", Wg + Ws);
		graph.addData("Wg", Wg);
		graph.addData("Ws", Ws);
		
		System.out.println("Wg:" + Wg + "\tWs:" + Ws);
	}
	
	
	
	
	
	
	
	/** Test Model */
	public static void main(String[] args) {
		
		GRASIM model = new GRASIM();
		
		double h = 12; // Day length (hours)
		double J = 8640000; // Day light integral (J/m²/day)
		double T = 10; // Soil Temperature °C
		double WSF = 0.5; // Water stress factor (0 to 1)
		double NSF = 0.5;  // Nitrogen stress factor (0 to 1)

		for(int i = 0; i < 190; i++){
			model.execDay(
					// Inputs
					h, // Day length (hours)
					J, // Day light integral (J/m²/day)
					T, // Soil Temperature °F
					WSF, // Water stress factor (0 to 1)
					NSF  // Nitrogen stress factor (0 to 1)
			);
//			if(i == 45) WSF = 0.01;
			System.out.println("Day:" + i);
		}
		
	}

}
