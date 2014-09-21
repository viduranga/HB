
package model.activation;

import model.exception.NeuralNetworkError;

public class ActivationLinear implements ActivationFunction {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -5356580554235104944L;

	/**
	 * A threshold function for a neural network.
	 * @param The input to the function.
	 * @return The output from the function.
	 */
	public double activationFunction(final double d) {
		return d;
	}

	/**
	 * Some training methods require the derivative.
	 * @param The input.
	 * @return The output.
	 */
	public double derivativeFunction(double d) {
		throw new NeuralNetworkError("Can't use the linear model.activation function where a derivative is required.");
	}

}
