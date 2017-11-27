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
public class SimilarityGapsScore implements Score
{

    @Override
    public <S extends MSASolution> double compute(S solution, char[][] decodedSequences)
    {
        //int testOrder = (Integer) solution.getAttribute("testOrder");
        //if(testOrder != 3)
        //{
        //    System.out.println("Trouble@"+getName());
        //    System.exit(0);
        //}
        //solution.setAttribute("testOrder", testOrder+1);
        
        if (solution.getAttribute("GapIndexList") == null)
        {
            new EntropyScore().compute(solution, decodedSequences);
        }

        List<Integer> GapIndexList = (List<Integer>) solution.getAttribute("GapIndexList");
        solution.setAttribute("GapIndexList", null);
        
        char residue;
        int numberOfRows = decodedSequences.length;
        double similarityG = 0;
        for (int i : GapIndexList)
        {
            HashMap< Character, Integer> columnMap = new HashMap<>();
            for (int j = 0; j < numberOfRows; j++)
            {
                residue = decodedSequences[j][i];
                if (solution.isGap(residue) == false)
                {
                    columnMap.put(residue, columnMap.getOrDefault(residue, 0) + 1);
                }
            }
            int maxFreq = Collections.max(columnMap.values());
            similarityG += 1.0 * maxFreq / numberOfRows;
        }
        return similarityG;

    }

    @Override
    public boolean isAMinimizationScore()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return "SimG";
    }

    @Override
    public String getDescription()
    {
        return "Similarity of gap columns in a multiple sequence";
    }

    public static int getDependency()
    {
        return EntropyScore.getDependency() + 1;
    }
}
