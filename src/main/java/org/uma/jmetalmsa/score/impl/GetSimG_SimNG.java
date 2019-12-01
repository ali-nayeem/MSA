/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.score.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetalmsa.problem.DNA_MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.util.distancematrix.impl.NUC44_V1;

/**
 *
 * @author ali_nayeem
 */
public class GetSimG_SimNG {

    public static void main(String arg[]) //throws IOException, CompoundNotFoundException
    {
//      String[] dummy = new String[1];
//      dummy[0] = "dataset/100S/R/input.fasta_fsa";
//      arg = dummy;
        if (arg.length < 1) {
            System.out.println("Please provide the input alignment as FASTA. Exiting !!");
            //System.exit(0);
            return;
        }
        if (arg.length < 2) {
            System.out.println("Please provide the weight SimG. Exiting !!");
            //System.exit(0);
            return;
        }
        double simgWeight = Double.parseDouble(arg[1]);
        List<Score> scoreList = new ArrayList<>();
        //Score sop = new SumOfPairsScore(new NUC44_V1());
        Score simg = new AdjustedSimilarityGapsScore();
        Score simng = new AdjustedSimilarityNonGapsScore();
        scoreList.add(simg);
        scoreList.add(simng);
        //MSAProblem problem = new MSAProblem(scoreList);

        DNA_MSAProblem problem = null;
        try {
            problem = new DNA_MSAProblem(scoreList, arg[0]); //"example/MAN/demo_align.txt");
        } catch (Exception ex) {
            Logger.getLogger(SumOfPairsScore.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Alignment file not found. Exiting !!");
            return;
        }
        //List<ArrayChar> strAlignment = problem.readDataFromFastaFile("example/MAN/demo_align.txt");
        //problem.originalSequences = strAlignment;
        //problem.
        MSASolution sol = new MSASolution(problem.alignedSeq, problem);
        //sol.setSizeOfOriginalSequences(problem.originalSequences);
        //System.out.println(simgWeight);
        //System.out.println(simg.compute(sol, sol.decodeToMatrix()));
        //System.out.println(simng.compute(sol, sol.decodeToMatrix()));
        double result = simg.compute(sol, sol.decodeToMatrix()) * simgWeight + simng.compute(sol, sol.decodeToMatrix()) * ( 1 - simgWeight);
        System.out.print(-1*result);
    }


}
