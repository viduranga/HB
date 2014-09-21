
package model.feedforward.train.genetic;

import model.feedforward.FeedforwardNetwork;
import model.genetic.GeneticAlgorithm;

public class NeuralGeneticAlgorithm<GA_TYPE extends GeneticAlgorithm<?>>
		extends GeneticAlgorithm<NeuralChromosome<GA_TYPE>> {

	/**
	 * Get the current best neural network.
	 * @return The current best neural network.
	 */
	public FeedforwardNetwork getNetwork() {
		final NeuralChromosome<GA_TYPE> c = getChromosome(0);
		c.updateNetwork();
		return c.getNetwork();
	}

}
