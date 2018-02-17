package org.uma.jmetalmsa.algorithm.nsgaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.solution.MSASolution;

import java.util.List;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NSGAIIMSA extends NSGAIIMeasures<MSASolution> {
  private CountingMeasure evaluations;
  private DurationMeasure durationMeasure;
  private SimpleMeasureManager measureManager;

  private BasicMeasure<List<MSASolution>> solutionListMeasure;
  private BasicMeasure<Integer> numberOfNonDominatedSolutionsInPopulation;
  boolean removePrecomputed = false;

  /**
   * Constructor
   */
  public NSGAIIMSA(MSAProblem problem, int maxIterations, int populationSize,
                   CrossoverOperator<MSASolution> crossoverOperator,
                   MutationOperator<MSASolution> mutationOperator,
                   SelectionOperator<List<MSASolution>, MSASolution> selectionOperator,
                   SolutionListEvaluator<MSASolution> evaluator) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);


    initMeasures();
  }
  
  public NSGAIIMSA(MSAProblem problem, int maxIterations, int populationSize,
                   CrossoverOperator<MSASolution> crossoverOperator,
                   MutationOperator<MSASolution> mutationOperator,
                   SelectionOperator<List<MSASolution>, MSASolution> selectionOperator,
                   SolutionListEvaluator<MSASolution> evaluator, boolean removePre) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
    removePrecomputed = removePre;

    initMeasures();
  }

  @Override
  protected List<MSASolution> createInitialPopulation() {
    //return ((MSAProblem) getProblem()).createInitialPopulation(getMaxPopulationSize());
      
      if (removePrecomputed)
      {
          if(getProblem() instanceof SATE_MSAProblem)
            return ((SATE_MSAProblem)getProblem()).createInitialPopulationRandomly(getMaxPopulationSize());
          else
              throw new JMetalException("No suitable random init method.");
          /*int numOfPrecomputerSol = ((MSAProblem) getProblem()).getNumberOfPrecomputerSol();
          List<MSASolution> initPop = ((MSAProblem) getProblem()).createInitialPopulation(getMaxPopulationSize() + numOfPrecomputerSol);
                    
          for (int i = 0; i < numOfPrecomputerSol; i++)
          {
            initPop.remove(0);
          }
          
          return initPop;*/
      }
      else
      {
          return ((MSAProblem) getProblem()).createInitialPopulation(getMaxPopulationSize());
      }
  }

  /* Measures code */
  private void initMeasures() {
    durationMeasure = new DurationMeasure();
    evaluations = new CountingMeasure(0);
    numberOfNonDominatedSolutionsInPopulation = new BasicMeasure<>();
    solutionListMeasure = new BasicMeasure<>();

    measureManager = new SimpleMeasureManager();
    measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
    measureManager.setPullMeasure("currentEvaluation", evaluations);
    measureManager.setPullMeasure("numberOfNonDominatedSolutionsInPopulation", numberOfNonDominatedSolutionsInPopulation);

    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
    measureManager.setPushMeasure("currentEvaluation", evaluations);
  }

  @Override
  public String getName() {
    return "NSGAII";
  }

  @Override
  public String getDescription() {
    return "NSGAII";
  }
}
