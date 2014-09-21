
package model.exception;

/**
 * NeuralNetworkError: Used by the neural network classes to 
 * indicate an error.
 *
 */
public class NeuralNetworkError extends RuntimeException {
	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 7167228729133120101L;

	/**
	 * Construct a message model.exception.
	 * 
	 * @param msg
	 *            The model.exception message.
	 */
	public NeuralNetworkError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an model.exception that holds another model.exception.
	 * 
	 * @param t
	 *            The other model.exception.
	 */
	public NeuralNetworkError(final Throwable t) {
		super(t);
	}
}
