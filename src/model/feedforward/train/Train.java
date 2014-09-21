
package model.feedforward.train;

import model.feedforward.FeedforwardNetwork;


public interface Train {

	/**
	 * Get the current error percent from the training.
	 * @return The current error.
	 */
	public double getError();

	/**
	 * Get the current best network from the training.
	 * @return The best network.
	 */
	public FeedforwardNetwork getNetwork();

	/**
	 * Perform one iteration of training.
	 */
	public void iteration();
}
