package uk.co.crider.jablus.utils;

import java.io.IOException;
import java.util.Random;

public class Utils {
	
	/** Rounds the number to 4 significant digits */
	public static double round(double d) {
		if (d == 0.0)
			return d;
		int digits = (int) (Math.log(d) / Math.log(10));
		return round(d, 3 - digits);
	}

	/** Rounds the number to n decimal places */
	public static double round(double d, int n) {
		double shift = Math.pow(10, n);
		return ((double) ((long) (d * shift + 0.5))) / shift;
	}
	
	/** Returns the value of n, as a probabilistically rounded int.
	 The returned value will be ceiling(n) with probability p, otherwise
	 the returned value will be floor(n). Probability p is given to be:
	 p = n - floor(n). Thus the closer n is to ceiling(n) the higher 
	 the probability is that the result will be ceiling(n).
	 see Gladkin 1975
	 */
	public static int pRound(double n){ return pRound(n, new Random()); }
	public static int pRound(double n, Random r){
		int f = (int)n; // Floor
		// If random is less than probability p
		if(r.nextDouble() < (n - f))
			return f + 1; // Return ceiling
		// Return floor
		return f;
	}
	
	/** Rounds the number returning a string representation of the numner */
	public static String roundString(double d){
		if (d == 0.0)
			return "" + d;
		int digits = (int) (Math.log(d) / Math.log(10));
		int n = 3 - digits;
		return fixBug("" + round(d, n), n);
	}
	public static String roundString(double d, int n){
		return fixBug("" + round(d, n), n);
	}
	// workaround for java's rounding bug
	private static String fixBug(String s, int n){
		if(n >= 3){
			int dotIndex = s.indexOf('.');
			if(dotIndex != -1)
				while((s.charAt(s.length() -1) == '0') && dotIndex < s.length() - 2)
					s = s.substring(0, s.length() - 1);
		}
		// Remove the .0 part if n == 0
		if(n == 0){
			return s.substring(0, s.indexOf("."));
		}
		return s;
	}
/*	public static String align(int n, int chrs){
		StringBuffer s = new StringBuffer(chrs);
		for(int i = 0; i < 10; i++) s.append(' ');
		String ns = "" + n;
		int len = ns.length();
		s.insert(chrs - len, ns);
		return s.toString();
	}
	public static String align(double n, int chrs){
		StringBuffer s = new StringBuffer(chrs);
		for(int i = 0; i < 10; i++) s.append(' ');
		String ns = "" + n;
		int len = ns.length();
		s.insert(chrs - len, ns);
		return s.toString();
	}

	5 = 0
	10 = 1
	15 = 1
	20 = 2
	25 = 2
	30+ = 3
	10 20 30

*/
	/**
	 Returns index i into iArray with i = ...
	 0 for v < iArray[0]
	 i for iArray[i - 1] <= v < iArray[i] (where 0 < i < n)
	 n for iArray[n - 1] <= v (where n = iArray.length)
	 The main purpose of the function is to establish
	 whether v falls between a non-linear set of intervals
	 */
	public static int index(int v, int[] iArray){
		for(int i = 0; i < iArray.length; i++){
			if(v < iArray[i])
				return i;
		}
		return iArray.length;
	}
	
	public static boolean parseBoolean(String s){
		if(s != null){
			try{ return Boolean.parseBoolean(s); }
			catch(NumberFormatException e){} // Do nothing
		}
		return false;
	}
	public static int parseInt(String s){
		if(s != null){
			try{ return Integer.parseInt(s); }
			catch(NumberFormatException e){} // Do nothing
		}
		return 0;
	}
	public static double parseDouble(String s){
		if(s != null){
			try{ return Double.parseDouble(s); }
			catch(NumberFormatException e){} // Do nothing
		}
		return 0;
	}

