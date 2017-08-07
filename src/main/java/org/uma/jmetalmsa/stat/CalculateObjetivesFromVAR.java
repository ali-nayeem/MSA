/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import org.uma.jmetalmsa.runner.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalmsa.score.impl.PercentageOfAlignedColumnsScore;
import org.uma.jmetalmsa.score.impl.PercentageOfNonGapsScore;
import org.uma.jmetalmsa.score.impl.EntropyScore;
import org.uma.jmetalmsa.problem.MAN_MSAProblem;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.solution.util.ArrayChar;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.GapConcentrationScore;
import org.uma.jmetalmsa.score.impl.NumberOfAlignedColumnsScore;
import org.uma.jmetalmsa.score.impl.NumberOfGapsScore;
import org.uma.jmetalmsa.score.impl.SimilarityGapsScore;
import org.uma.jmetalmsa.score.impl.SimilarityNonGapsScore;

/**
 *
 * @author Nayeem
 */
public class CalculateObjetivesFromVAR
{

    static MSAProblem problem;
    static String path = "dataset/100S";
    static String seq = "seq.txt";
    static String instance = "R0";
    static String varFilePath = "VAR.R0.NSGAIIIYY.tsv";
    static int numOfSeq;

    public <P extends MSAProblem> List<MSASolution> createPopulationFromVarFile(String varFilePath, P problem) throws Exception
    {
        List<MSASolution> pop = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(varFilePath));

        while (true)
        {
            String oneVar = "";
            String oneLine = "";
            for (int i = 0; i < 2 * numOfSeq; i++)
            {
                oneLine = br.readLine();
                if (oneLine == null)
                {
                    break;
                }
                oneVar += oneLine + "\n";
            }
            if (oneLine == null)
            {
                break;
            }
            br.readLine();
            br.readLine();
            InputStream is = new ByteArrayInputStream(oneVar.getBytes());
            MSASolution sol = new MSASolution(problem.readDataFromInputStream(is), problem);
            pop.add(sol);
        }
        br.close();

        return pop;
    }

    public void evaluatePopulationToFile(List<MSASolution> pop, String filepath, int numberOfCores)
    {
        SolutionListEvaluator<MSASolution> evaluator;

        if (numberOfCores == 1)
        {
            evaluator = new SequentialSolutionListEvaluator<>();

        } else
        {
            if (numberOfCores == 0)
            {
                numberOfCores = Runtime.getRuntime().availableProcessors();
            }
            evaluator = new MultithreadedSolutionListEvaluator(numberOfCores, problem);
        }

        evaluator.evaluate(pop, problem);

        DefaultFileOutputContext funFile = new DefaultFileOutputContext(filepath + ".tsv");
        funFile.setSeparator("\t");

        new SolutionListOutput(pop)
                .setFunFileOutputContext(funFile)
                .print();
        evaluator.shutdown();
    }
    
    public void varifySolutions (List<MSASolution> pop)
    {
        for(MSASolution sol : pop)
        {
            if(!sol.isValid())
            {
                System.err.println("Not valid:" + "\n" + sol);
            }
            for (int i = 0; i < sol.getAlignmentLength(); i++)
            {
                if (sol.isGapColumn(i))
                {
                    System.err.println("Gap column:" + "\n" + sol);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new EntropyScore());
        scoreList.add(new NumberOfAlignedColumnsScore());
        scoreList.add(new NumberOfGapsScore());
        scoreList.add(new SimilarityGapsScore());
        scoreList.add(new SimilarityNonGapsScore());
        scoreList.add(new GapConcentrationScore());

        problem = new SATE_MSAProblem(instance, path, scoreList);
        
        numOfSeq = problem.getNumberOfVariables();

        CalculateObjetivesFromVAR ob = new CalculateObjetivesFromVAR();
        List<MSASolution> pop = ob.createPopulationFromVarFile(varFilePath, problem);
        ob.evaluatePopulationToFile(pop, "FUN." + problem.getName() + ".MAN", 0);
        
        ob.varifySolutions(pop);

    }

    public static void EvaluaGroup(int Limit, String Instance_) throws Exception
    {

        for (int i = 1; i <= Limit; i++)
        {
            String Instance = Instance_;
//            if(i<10)  Instance = Instance +"0";
//            Instance = Instance + i;
            List<Score> scoreList = new ArrayList<>();

            scoreList.add(new EntropyScore());
            scoreList.add(new PercentageOfAlignedColumnsScore());
            scoreList.add(new PercentageOfNonGapsScore());
            problem = new SATE_MSAProblem("R0", "dataset/100S", scoreList);

            EvaluaAlig(path + Instance_);

        }
    }

    public static void EvaluaAlig(String Fichero) throws Exception
    {
        List<ArrayChar> strAlignment = problem.readDataFromFastaFile(Fichero);
        MSASolution s = new MSASolution(strAlignment, problem);

        problem.evaluate(s);
        System.out.println(Fichero + "\t" + s.getObjective(0) + "\t" + +s.getObjective(1) * -1 + "\t"
                + s.getObjective(2) * -1 + "\t");

    }

}
