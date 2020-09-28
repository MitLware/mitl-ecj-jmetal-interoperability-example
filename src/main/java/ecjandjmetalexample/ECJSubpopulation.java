package ecjandjmetalexample;

import ec.EvolutionState;
import ec.Subpopulation;
import ec.vector.BitVectorIndividual;

///////////////////////////////////

@SuppressWarnings("serial")
public class ECJSubpopulation extends Subpopulation {
	
	@Override
	public void populate(EvolutionState evolutionState, int thread) {
		
		EvolutionStateWithEval state = (EvolutionStateWithEval)evolutionState;
		individuals.addAll(state.initialPopulation);
//		for(int i=0, len = initialSize; i< len; i++){
//			BitVectorIndividual newIndividual = (BitVectorIndividual)state.initialIndividual.clone();
//			newIndividual.defaultMutate(state, 0);
//			individuals.add(newIndividual);
//		}
	}
}

// End ///////////////////////////////////////////////////////////////
