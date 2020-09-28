package ecjandjmetalexample;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.BitVectorIndividual;

///////////////////////////////////

@SuppressWarnings({ "serial" })
public class ECJProblem extends Problem implements SimpleProblemForm {
		
//	private static BitVector ecjToMitlIndividual(BitVectorIndividual ecjFormat){
//		return BitVector.fromBinaryString( ecjFormat.genotypeToStringForHumans() );
//	}
	
	///////////////////////////////
		
	@Override
	public void evaluate(final EvolutionState state,
		final Individual ind,
		final int subpopulation,
		final int threadnum ) {

		if (ind.evaluated) 
			return;
		
		if (!(ind instanceof BitVectorIndividual))
			state.output.fatal("Not an BitVectorIndividual", null);
		BitVectorIndividual ind2 = (BitVectorIndividual)ind;

		final double evaluationOutput = ((EvolutionStateWithEval)state).eval.apply(
				ECJBitVectorPerturb.ecjIndividualToMitlBitVector( ind ) );

		if ( !(ind2.fitness instanceof SimpleFitness) )
			state.output.fatal("Not a SimpleFitness",null);

		((SimpleFitness)ind2.fitness).setFitness(state, evaluationOutput, false);

		ind2.evaluated = true;
	}
}

// End ///////////////////////////////////////////////////////////////
