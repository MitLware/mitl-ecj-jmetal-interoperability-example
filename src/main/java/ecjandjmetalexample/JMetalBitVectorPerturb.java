package ecjandjmetalexample;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mitlware.mutable.Perturb;
import org.mitlware.solution.bitvector.BitVector;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

///////////////////////////////////

public final class JMetalBitVectorPerturb implements Perturb< BitVector > {
	
	private static BinarySet mitlBitVectorToJMetalBinarySet(BitVector x) {
		BinarySet result = new BinarySet(x.length());
		for( int i=0; i<x.length(); ++i )
			result.set( i, x.get(i) );
		return result;
	}
	
	private static final BinarySolution 
	mitlBitVectorToJMetalBinarySolution(BitVector x, BinaryProblem problem) {
		BinarySolution result = new DefaultBinarySolution(problem);
		result.setVariableValue(0, mitlBitVectorToJMetalBinarySet(x));
		return result;
	}

	private static final BitVector 
	jMetalBinarySolutionToMitlBitVector(BinarySolution x) {
		if( x.getNumberOfVariables() != 1 )
			throw new IllegalArgumentException();
		
		return new BitVector( x.getVariableValue(0), 0, x.getTotalNumberOfBits() );
	}
	
	///////////////////////////////
	
	private static final GenerationalGeneticAlgorithm< BinarySolution >
	createAlgorithm( List<BinarySolution> incumbent,
		BinaryProblem problem,
		int populationSize, int maxEvaluations, int numBits ) {
		
		CrossoverOperator<BinarySolution> crossoverOperator =  new SinglePointCrossover(0.9);
		MutationOperator<BinarySolution> mutationOperator = new BitFlipMutation( 1.0 / numBits );
		SelectionOperator<List< BinarySolution >, BinarySolution > selectionOperator = 
			new BinaryTournamentSelection< BinarySolution >();
		SolutionListEvaluator<BinarySolution> eval = new SequentialSolutionListEvaluator<BinarySolution>();

		return new GenerationalGeneticAlgorithm< BinarySolution >(
			problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, eval ) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected List<BinarySolution> createInitialPopulation() {
				return incumbent;
			}
		};
	}
	
	///////////////////////////////	

	private final int populationSize; 
	private final int maxEvaluations;
	private final BinaryProblem problem;
	
	public JMetalBitVectorPerturb(int populationSize, int maxEvaluations, BinaryProblem problem) {
		this.populationSize = populationSize;
		this.maxEvaluations = maxEvaluations; 
		this.problem = problem;
	}
	
	///////////////////////////////
	
	private List< BitVector > 
	applyImpl(List<BitVector> incumbentPop) {

		if( incumbentPop.isEmpty() )
			return incumbentPop;
		else {
			final int numBits = incumbentPop.get(0).length();
	
			List< BinarySolution > jmetalIncumbentPop = incumbentPop.stream().map( 
				g -> mitlBitVectorToJMetalBinarySolution(g, problem) ).collect(Collectors.toList());
			
			GenerationalGeneticAlgorithm< BinarySolution 
				> algorithm = createAlgorithm( jmetalIncumbentPop, problem, populationSize, maxEvaluations, numBits);		
			new AlgorithmRunner.Executor(algorithm).execute();
			return algorithm.getPopulation().stream().map( 
				g -> jMetalBinarySolutionToMitlBitVector(g) ).collect(Collectors.toList());
		}
	}
	
	///////////////////////////////	
	
	@Override
	public BitVector 
	apply(BitVector incumbent) {
		return applyImpl(Collections.singletonList(incumbent)).get(0);
	}
}

// End ///////////////////////////////////////////////////////////////

