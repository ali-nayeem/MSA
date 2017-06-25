package org.uma.jmetalmsa.score.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.score.Score;

/**
 * @author Nayeem
 */
public class EntropyScore implements Score
{

    @Override
    public <S extends MSASolution> double compute(S solution, char[][] decodedSequences)
    {
        double totalEntropy = 0;
        int numberOfColumns = decodedSequences[0].length;
        char residue;
        Boolean isGapFree;
        for (int i = 0; i < numberOfColumns; i++)
        {
            HashMap< Character, Integer> columnMap = new HashMap<>();
            isGapFree = true;
            for (int j = 0; j < decodedSequences.length; j++)
            {
                residue = decodedSequences[j][i];
                if (solution.isGap(residue))
                {
                    isGapFree = false;
                    break;
                }
                columnMap.put(residue, columnMap.getOrDefault(residue, 0) + 1);
            }

            if (isGapFree)
            {
                for (int freq : columnMap.values())
                {
                    double pr = freq * 1.0 / decodedSequences.length;
                    totalEntropy += -(pr * log2(pr));
                }
            }
        }

        double result = totalEntropy / numberOfColumns;
        return result;
    }

    public double log2(double n)
    {
        return Math.log(n) / Math.log(2);
    }

    @Override
    public boolean isAMinimizationScore()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Entropy";
    }

    @Override
    public String getDescription()
    {
        return "Based on relative frequency of residues for each column";
    }
}
