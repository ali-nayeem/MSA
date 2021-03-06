//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetalmsa.runner;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder.NSGAIIVariant;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.AlgorithmRunner;
//import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalmsa.algorithm.nsgaii.NSGAIIMSABuilder;
import org.uma.jmetalmsa.crossover.SPXMSACrossover;
import org.uma.jmetalmsa.mutation.ShiftClosedGapsMSAMutation;
import org.uma.jmetalmsa.score.impl.*;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.Standard_MSAProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.util.distancematrix.impl.PAM250;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII45;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;
import org.uma.jmetalmsa.algorithm.nsgaIII.NSGAIIIMSABuilder;
import org.uma.jmetalmsa.algorithm.nsgaIII.NSGAIIIYYMSA;
import org.uma.jmetalmsa.algorithm.nsgaii.NSGAII45MSA;
import org.uma.jmetalmsa.score.Score;

/**
 * Class to configure and run the MOSAStrE (NSGA-II) algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIRunnerMAN {
  /**
   * Arguments: msaFileName,  dataDirectory, maxEvaluations populationSize
   * @param args Command line arguments.
   */
  public static void main(String[] args) throws Exception {
    JMetalRandom.getInstance().setRandomGenerator(new MersenneTwisterGenerator(1234));
    //JMetalRandom.getInstance().setRandomGenerator(SynchronizedMersenneTwister.getInstance());
    //JMetalRandom.getInstance().setSeed(1234);  
    MSAProblem problem;
    Algorithm<List<MSASolution>> algorithm;
    CrossoverOperator<MSASolution> crossover;
    MutationOperator<MSASolution> mutation;
    SelectionOperator selection;

    //if (args.length != 4) {
    //  throw new JMetalException("Wrong number of arguments") ;
   // }

    String problemName = "R0"; //BB30009, BB11001 23S.E
    String dataDirectory = "dataset/100S";
    Integer maxEvaluations = 500;
    Integer populationSize = 50;
    int div1 = 4;
    int div2 = 3;
    int numberOfCores;
    if (args.length != 0) {
      numberOfCores = Integer.parseInt(args[0]) ;
    }
    else
    {
        numberOfCores = Runtime.getRuntime().availableProcessors();
    }
    

    crossover = new SPXMSACrossover(0.8);
    mutation = new ShiftClosedGapsMSAMutation(0.2);
    selection = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());

    List<Score> scoreList = new ArrayList<>();

    scoreList.add(new EntropyScore());
    scoreList.add(new NumberOfAlignedColumnsScore());
    //scoreList.add(new NumberOfGapsScore());
    scoreList.add(new SimilarityGapsScore());
    scoreList.add(new SimilarityNonGapsScore());
    scoreList.add(new GapConcentrationScore());

    problem = new SATE_MSAProblem(problemName, dataDirectory, scoreList);

    SolutionListEvaluator<MSASolution> evaluator;

    if (numberOfCores == 1) {
      evaluator = new SequentialSolutionListEvaluator<>();

    } else {
      evaluator = new MultithreadedSolutionListEvaluator(numberOfCores, problem);
    }

//    algorithm = new NSGAIIIMSABuilder(problem)
//            .setCrossoverOperator(crossover)
//            .setMutationOperator(mutation)
//            .setSelectionOperator(selection)
//            .setMaxIterations(maxEvaluations/populationSize)
//            .setPopulationSize(populationSize)
//            .setSolutionListEvaluator(evaluator)
//            .build();
    
    algorithm = new NSGAIIMSABuilder(problem, crossover, mutation, NSGAIIVariant.NSGAII, true)
            .setSelectionOperator(selection)
            .setMaxEvaluations(maxEvaluations)
            .setPopulationSize(populationSize)
            .setSolutionListEvaluator(evaluator)
            .build();    
    
    //algorithm = new NSGAII45MSA(problem, maxEvaluations, populationSize, crossover, mutation, selection, evaluator );
    //algorithm = new NSGAIIIYYMSA(problem, maxEvaluations, div1, div2, true, crossover, mutation, new RandomSelection<>(), evaluator );


    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    List<MSASolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    
//    DefaultFileOutputContext funFilePre = new  DefaultFileOutputContext("FUN." + problemName +"." + algorithm.getName()+ "_pre.tsv");
//    funFilePre.setSeparator("\t");
//    new SolutionListOutput(population)
//            .setFunFileOutputContext(funFilePre)
//            .print();
    
//    for (MSASolution solution : population) {
//      for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
//        if (!scoreList.get(i).isAMinimizationScore()) {
//          solution.setObjective(i, -1.0 * solution.getObjective(i));
//        }
//      }
//    }
       
    DefaultFileOutputContext varFile = new  DefaultFileOutputContext("VAR." + problemName +"." + algorithm.getName()+ ".tsv");
    varFile.setSeparator("\n");
    DefaultFileOutputContext funFile = new  DefaultFileOutputContext("FUN." + problemName +"." + algorithm.getName()+ ".tsv");
    funFile.setSeparator("\t");

   
    new SolutionListOutput(population)
            .setVarFileOutputContext(varFile)
            .setFunFileOutputContext(funFile)
            .print();
    evaluator.shutdown();
  }
}
