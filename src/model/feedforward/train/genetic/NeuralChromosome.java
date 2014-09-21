
package model.feedforward.train.genetic;

import model.exception.NeuralNetworkError;
import model.feedforward.FeedforwardNetwork;
import model.genetic.Chromosome;
import model.genetic.GeneticAlgorithm;
import model.matrix.MatrixCODEC;

import java.util.Arrays;

abstract public class NeuralChromosome<GA_TYPE extends GeneticAlgorithm<?>>
		extends Chromosome<Double, GA_TYPE> {

	private static final Double ZERO = Double.valueOf(0);
	private static final double RANGE = 20.0;

	private FeedforwardNetwork network;

	/**
	 * @return the network
	 */
	public FeedforwardNetwork getNetwork() {
		return this.network;
	}

	public void initGenes(final int length) {
		final Double result[] = new Double[length];
		Arrays.fill(result, ZERO);
		this.setGenesDirect(result);
	}

	/**
	 * Mutate this chromosome randomly
	 */
	@Override
	public void mutate() {
		final int length = getGenes().length;
		for (int i = 0; i < length; i++) {
			double d = getGene(i);
			final double ratio = (int) ((RANGE * Math.random()) - RANGE);
			d*=ratio;
			setGene(i,d);
		}
	}



	/**
	 * Set all genes.
	 * 
	 * @param list
	 *            A list of genes.
	 * @throws NeuralNetworkException
	 */
	@Override
	public void setGenes(final Double[] list) throws NeuralNetworkError {

		// copy the new genes
		super.setGenes(list);

		calculateCost();
	}

	/**
	 * @param network
	 *            the network to set
	 */
	public void setNetwork(final FeedforwardNetwork network) {
		this.network = network;
	}

	public void updateGenes() throws NeuralNetworkError {
		this.setGenes(MatrixCODEC.networkToArray(this.network));
	}

	public void updateNetwork() {
		MatrixCODEC.arrayToNetwork(getGenes(), this.network);
	}

}
