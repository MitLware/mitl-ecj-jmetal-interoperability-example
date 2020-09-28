package ecjandjmetalexample

///////////////////////////////////

import org.mitlware._
import org.mitlware.mutable._

import org.mitlware.problem.bitvector._
import org.mitlware.solution.bitvector.BitVector

import org.mitlware.hyperion3.mutable._

///////////////////////////////////

object Main {

  def main(args: Array[String]): Unit = {
    
    val seed = 0xDEADBEEF
    val numBits = 32
    val maxEvaluations = 100

    ///////////////////////////////
    
    val rng = new scala.util.Random( seed )
    
    implicit val workspace = Workspace()
    val varFactory = new WorkspaceVars.Factory

    val iterCount: ReadWrite[Long] = varFactory.readWrite( 0 )
    val startTime: ReadWrite[Long] = varFactory.readWrite( System.currentTimeMillis() )
    
    ///////////////////////////////
    
		val eval = new BitVectorProblems.Onemax()
    
    val isFinished = new IsFinished[BitVector] { 
      override def apply(x: BitVector): java.lang.Boolean =
        iterCount.get == maxEvaluations || eval(x) == 1.0      
    }
    
    val prefer = Prefer.from(eval)
    val accept = Accept.improving( eval )
    
    val perturb = new Perturb[BitVector] {

      val ecjPerturb = new ECJBitVectorPerturb(numBits, eval, 100, maxEvaluations )
      val jmetalPerturb = new JMetalBitVectorPerturb(100, maxEvaluations, new org.uma.jmetal.problem.singleobjective.OneMax())

      /////////////////////////////
      
      // alternate between running ECJ and JMetal as perturbations: 
      override def apply(sol: BitVector): BitVector = iterCount.get % 2 match { 
        case 0 => jmetalPerturb(sol)
        case 1 => ecjPerturb(sol)        
      } 
    }
    
    ///////////////////////////////
    
    val search = org.mitlware.hyperion3.mutable.LocalSearch.returnBest(
        perturb, 
        accept, 
        isFinished, 
        prefer, 
        iterCount, 
        startTime)
        
    val initial = BitVector.fromInt( rng.nextInt() )        
    val best = search( initial )

    println( s"best solution: $best" )
    println( "All done." )    
  }
}

// End ///////////////////////////////////////////////////////////////
