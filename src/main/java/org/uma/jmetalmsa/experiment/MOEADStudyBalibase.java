package org.uma.jmetalmsa.experiment;

import java.io.File;
import java.nio.file.Files;
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
//import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

//import java.io.IOException;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
//import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;
import org.uma.jmetalmsa.algorithm.moead.MOEADMSABuilder;
import org.uma.jmetalmsa.crossover.SPXMSACrossover;
import org.uma.jmetalmsa.mutation.ShiftClosedGapsMSAMutation;
import org.uma.jmetalmsa.problem.BAliBASE_MSAProblem;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.*;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.stat.FindUniqueEncodedVAR_Balibase;
import org.uma.jmetalmsa.util.IOCopier;
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
public class MOEADStudyBalibase {
    static String experimentBaseDirectory = "experiment/Balibase_runtime" ;
    static String problemName[] = {"BB20022"} ; //"BB20001", "BB20010" ,"BB20022", "BB20033", "BB20041"    "BB11005", "BB11018", "BB11020", "BB11033" "BB40001", "BB40013", "BB40025", "BB40038", "BB40048" "BB12001", "BB12013", "BB12022", "BB12035", "BB12044"
    //"BB12001", "BB12013", "BB12022", "BB12035", "BB12044"
    static String dataDirectory = "example";
    static Integer maxEvaluations = 24120; //50000 60120
    static Integer populationSize = 100; //100
    private static final int INDEPENDENT_RUNS = 4 ;
    static Integer neighborSize = 10; //100
    static Double neighborhoodSelectionProbability = 0.7;
    static Integer maximumNumberOfReplacedSolutions = 2;
    static int div1=7, div2=0;

  public static void main(String[] args) throws Exception {
    if (args.length > 0) 
    {
      //throw new JMetalException("Needed arguments: experimentBaseDirectory") ;
      problemName = new String[args.length-1];
        for (int i = 1; i < args.length; i++) 
        {
            problemName[i-1] = args[i];
        }
    }
    JMetalRandom.getInstance().setRandomGenerator(new MersenneTwisterGenerator(1234));
    
    List<Score> scoreList = new ArrayList<>();

    scoreList.add(new EntropyScore()); //1
    scoreList.add(new NumberOfAlignedColumnsScore()); //2 TC
    scoreList.add(new SimilarityGapsScore()); //3 SimG
    scoreList.add(new SimilarityNonGapsScore()); //4 SimNG
    scoreList.add(new NumberOfGapsScore()); //5 Gap
    scoreList.add(new GapConcentrationScore()); //6
    scoreList.add(new SumOfPairsScore(new Blosum62(-4))); //7 SOP
    scoreList.add(new InternalConsistencyScore()); //8 InCon
    //scoreList.add(new WeightedSumOfPairsScore(new Blosum62(-4))); //8 wSOP
    //double weightGapExtend, weightGapOpen;
    //scoreList.add(new SumOfPairMinusAffineGapPenaltyScore(new NUC44_V1(),  weightGapOpen=10,  weightGapExtend=1)); //9
    //scoreList.add(new SumOfPairMinusAffineGapPenaltyScore(new NUC44_V1(),  weightGapOpen=8,  weightGapExtend=12)); //10
    int scoreCombination[ ][ ];
    if (args.length == 0)
    {
        int gapSop [ ][ ] = { { 3, 4, 7, 5} }; //{ 1, 5, 6 }, { 1, 2, 5, 6 }, { 1, 3, 5, 6 }, { 1, 4, 5, 6 }, { 1, 2, 3, 4 }
        scoreCombination = gapSop;
    }
    else if (args[0].toLowerCase().contains("gap")) 
    {
        int gapSop [ ][ ] = { { 5, 7 } }; //{ 1, 5, 6 }, { 1, 2, 5, 6 }, { 1, 3, 5, 6 }, { 1, 4, 5, 6 }, { 1, 2, 3, 4 }
        scoreCombination = gapSop;
    }
    else
    {
        int simgSimng [ ][ ] = { { 3, 4 } };
        scoreCombination = simgSimng;
    }
    System.out.println("Score: " + scoreCombination[0][0] + ", " + scoreCombination[0][1]);
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
        new ExperimentBuilder<MSASolution, List<MSASolution>>("Hybrid_MOEADStudy_Balibase")
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
            .setNumberOfCores(INDEPENDENT_RUNS)
            .build();

    new ExecuteParallelAlgorithms<>(experiment).run();
    
    for(ExperimentAlgorithm<MSASolution, List<MSASolution>> algo : algorithmList)
    {
        String path1 = experimentBaseDirectory + "/" + experiment.getExperimentName() + "/data/" + algo.getAlgorithmTag() + "/";
        for(ExperimentProblem<MSASolution> prob : problemList)
        {
            String path2 = path1 + prob.getTag() + "/";
            File[] varFiles = new File[INDEPENDENT_RUNS];
            File combinedVarFile = new File(path2 + "combinedVAR");
            boolean result = Files.deleteIfExists(combinedVarFile.toPath());
            for (int i = 0; i < INDEPENDENT_RUNS; i++) 
            {
                String varFilePath = path2 + "VAR" + i + ".tsv";
                varFiles[i] = new File(varFilePath);               
            }
            IOCopier.joinFiles(combinedVarFile, varFiles);
            FindUniqueEncodedVAR_Balibase selfObj = new FindUniqueEncodedVAR_Balibase();
            Set<String> uniqueVar = selfObj.getUniqueEncodedVarFile(combinedVarFile.toPath().toString(), (MSAProblem) prob.getProblem());
            selfObj.printUniqueEncodedVarToFile(uniqueVar, path2 + "uniqueCombined");
        }
    }
    
    JMetalLogger.logger.info("All exp ended.");
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
    //SelectionOperator selection = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());

//   
//
    for (int i = 0; i < problemList.size(); i++) {
        for (int j = 0; j < INDEPENDENT_RUNS; j++)
        {
            Algorithm<List<MSASolution>> algorithm = new MOEADMSABuilder(problemList.get(i).getProblem(), MOEADMSABuilder.Variant.HYBRID) //MOEAD
            .setCrossover(crossover)
            .setMutation(mutation)
            .setMaxEvaluations(maxEvaluations)
            .setPopulationSize(populationSize)
            //.setDataDirectory("MOEAD_Weights")
            .setNeighborSize(neighborSize)
            .setFunctionType(AbstractMOEAD.FunctionType.AGG)
            .setNeighborhoodSelectionProbability(neighborhoodSelectionProbability)
            .setMaximumNumberOfReplacedSolutions(maximumNumberOfReplacedSolutions)
            .setDiv(div1, div2)
            .build() ;
       algorithms.add(new ExperimentAlgorithmMSA(algorithm, "MOEAD", problemList.get(i).getTag(), j));
        }
       
    }

    return algorithms;
  }

}
