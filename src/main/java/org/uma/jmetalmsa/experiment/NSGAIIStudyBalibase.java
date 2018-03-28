package org.uma.jmetalmsa.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
//import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
//import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
//import org.uma.jmetal.problem.multiobjective.zdt.*;
//import org.uma.jmetal.qualityindicator.impl.*;
//import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
//import org.uma.jmetal.solution.DoubleSolution;
//import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

//import java.io.IOException;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
//import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;
import org.uma.jmetalmsa.algorithm.nsgaii.NSGAIIMSABuilder;
import org.uma.jmetalmsa.crossover.SPXMSACrossover;
import org.uma.jmetalmsa.mutation.ShiftClosedGapsMSAMutation;
import org.uma.jmetalmsa.problem.BAliBASE_MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.*;
import org.uma.jmetalmsa.solution.MSASolution;
//import org.uma.jmetalmsa.util.SynchronizedMersenneTwister;
import org.uma.jmetalmsa.util.distancematrix.impl.*;


/**
 * Example of experimental study based on solving the ZDT problems with four versions of NSGA-II, each
 * of them applying a different crossover probability (from 0.7 to 1.0).
 *
 * This experiment assumes that the reference Pareto front are not known, so the names of files containing
 * them and the directory where they are located must be specified.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are:
 * 1. Configure the experiment
 * 2. Execute the algorithms
 * 3. Generate the reference Pareto fronts
 * 4. Compute the quality indicators
 * 5. Generate Latex tables reporting means and medians
 * 6. Generate Latex tables with the result of applying the Wilcoxon Rank Sum Test
 * 7. Generate Latex tables with the ranking obtained by applying the Friedman test
 * 8. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIStudyBalibase {
    static String experimentBaseDirectory = "experiment/NSGAII" ;
    static String problemName[] = {"BB40001", "BB40010", "BB40020", "BB40030", "BB40040", "BB40049"} ; //, "R4", "R9", "R14", "R19"}; 
    static String dataDirectory = "example";
    static Integer maxEvaluations = 50000; //50000
    static Integer populationSize = 100; //100
    private static final int INDEPENDENT_RUNS = 20 ;

  public static void main(String[] args) throws Exception {
//    if (args.length != 1) {
//      throw new JMetalException("Needed arguments: experimentBaseDirectory") ;
//    }
    JMetalRandom.getInstance().setRandomGenerator(new MersenneTwisterGenerator(1234));
    
    List<Score> scoreList = new ArrayList<>();

    scoreList.add(new EntropyScore()); //1
    scoreList.add(new NumberOfAlignedColumnsScore()); //2 TC
    scoreList.add(new SimilarityGapsScore()); //3
    scoreList.add(new SimilarityNonGapsScore()); //4
    scoreList.add(new NumberOfGapsScore()); //5 Gap
    scoreList.add(new GapConcentrationScore()); //6
    scoreList.add(new SumOfPairsScore(new Blosum62(-4))); //7 SOP
    //scoreList.add(new WeightedSumOfPairsScore(new Blosum62(-4))); //8 wSOP
    //double weightGapExtend, weightGapOpen;
    //scoreList.add(new SumOfPairMinusAffineGapPenaltyScore(new NUC44_V1(),  weightGapOpen=10,  weightGapExtend=1)); //9
    //scoreList.add(new SumOfPairMinusAffineGapPenaltyScore(new NUC44_V1(),  weightGapOpen=8,  weightGapExtend=12)); //10
    
    int scoreCombination[ ][ ] = { { 5, 7 }}; //{ 1, 5, 6 }, { 1, 2, 5, 6 }, { 1, 3, 5, 6 }, { 1, 4, 5, 6 }, { 1, 2, 3, 4 }

    List<ExperimentProblem<MSASolution>> problemList = new ArrayList<>();
    for(int probIndex = 0; probIndex < problemName.length; probIndex++)
    {
      for (int i = 0; i < scoreCombination.length; i++)
      {
          List<Score> localScoreList = new ArrayList<>();
          for (int j = 0; j < scoreCombination[i].length; j++)
          {
              localScoreList.add(scoreList.get(scoreCombination[i][j]-1));
          }
          problemList.add(new ExperimentProblem<>(new BAliBASE_MSAProblem(problemName[probIndex], dataDirectory, localScoreList)));
          
      }
    }

    List<ExperimentAlgorithm<MSASolution, List<MSASolution>>> algorithmList =
            configureAlgorithmList(problemList);

    Experiment<MSASolution, List<MSASolution>> experiment =
        new ExperimentBuilder<MSASolution, List<MSASolution>>("NSGAIIStudy_Balibase_GapSOP")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            //.setReferenceFrontDirectory(experimentBaseDirectory+"/referenceFronts")
            //.setIndicatorList(Arrays.asList(
                //new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(),
                //new PISAHypervolume<DoubleSolution>(),
                //new InvertedGenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistancePlus<DoubleSolution>()))
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(1)
            .build();

    new ExecuteAlgorithms<>(experiment).run();
    //new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
    //new ComputeQualityIndicators<>(experiment).run() ;
    //new GenerateLatexTablesWithStatistics(experiment).run() ;
    //new GenerateWilcoxonTestTablesWithR<>(experiment).run() ;
    //new GenerateFriedmanTestTables<>(experiment).run();
    //new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run() ;
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link
   * ExperimentAlgorithm} has an optional tag component, that can be set as it is shown in this example,
   * where four variants of a same algorithm are defined.
   */
  static List<ExperimentAlgorithm<MSASolution, List<MSASolution>>> configureAlgorithmList(
          List<ExperimentProblem<MSASolution>> problemList) 
   {
    List<ExperimentAlgorithm<MSASolution, List<MSASolution>>> algorithms = new ArrayList<>();
    
    CrossoverOperator<MSASolution> crossover = new SPXMSACrossover(0.8);
    MutationOperator<MSASolution> mutation = new ShiftClosedGapsMSAMutation(0.2);
    SelectionOperator selection = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());
    int numberOfCores = 12;//Runtime.getRuntime().availableProcessors();
    SolutionListEvaluator<MSASolution> evaluator = new SequentialSolutionListEvaluator<>();;
   

    for (int i = 0; i < problemList.size(); i++) {
       if (numberOfCores > 1)
       {
           evaluator = new MultithreadedSolutionListEvaluator(numberOfCores, problemList.get(i).getProblem());
       }
       Algorithm<List<MSASolution>> algorithm = new NSGAIIMSABuilder(problemList.get(i).getProblem(), crossover, mutation, NSGAIIBuilder.NSGAIIVariant.NSGAII)
            .setSelectionOperator(selection)
            .setMaxEvaluations(maxEvaluations)
            .setPopulationSize(populationSize)
            .setSolutionListEvaluator(evaluator)
            .build();
       algorithms.add(new ExperimentAlgorithmMSA(algorithm, "NSGAII", problemList.get(i).getTag()));
    }

    return algorithms;
  }

}

