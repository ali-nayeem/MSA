/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.*;
import org.uma.jmetalmsa.solution.MSASolution;
import static org.uma.jmetalmsa.stat.DecodeVAR.instancePath;

/**
 *
 * @author Nayeem
 */
public class GenerateFUN
{
    static String instancePath = "dataset/100S";
    static String instanceName = "R4";
    static String encodedVarFileName = "/home/ali_nayeem/MSA/experiment/uniqueCombined";
    static String FUNFileName = "/home/ali_nayeem/MSA/experiment/uniqueCombinedFUN_R4";
    //static String shuffledVarFileName = "/home/ali_nayeem/MSA/experiment/shuffledVAR";
    public static void main(String[] args) throws Exception
    {
        int numberOfCores;
        if (args.length == 1)
        {
            numberOfCores = Integer.parseInt(args[0]);
        } else
        {
            numberOfCores = Runtime.getRuntime().availableProcessors()/2;
        }
        //String instanceName = args[0];//args[0]; "R-1"
        //String inputFilePath = args[1];//args[1]; "input"
        //String outputFilePath = args[2];//args[2]; "output"

        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new EntropyScore());
        scoreList.add(new NumberOfAlignedColumnsScore());
        scoreList.add(new NumberOfGapsScore());
        scoreList.add(new SimilarityGapsScore());
        scoreList.add(new SimilarityNonGapsScore());
        scoreList.add(new GapConcentrationScore());

        MSAProblem problem = new SATE_MSAProblem(instanceName, instancePath, scoreList);

        //numOfSeq = problem.getNumberOfVariables();
        SolutionListEvaluator<MSASolution> evaluator;
        if (numberOfCores == 1) {
          evaluator = new SequentialSolutionListEvaluator<>();
        } else {
          evaluator = new MultithreadedSolutionListEvaluator(numberOfCores, problem);
        }  
        
        CalculateObjetivesFromVAR ob = new CalculateObjetivesFromVAR();
        List<MSASolution> pop = ob.createPopulationFromEncodedVarFile(encodedVarFileName, problem);
        //Most imp part
        evaluator.evaluate(pop, problem);

        DefaultFileOutputContext funFile = new DefaultFileOutputContext(FUNFileName);
        funFile.setSeparator("\t"); 
        
        new SolutionListOutput(pop)
                .printObjectivesToFile(funFile, pop);
                

    }
}
