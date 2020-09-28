package ecjandjmetalexample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import org.mitlware.mutable.Evaluate;
import org.mitlware.mutable.Perturb;
import org.mitlware.solution.bitvector.BitVector;

import ec.Evolve;
import ec.Individual;
import ec.util.ParameterDatabase;
import ec.vector.BitVectorIndividual;

///////////////////////////////////

public final class ECJBitVectorPerturb implements Perturb< BitVector > {

	static BitVectorIndividual mitlBitVectorToECJIndividual(BitVector x) {
		String asString = x.toString();
		BitVectorIndividual result = new BitVectorIndividual();
		boolean [] genome = new boolean [ asString.length() ];
		for(int i=0; i<asString.length(); ++i )
			genome[i] = asString.charAt(i) != '0';
		
		result.genome = genome;
		return result;
	}

	static BitVector ecjIndividualToMitlBitVector(Individual x) {
		return BitVector.fromBinaryString( x.genotypeToStringForHumans() );
	}
	
	///////////////////////////////	
	
	private static Hashtable< String, String > staticParams = new Hashtable<String,String>();
	private Hashtable< String, String > dynamicParams;
	private Evaluate.Directional< BitVector, Double > eval;
	
	///////////////////////////

	static {
		staticParams.put("select.tournament.size", "2");
		staticParams.put("init", "ec.simple.SimpleInitializer");
		staticParams.put("finish", "ec.simple.SimpleFinisher");
		staticParams.put("breed", "ec.simple.SimpleBreeder");
		staticParams.put("eval", "ec.simple.SimpleEvaluator");
		staticParams.put("exch", "ec.simple.SimpleExchanger");
		staticParams.put("stat", "ec.simple.SimpleStatistics");
		staticParams.put("pop", "ec.Population");
		staticParams.put("pop.subpops","1");
		staticParams.put("pop.subpop.0","ec.Subpopulation");
		staticParams.put("pop.subpop.0.species", "ec.vector.BitVectorSpecies");
		staticParams.put("pop.subpop.0.species.ind", "ec.vector.BitVectorIndividual");
		staticParams.put("pop.subpop.0.species.crossover-type", "one");
		staticParams.put("pop.subpop.0.species.mutation-type", "flip");
		staticParams.put("pop.subpop.0.species.mutation-prob", "0.01"); 
		staticParams.put("pop.subpop.0.species.fitness", "ec.simple.SimpleFitness"); 
		staticParams.put("pop.subpop.0.species.pipe", "ec.vector.breed.VectorMutationPipeline");
		staticParams.put("pop.subpop.0.species.pipe.source.0", "ec.vector.breed.VectorCrossoverPipeline");
		staticParams.put("pop.subpop.0.species.pipe.source.0.source.0", "ec.select.TournamentSelection");
		staticParams.put("pop.subpop.0.species.pipe.source.0.source.1", "ec.select.TournamentSelection");
		staticParams.put("stat.file", "$out.stat");
		staticParams.put("pop.subpop.0.duplicate-retries", "0");
		staticParams.put("breedthreads","1");
		staticParams.put("evalthreads", "1");
		staticParams.put("checkpoint", "false");
		staticParams.put("checkpoint-modulo", "1");
		staticParams.put("checkpoint-prefix", "ec");
		staticParams.put("seed.0", "time");
		staticParams.put("eval.problem", "ecjandjmetalexample.ECJProblem"); 
		staticParams.put("state", "ecjandjmetalexample.EvolutionStateWithEval");
		staticParams.put("silent", "true" );		
	};

	///////////////////////////////
	
	public ECJBitVectorPerturb(int numBits, 
			Evaluate.Directional<BitVector, Double > eval,
			int populationSize,
			int maxEvaluations ) {

		this.eval = eval;

		dynamicParams = new Hashtable<String,String>(); 
		dynamicParams.put("pop.subpop.0.species.genome-size", new Integer(numBits).toString());
		dynamicParams.put("pop.subpop.0.size", new Integer(populationSize).toString());
		dynamicParams.put("generations", new Integer(maxEvaluations).toString());
	}

	///////////////////////////////
	
	private List< BitVector > 
	runEcj(List< BitVector > incumbent) {

		dynamicParams.putAll(staticParams);

		ParameterDatabase parameterDatabase = null;
		try {
			parameterDatabase = new ParameterDatabase(dynamicParams);
		} catch (FileNotFoundException e) {
			throw new RuntimeException( e );
		} catch (IOException e) {
			throw new RuntimeException( e );
		}

		EvolutionStateWithEval state;
		state = (EvolutionStateWithEval)Evolve.initialize(parameterDatabase, 0);
		state.eval = eval;
		state.initialPopulation = incumbent.stream().map( 
			i -> mitlBitVectorToECJIndividual(i) ).collect(Collectors.toList());
		
		state.run( EvolutionStateWithEval.C_STARTED_FRESH );

		Individual bestIndividual = state.population.subpops.get(0).individuals.get(0);

		for( int i=1;i<state.population.subpops.get(0).individuals.size(); ++i ){
			Individual currentIndividual = state.population.subpops.get(0).individuals.get(i);
			if (currentIndividual.compareTo(bestIndividual)>0)
				bestIndividual = currentIndividual;
		}

		Evolve.cleanup(state); 
		return state.population.subpops.get(0).individuals.stream().map( 
				i -> ecjIndividualToMitlBitVector(i) ).collect(Collectors.toList());
	}

	///////////////////////////////
	
	@Override
	public BitVector apply(BitVector  incumbent) {
		return runEcj( Collections.singletonList(incumbent) ).get(0); 
	}
}

// End ///////////////////////////////////////////////////////////////
