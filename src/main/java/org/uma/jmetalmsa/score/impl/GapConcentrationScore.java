package org.uma.jmetalmsa.score.impl;

//import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import java.util.List;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.score.Score;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GapConcentrationScore implements Score
{

    @Override
    public <S extends MSASolution> double compute(S solution, char[][] decodedSequences)
    {
        //int testOrder = (Integer) solution.getAttribute("testOrder");
        //if(testOrder != 5)
        //{
        //    System.out.println("Trouble@"+getName());
        //    System.exit(0);
        //}
        //solution.setAttribute("testOrder", testOrder+1);
        
        double totalGapCon = 0;
        for (int i = 0; i < solution.getNumberOfVariables(); i++)
        {
            List<Integer> gapGroupList = solution.getVariableListInteger(i);
            if (gapGroupList.size() > 0)
            {
                double seqGapCon = 0;
                for (int j = 1; j < gapGroupList.size(); j = j + 2)
                {
                    seqGapCon += (gapGroupList.get(j) - gapGroupList.get(j - 1) + 1);
                }
                seqGapCon = seqGapCon / (gapGroupList.size() / 2.0);
                totalGapCon += seqGapCon;
            }
        }
        totalGapCon = totalGapCon / solution.getNumberOfVariables();
        return totalGapCon;
    }

    @Override
    public boolean isAMinimizationScore()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return "GapCon";
    }

    @Override
    public String getDescription()
    {
        return "Concentration of gaps in a multiple sequence";
    }
    
    public static int getDependency()
    {
        return 0;
    }   
}
