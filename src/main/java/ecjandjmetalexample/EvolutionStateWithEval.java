package ecjandjmetalexample;

import java.util.List;

import org.mitlware.mutable.Evaluate;
import org.mitlware.solution.bitvector.BitVector;

import ec.simple.SimpleEvolutionState;
import ec.vector.BitVectorIndividual;

@SuppressWarnings("serial")
public final class EvolutionStateWithEval extends SimpleEvolutionState {
	public Evaluate.Directional< BitVector, Double > eval;
//	public BitVectorIndividual initialIndividual;
	public List< BitVectorIndividual > initialPopulation;	
}

// End ///////////////////////////////////////////////////////////////
