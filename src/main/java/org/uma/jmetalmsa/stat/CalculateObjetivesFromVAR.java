/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetalmsa.score.impl.PercentageOfAlignedColumnsScore;
import org.uma.jmetalmsa.score.impl.PercentageOfNonGapsScore;
import org.uma.jmetalmsa.score.impl.EntropyScore;
//import org.uma.jmetalmsa.problem.MAN_MSAProblem;
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

    //static MSAProblem problem;
    static String path = "dataset/100S";
    static String seq = "seq.txt";
    static String instance = "R-1";
    static String varFilePath = "VAR.R-1.NSGAII.tsv";
    static int numOfSeq;

    public List<MSASolution> createPopulationFromVarFile(String varFilePath, MSAProblem problem) throws Exception
    {
        List<MSASolution> pop = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(varFilePath));

        while (br.ready())
        {
            List<ArrayChar> sequenceList = new ArrayList<>();
            //String oneVar = "";
            String oneLine = "";

            do
            {
                oneLine = br.readLine();
            } while (oneLine != null && !oneLine.startsWith(">"));

            while (oneLine != null && !oneLine.isEmpty())
            {
                //oneVar += oneLine + "\n";
                if (!oneLine.startsWith(">"))
                {
                    sequenceList.add(new ArrayChar(oneLine));
                }
                oneLine = br.readLine();
            }

            if (oneLine == null)
            {
                break;
            }
            //br.readLine();
            //br.readLine();
            //InputStream is = new ByteArrayInputStream(oneVar.getBytes());
            //MSASolution sol = new MSASolution(problem.readDataFromInputStream(is), problem);
            MSASolution sol = new MSASolution(sequenceList, problem);
            pop.add(sol);
        }
        br.close();

        return pop;
    }

    public List<MSASolution> createPopulationFromEncodedVarFile(String encodedVarFilePath, MSAProblem problem) throws Exception
    {
        List<MSASolution> pop = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(encodedVarFilePath));

        while (br.ready())
        {
            List<List<Integer>> gapsGroups = new ArrayList<>();
            String oneLine = "";

            do
            {
                oneLine = br.readLine();
            } while (oneLine != null && !oneLine.startsWith("<"));

            while (oneLine != null)
            {
                oneLine = br.readLine();
                if (oneLine.startsWith(">"))
                {
                    break;
                }

                List<Integer> oneGapList = new ArrayList<>();
                if (!oneLine.isEmpty())
                {
                    String[] gapStringList = oneLine.split(",");
                    for (String gap : gapStringList)
                    {
                        oneGapList.add(Integer.parseInt(gap));
                    }
                }
                gapsGroups.add(oneGapList);
            }

            if (oneLine == null)
            {
                break;
            }

            MSASolution sol = new MSASolution(problem, gapsGroups);
            pop.add(sol);

        }
        br.close();

        return pop;
    }

    public void evaluatePopulationToFile(List<MSASolution> pop, String filepath, int numberOfCores, MSAProblem problem)
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

    public void printPopulationToEncodedvarFile(List<MSASolution> pop, String filepath) throws IOException
    {
        DefaultFileOutputContext outFile = new DefaultFileOutputContext(filepath);
        BufferedWriter bufferedWriter = outFile.getFileWriter();
        for (MSASolution sol : pop)
        {
            bufferedWriter.write(sol.getEncodedAlignment());
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }

    public void varifySolutions(List<MSASolution> pop)
    {
        for (MSASolution sol : pop)
        {
            if (!sol.isValid())
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

    public List<MSASolution> getNonDominatedSolutions(List<MSASolution> pop)
    {
        return SolutionListUtils.getNondominatedSolutions(pop);
    }

    public List<MSASolution> getFirstFront(List<MSASolution> pop)
    {
        Ranking<MSASolution> ranking = new DominanceRanking<>();
        ranking.computeRanking(pop);
        return ranking.getSubfront(0);
    }

    public static void main(String[] args) throws Exception
    {
        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new EntropyScore());
        scoreList.add(new NumberOfAlignedColumnsScore());
        //scoreList.add(new NumberOfGapsScore());
        scoreList.add(new SimilarityGapsScore());
        scoreList.add(new SimilarityNonGapsScore());
        scoreList.add(new GapConcentrationScore());

        MSAProblem problem = new SATE_MSAProblem(instance, path, scoreList);

        //numOfSeq = problem.getNumberOfVariables();
        CalculateObjetivesFromVAR ob = new CalculateObjetivesFromVAR();
        List<MSASolution> pop = ob.createPopulationFromVarFile(varFilePath, problem);
        //pop = ob.getFirstFront(pop);
        //ob.evaluatePopulationToFile(pop, "FUN." + problem.getName() + ".MAN", 0);

        //ob.varifySolutions(pop);
        ob.printPopulationToEncodedvarFile(pop, varFilePath + "_encoded");
        pop = ob.createPopulationFromEncodedVarFile(varFilePath + "_encoded.txt", problem);
        ob.evaluatePopulationToFile(pop, "FUN." + problem.getName() + ".MAN", 0, problem);

    }
}