//    problemList.add(new ExperimentProblem<>(new ZDT1()));
//    problemList.add(new ExperimentProblem<>(new ZDT2()));
//    problemList.add(new ExperimentProblem<>(new ZDT3()));
//    problemList.add(new ExperimentProblem<>(new ZDT4()));
//    problemList.add(new ExperimentProblem<>(new ZDT6()));


//    for (int i = 0; i < problemList.size(); i++) {
//      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(
//              problemList.get(i).getProblem(),
//              new SBXCrossover(1.0, 20.0),
//              new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0))
//              .setMaxEvaluations(25000)
//              .setPopulationSize(100)
//              .build();
//      algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAIIb", problemList.get(i).getTag()));
//    }
//
//    for (int i = 0; i < problemList.size(); i++) {
//      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problemList.get(i).getProblem(), new SBXCrossover(1.0, 40.0),
//              new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 40.0))
//              .setMaxEvaluations(25000)
//              .setPopulationSize(100)
//              .build();
//      algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAIIc", problemList.get(i).getTag()));
//    }
//
//    for (int i = 0; i < problemList.size(); i++) {
//      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problemList.get(i).getProblem(), new SBXCrossover(1.0, 80.0),
//              new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 80.0))
//              .setMaxEvaluations(25000)
//              .setPopulationSize(100)
//              .build();
//      algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAIId", problemList.get(i).getTag()));
//    }
