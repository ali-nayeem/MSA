package org.uma.jmetalmsa.score.impl;

//import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.score.Score;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SimilarityNonGapsScore implements Score
{

    @Override
    public <S extends MSASolution> double compute(S solution, char[][] decodedSequences)
    {
        //int testOrder = (Integer) solution.getAttribute("testOrder");
        //if(testOrder != 4)
        //{
        //    System.out.println("Trouble@"+getName());
        //    System.exit(0);
        //}
        //solution.setAttribute("testOrder", testOrder+1);
        if (solution.getAttribute("similarityNG") == null)
        {
            new EntropyScore().compute(solution, decodedSequences);
        }
        double similarityNG = (double) solution.getAttribute("similarityNG");
        solution.setAttribute("similarityNG",null);
        
        return similarityNG;
    }

    @Override
    public boolean isAMinimizationScore()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return "SimNG";
    }

    @Override
    public String getDescription()
    {
        return "Similarity of non-gap columns in a multiple sequence";
    }

    public static int getDependency()
    {
        return EntropyScore.getDependency() + 1;
    }
}

//        List <HashMap< Character, Integer>> NonGapColumnMapList = (List <HashMap< Character, Integer>>) solution.getAttribute("NonGapColumnMapList");
//        if (NonGapColumnMapList.isEmpty())
//        {
//            return 0;
//        } 
//        else
//        {
//            int numberOfRows = decodedSequences.length;
//            double similarityNG = 0;
//            for(HashMap< Character, Integer> columnMap : NonGapColumnMapList)
//            {
//                int maxFreq = Collections.max(columnMap.values());
//                similarityNG += 1.0 * maxFreq / numberOfRows;
//               
//            }
//            return similarityNG;
