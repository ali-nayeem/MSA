/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.runner;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetalmsa.score.impl.PercentageOfAlignedColumnsScore;
import org.uma.jmetalmsa.score.impl.PercentageOfNonGapsScore;
import org.uma.jmetalmsa.score.impl.EntropyScore;
import org.uma.jmetalmsa.problem.MAN_MSAProblem;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.solution.util.ArrayChar;
import org.uma.jmetalmsa.score.Score;

/**
 *
 * @author Nayeem
 */
public class TestObjetivesMAN
{

    static MSAProblem problem;
    static String path = "dataset/100S/R0/";
    static String seq = "seq.txt";
    static String align = "test.fasta";

    public static void main(String[] args) throws Exception
    {

        EvaluaGroup(1, align);

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
        System.out.println(Fichero + "\t" + s.getObjective(0)  + "\t" + +s.getObjective(1) * -1 + "\t"
                + s.getObjective(2) * -1 + "\t");

    }

}
