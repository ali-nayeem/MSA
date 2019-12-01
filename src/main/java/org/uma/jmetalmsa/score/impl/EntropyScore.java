package org.uma.jmetalmsa.score.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
//import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.solution.util.ArrayChar;

/**
 * @author Nayeem
 */
public class EntropyScore implements Score
{
    private int longestSeqLength = -1;
    //private int AlignedColumnCount;
    //public List<Integer> GapIndexList;
    //public List<Integer> NonGapIndexList;
    //public List <HashMap< Character, Integer>> NonGapColumnMapList;
    void calculateLongestSeqLength(MSAProblem prob)
    {
        List<Integer> seqLengthList = new ArrayList<> ();
        for (ArrayChar cs : prob.originalSequences)
        {
            seqLengthList.add(cs.getSize());
        }
        longestSeqLength = Collections.max(seqLengthList);
    }
    
//    public int getAlignedColumnCount()
//    {
//        return AlignedColumnCount;
//    }

    @Override
    public <S extends MSASolution> double compute(S solution, char[][] decodedSequences)
    {
        //solution.setAttribute("testOrder", 1);
        
        if(longestSeqLength == -1)
        {
            calculateLongestSeqLength(solution.getMSAProblem());
        }
        int numberOfColumns = decodedSequences[0].length;
        int numberOfRows = decodedSequences.length;
        double totalEntropy = 0;
        boolean hasNonGapColumn = false;
        char residue;
        Boolean isNonGapColumn;
        int AlignedColumnCount = 0;
        List<Integer> GapIndexList = new ArrayList<>();
        //NonGapIndexList = new ArrayList<>();
        //List <HashMap< Character, Integer>> NonGapColumnMapList = new ArrayList<>();
        double similarityNG = 0;
        int nonGapColumnCount = 0;
        for (int i = 0; i < numberOfColumns; i++)
        {
            HashMap< Character, Integer> columnMap = new HashMap<>();
            isNonGapColumn = true;
            for (int j = 0; j < numberOfRows; j++)
            {
                residue = decodedSequences[j][i];
                if (solution.isGap(residue))
                {
                    isNonGapColumn = false;
                    GapIndexList.add(i);
                    break;
                }
                columnMap.put(residue, columnMap.getOrDefault(residue, 0) + 1);
            }

            if (isNonGapColumn)
            {
                nonGapColumnCount++;
                //NonGapIndexList.add(i);
                hasNonGapColumn = true;
                for (int freq : columnMap.values())
                {
                    double pr = freq * 1.0 / numberOfRows;
                    totalEntropy += -(pr * log2(pr));
                }
                if (columnMap.size() == 1)
                {
                    AlignedColumnCount++;
                    
                }
                //NonGapColumnMapList.add(columnMap);
                int maxFreq = Collections.max(columnMap.values());
                similarityNG += 1.0 * maxFreq / numberOfRows;
            }
        }
        solution.setAttribute("AlignedColumnCount", (double) AlignedColumnCount);
        solution.setAttribute("GapIndexList", GapIndexList);
        //solution.setAttribute("NonGapColumnMapList", NonGapColumnMapList);
        solution.setAttribute("similarityNG", similarityNG);
        solution.setAttribute("nonGapColumnCount", nonGapColumnCount);
        if (hasNonGapColumn)
        {
            double result = totalEntropy ;
            return result;
        }
        else return longestSeqLength;
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
        return "Based on relative frequency of residues for each non-gap column";
    }
    
    public static int getDependency()
    {
        return 0;
    }
}
