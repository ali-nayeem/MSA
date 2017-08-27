/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.experiment;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.EntropyScore;
//import org.uma.jmetalmsa.score.impl.GapConcentrationScore;
//import org.uma.jmetalmsa.score.impl.NumberOfAlignedColumnsScore;
//import org.uma.jmetalmsa.score.impl.SimilarityGapsScore;
//import org.uma.jmetalmsa.score.impl.SimilarityNonGapsScore;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.stat.CalculateObjetivesFromVAR;

/**
 *
 * @author Nayeem
 */
public class DecodeVAR
{

    static String instancePath = "dataset/100S";

    public static void main(String[] args) throws Exception
    {
        if (args.length != 3)
        {
            throw new JMetalException("Wrong number of arguments");
        }
        String instanceName = args[0];//args[0]; "R-1"
        String inputFilePath = args[1];//args[1]; "input"
        String outputFilePath = args[2];//args[2]; "output"

        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new EntropyScore());
        //scoreList.add(new NumberOfAlignedColumnsScore());
        //scoreList.add(new NumberOfGapsScore());
        //scoreList.add(new SimilarityGapsScore());
        //scoreList.add(new SimilarityNonGapsScore());
        //scoreList.add(new GapConcentrationScore());

        MSAProblem problem = new SATE_MSAProblem(instanceName, instancePath, scoreList);

        //numOfSeq = problem.getNumberOfVariables();
        CalculateObjetivesFromVAR ob = new CalculateObjetivesFromVAR();
        List<MSASolution> pop = ob.createPopulationFromEncodedVarFile(inputFilePath, problem);
        
        DefaultFileOutputContext varFile = new  DefaultFileOutputContext(outputFilePath);
        varFile.setSeparator("\n");
        new SolutionListOutput(pop)
            .setVarFileOutputContext(varFile)
            .print();
        
    }

}
