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
  public NSGAIIIYYMSA(Problem<MSASolution> problem, int maxIterations, int populationSize, int div1, int div2, boolean normalize,
                   CrossoverOperator<MSASolution> crossoverOperator,
                   MutationOperator<MSASolution> mutationOperator,
                   SelectionOperator<List<MSASolution>, MSASolution> selectionOperator,
                   SolutionListEvaluator<MSASolution> evaluator) {
      super(problem, maxIterations, populationSize, div1, div2, normalize, crossoverOperator, mutationOperator, selectionOperator, evaluator);

  }

  protected List<MSASolution> createInitialPopulation() {
    return ((MSAProblem) problem_).createInitialPopulation(populationSize_);
  }
}
