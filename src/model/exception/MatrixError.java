
package model.exception;

/**
 * MatrixError: Used by the model.matrix classes to indicate an error.
 *
 */
public class MatrixError extends RuntimeException {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -8961386981267748942L;

	/**
	 * Construct this model.exception with a message.
	 * @param t The other model.exception.
	 */
	public MatrixError(final String message) {
		super(message);
	}

	/**
	 * Construct this model.exception with another model.exception.
	 * @param t The other model.exception.
	 */
	public MatrixError(final Throwable t) {
		super(t);
	}

}