	public static String arrayString(int[] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			buf.append(data[i] + " ");
		}
		return buf.toString();
	}
	public static String arrayString(int[][] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
				buf.append(arrayString(data[i]) + "\n");
		}
		return buf.toString();
	}
	public static String arrayString(int[][][] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
				buf.append(arrayString(data[i]) + "\n");
		}
		return buf.toString();
	}
	public static String arrayString(double[] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			buf.append(data[i] + " ");
		}
		return buf.toString();
	}
	public static String arrayString(double[][] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
				buf.append(arrayString(data[i]) + "\n");
		}
		return buf.toString();
	}
	public static String arrayStringR(double[] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			buf.append(round(data[i]) + " ");
		}
		return buf.toString();
	}
	public static String arrayStringR(double[][] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[i].length; j++){
				buf.append(round(data[i][j], 2) + " ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	public static String arrayStringR(float[][] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[i].length; j++){
				buf.append(round(data[i][j], 2) + " ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	public static String arrayString(String[] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			buf.append(data[i] + " ");
		}
		return buf.toString();
	}
	public static String arrayString(String[][] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[i].length; j++){
				buf.append(data[i][j] + " ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	public static String arrayString(Object[] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			buf.append(data[i] + " ");
		}
		return buf.toString();
	}
	public static String arrayString(Object[][] data){
		if(data == null) return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[i].length; j++){
				buf.append(data[i][j] + " ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	
	public static int[] intArray(String data){
		if(data == null || data.equals(""))
			return new int[0];
		String[] split = data.split(" ");
		int[] r = new int[split.length];
		for(int i = 0; i < r.length; i++){
			try{ r[i] = Integer.parseInt(split[i]); }
			catch(NumberFormatException e){ r[i] = 0; }
		}
		return r;
	}
	public static double[] doubleArray(String data){
		if(data == null || data.equals(""))
			return new double[0];
		String[] split = data.split(" ");
		double[] r = new double[split.length];
		for(int i = 0; i < r.length; i++){
			try{ r[i] = Double.parseDouble(split[i]); }
			catch(NumberFormatException e){ r[i] = 0; }
		}
		return r;
	}
	public static String[] stringArray(String s){
		if(s == null || s.equals(""))
			return new String[0];
		return s.split(" ");
	}
	
	// Correct java's problem with mod of negative numbers
	public static int mod(int n, int m){
		int r = n % m;
		return r < 0 ? r + m : r;
	}
	
	public static double[] copy(double[] in){
		if(in == null) return null;
		double[] out = new double[in.length];
		return copy(in, out);
	}
	public static double[][] copy(double[][] in){
		if(in == null) return null;
		double[][] out = new double[in.length][];
		for(int i = 0; i < in.length; i++)
			out[i] = copy(in[i]);
		return out;
	}
	public static double[] copy(double[] in, double[] out){
		if(in == null) return null;
		for(int i = 0; i < in.length; i++)
			out[i] = in[i];
		return out;
	}
	public static double[][] copy(double[][] in, double[][] out){
		if(in == null) return null;
		for(int i = 0; i < in.length; i++)
			copy(in[i], out[i]);
		return out;
	}
	/** Returns true if the array contains the value n */
	public static boolean contains(int[] data, int n){
		for(int i = 0; i < data.length; i++)
			if(data[i] == n) return true;
		return false;
	}
    /** Calculates sum of n items up to position i in the array */
    public static double sum(double[] data, int i_, int n_){
    	double total = 0;
    	for(int i = i_, n = 0; n < n_; i = i > 0 ? i - 1 : data.length - 1, n++){
    		total += data[i];
    	}
//System.out.println("i_=" + i_ + ", n_=" + n_ + ", Total of:\n" + Utils.arrayString(data) + " \n =" + total);
    	return total;
    }
    /** Calculates the sum of all items in the array */
    public static double sum(double[] data){
    	return sum(data, 0, data.length);
    }
    /** Calculates the sum of all items in the array */
    public static double sum(double[][] data){
    	double total = 0;
    	for(int i = 0; i < data.length; i++){
    		total += sum(data[i]);
    	}
    	return total;
    }
    /** Calculates the sum of all items in the array */
    public static int sum(int[] data){
    	int total = 0;
    	for(int i = 0; i < data.length; i++){
    		total += data[i];
    	}
    	return total;
    }
    /** Calculates the sum of all items in the array */
    public static int sum(int[][] data){
    	int total = 0;
    	for(int i = 0; i < data.length; i++){
    		total += sum(data[i]);
    	}
    	return total;
    }
    /** Calculates sum of products of n elements in input arrays, up to position i */
    public static double sumProd(double[] v1, double[] v2, int i_, int n_){
//System.out.println("i_=" + i_ + ", n_=" + n_ + ", Multiplying:\n" + Utils.arrayString(v1) + " \nby\n" + Utils.arrayString(v2));
    	double total = 0;
    	for(int i = i_, n = 0; n < n_; i = i > 0 ? i - 1 : v1.length - 1, n++){
    		total += v1[i] * v2[i];
    	}
//System.out.println("answer= " + total);
    	return total;
    }
    
    public static int[][] perm(int len){
		int [][] pm = new int[factorial(len)][len];
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < len; i++) buf.append(i);
		String perms = perm1(buf.toString());
		for(int i = 0, j = 0, k = 0; i < perms.length(); i++, k++){
			char c = perms.charAt(i);
			if(c == '\n'){ j++; k = -1; }
			else pm[j][k] = Integer.parseInt("" + c);
		}
		return pm;		
	}
	// Permutation function from: 
	// Copyright © 2006, Robert Sedgewick and Kevin Wayne.
	// TODO: Get permission to use!
	// print N! permutation of the characters of the string s (in order)
	private static String perm1(String s){ return perm1("", s); }
	private static String perm1(String prefix, String s) {
		int N = s.length();
		if(N == 0) return prefix + "\n";
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < N; i++)
			buf.append(perm1(prefix + s.charAt(i), s.substring(0, i) + s.substring(i+1, N)));
		return buf.toString();
	}
	private static int factorial(int n){
		if(n == 0) return 0;
		if(n == 1) return 1;
		return n * factorial(n - 1);
	}
	
	/** Returns the binary log of a number (log to base 2) */
	public static int log2(int n){
		  int pos = 0;
		  if (n >= 1<<16) { n >>= 16; pos += 16; }
		  if (n >= 1<< 8) { n >>=  8; pos +=  8; }
		  if (n >= 1<< 4) { n >>=  4; pos +=  4; }
		  if (n >= 1<< 2) { n >>=  2; pos +=  2; }
		  if (n >= 1<< 1) {           pos +=  1; }
		  return ((n == 0) ? (-1) : pos);
	};

	// Blocks until user presses Enter
	public static void awaitEnter(){
		try{
			int a = System.in.read();
		while(a != -1 && a != 10){
			System.out.println(a);
			a = System.in.read();
		}
		}catch(IOException e){;}
	}
	
	public static void main(String[] args){
		testLog2();
		if(true) return;
		testIndex();
Double d = new Double(0.001);
System.out.println("s=" + 0.01 + ", d=" + d.toString());
System.out.println("s=" + 0.002);
System.out.println("s=" + 0.0001);
/*		test(Math.PI);
		test(0.1);
		test(0.01);		
		test(0.001);		
		test(0.0001);		
		test(0.00001);		
*/		test2(Math.PI);
		test2(0.1);
		test2(0.01);		
		test2(0.001);		
		test2(0.0001);		
		test2(0.00001);		
	}
	public static void test(double value){
		System.out.println("Rounding " + value + " to 1 decimal place : " + Utils.round(value, 1));
		System.out.println("Rounding " + value + "to 2 decimal places: " + Utils.round(value, 2));
		System.out.println("Rounding " + value + "to 3 decimal places: " + Utils.round(value, 3));
		System.out.println("Rounding " + value + "to 4 decimal places: " + Utils.round(value, 4));
		System.out.println("Rounding " + value + "to 5 decimal places: " + Utils.round(value, 5));
	}
	public static void test2(double value){
		System.out.println("Rounding " + value + "to 1 decimal place : " + Utils.roundString(value, 1));
		System.out.println("Rounding " + value + "to 2 decimal places: " + Utils.roundString(value, 2));
		System.out.println("Rounding " + value + "to 3 decimal places: " + Utils.roundString(value, 3));
		System.out.println("Rounding " + value + "to 4 decimal places: " + Utils.roundString(value, 4));
		System.out.println("Rounding " + value + "to 5 decimal places: " + Utils.roundString(value, 5));
	}
	public static void testLog2(){
		System.out.println("log2(1 <<  0) =  " + log2(1 <<  0));
		System.out.println("log2(1 <<  1) =  " + log2(1 <<  1));
		System.out.println("log2(1 <<  2) =  " + log2(1 <<  2));
		System.out.println("log2(1 <<  3) =  " + log2(1 <<  3));
		System.out.println("log2(1 <<  5) =  " + log2(1 <<  5));
		System.out.println("log2(1 << 10) = "  + log2(1 << 10));
		System.out.println("log2(1 << 16) = "  + log2(1 << 16));
		System.out.println("log2(1 << 17) = "  + log2(1 << 17));
		System.out.println("log2(1 << 30) = "  + log2(1 << 30));
		System.out.println("log2(1 << 31) = "  + log2(1 << 31));
		for(int i = -10; i < 258; i++){
			System.out.println("i=" + i + ", log2(i)=" + log2(i));
		}
	}
	public static void testIndex(){
		System.out.println("Testing index() function...");
		int[] iArray = {10, 20, 25, 35, 40};
		System.out.println("iArray: " + arrayString(iArray));
		System.out.println("Value:  -1 => Index: " + index( -1, iArray));
		System.out.println("Value:   5 => Index: " + index(  5, iArray));
		System.out.println("Value:   9 => Index: " + index(  9, iArray));
		System.out.println("Value:  10 => Index: " + index( 10, iArray));
		System.out.println("Value:  11 => Index: " + index( 11, iArray));
		System.out.println("Value:  34 => Index: " + index( 34, iArray));
		System.out.println("Value:  35 => Index: " + index( 35, iArray));
		System.out.println("Value:  39 => Index: " + index( 39, iArray));
		System.out.println("Value:  40 => Index: " + index( 40, iArray));
		System.out.println("Value: 400 => Index: " + index(400, iArray));
	}
}
