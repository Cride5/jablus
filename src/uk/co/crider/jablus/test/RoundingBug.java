package uk.co.crider.jablus.test;

public class RoundingBug {
	
	/* According to the Java spec...
	 * 
	 * If at least one of the operands to a numerical operator is of type double,
	 * then the operation is carried out using 64-bit floating-point arithmetic,
	 * and the result of the numerical operator is a value of type double.
	 * (If the other operand is not a double, it is first widened to type double
	 * by numeric promotion (§5.6).) Otherwise, the operation is carried out using
	 * 32-bit floating-point arithmetic, and the result of the numerical operator
	 * is a value of type float. If the other operand is not a float, it is first
	 * widened to type float by numeric promotion.
	 */

	/** Demonstrate == operator bug */
	public static void main(String[] args) {
		float a = 0.312f;
		double b = 0.31200000643730164;
		System.out.println(a + " == " + b + " is " + (a == b));
		System.out.println(
				"\nUsing VM: " 
				+ System.getProperty("java.vm.vendor") + " "
				+ System.getProperty("java.vm.name") + " "
				+ System.getProperty("java.vm.version") + "\n"
				+ "Using OS: "
				+ System.getProperty("os.name") + " "
				+ System.getProperty("os.arch") + " "
				+ System.getProperty("os.version")
		);
	}

}
