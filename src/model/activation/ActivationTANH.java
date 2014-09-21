
package model.activation;

import model.util.BoundNumbers;

public class ActivationTANH implements ActivationFunction {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 9121998892720207643L;

	/**
	 * A threshold function for a neural network.
	 * @param The input to the function.
	 * @return The output from the function.
	 */
	public double activationFunction(double d) {
		final double result = (BoundNumbers.exp(d*2.0)-1.0)/(BoundNumbers.exp(d*2.0)+1.0);
		return result;
	}
	
	/**
	 * Some training methods require the derivative.
	 * @param The input.
	 * @return The output.
	 */
	public double derivativeFunction(double d) {
		return( 1.0-Math.pow(activationFunction(d), 2.0) );
	}

}
