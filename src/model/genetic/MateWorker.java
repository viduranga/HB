
package model.genetic;

import java.util.concurrent.Callable;

public class MateWorker<CHROMOSME_TYPE extends Chromosome<?, ?>> implements
		Callable<Integer> {
	private final CHROMOSME_TYPE mother;
	private final CHROMOSME_TYPE father;
	private final CHROMOSME_TYPE child1;
	private final CHROMOSME_TYPE child2;

	public MateWorker(final CHROMOSME_TYPE mother, final CHROMOSME_TYPE father,
			final CHROMOSME_TYPE child1, final CHROMOSME_TYPE child2) {
		this.mother = mother;
		this.father = father;
		this.child1 = child1;
		this.child2 = child2;
	}

	@SuppressWarnings("unchecked")
	public Integer call() throws Exception {
		this.mother.mate((Chromosome)this.father, 
				(Chromosome)this.child1, 
				(Chromosome)this.child2);
		return null;
	}

}
