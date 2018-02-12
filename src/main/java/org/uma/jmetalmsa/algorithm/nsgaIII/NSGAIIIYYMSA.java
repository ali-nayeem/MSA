package org.uma.jmetalmsa.algorithm.nsgaIII;

import org.uma.jmetalmsa.algorithm.algoyy.NSGAIIIYY;
//import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.solution.MSASolution;

import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * Created by ajnebro on 21/10/16.
 */
public class NSGAIIIYYMSA extends NSGAIIIYY<MSASolution> {
  boolean removePrecomputedSolutions = false;
  public NSGAIIIYYMSA(Problem<MSASolution> problem, int maxIterations, int div1, int div2, boolean normalize,
                   CrossoverOperator<MSASolution> crossoverOperator,
                   MutationOperator<MSASolution> mutationOperator,
                   SelectionOperator<List<MSASolution>, MSASolution> selectionOperator,
                   SolutionListEvaluator<MSASolution> evaluator) {
      super(problem, maxIterations, div1, div2, normalize, crossoverOperator, mutationOperator, selectionOperator, evaluator);

  }
  
  public NSGAIIIYYMSA(Problem<MSASolution> problem, int maxIterations, int div1, int div2, boolean normalize,
                   CrossoverOperator<MSASolution> crossoverOperator,
                   MutationOperator<MSASolution> mutationOperator,
                   SelectionOperator<List<MSASolution>, MSASolution> selectionOperator,
                   SolutionListEvaluator<MSASolution> evaluator, boolean removePrecomuted) {
      super(problem, maxIterations, div1, div2, normalize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
      removePrecomputedSolutions = removePrecomuted;

  }

  protected List<MSASolution> createInitialPopulation() {
    //List<MSASolution> initPop = ((MSAProblem) problem_).createInitialPopulation(populationSize_);
      if (removePrecomputedSolutions)
      {
          int numOfPrecomputerSol = ((MSAProblem) problem_).getNumberOfPrecomputerSol();
          List<MSASolution> initPop = ((MSAProblem) problem_).createInitialPopulation(populationSize_ + numOfPrecomputerSol);
                    
          for (int i = 0; i < numOfPrecomputerSol; i++)
          {
            initPop.remove(0);
          }
          
          return initPop;
      }
      else
      {
          return ((MSAProblem) problem_).createInitialPopulation(populationSize_);
      }
    
  }
}
