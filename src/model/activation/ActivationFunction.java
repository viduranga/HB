
package model.activation;

import java.io.Serializable;

public interface ActivationFunction extends Serializable {

	/**
	 * A model.activation function for a neural network.
	 * @param The input to the function.
	 * @return The output from the function.
	 */
	public double activationFunction(double d);

	/**
	 * Performs the derivative of the model.activation function function on the input.
	 * 
	 * @param d
	 *            The input.
	 * @return The output.
	 */
	public double derivativeFunction(double d);
}
