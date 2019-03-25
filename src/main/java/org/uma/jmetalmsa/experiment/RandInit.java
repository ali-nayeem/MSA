/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.experiment;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalmsa.algorithm.algoyy.util.PseudoRandom;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.solution.MSASolution;

/**
 *
 * @author ali_nayeem
 */
public class RandInit {
    MSAProblem prob;

    public RandInit(MSAProblem prob) {
        this.prob = prob;
    }
    public int getLongestSeqLength()
    {
        int longestSeqLength = -1;
        
        for (int i = 0; i < prob.originalSequences.size(); i++)
        {
            if (prob.originalSequences.get(i).getSize() > longestSeqLength) 
            {
                longestSeqLength = prob.originalSequences.get(i).getSize();
            }
        }
        return longestSeqLength;
    }
    public List<MSASolution> createInitialPopulationRandomly(int Size)
    {
        System.out.println(prob.getName() + " :Generating init pop randomly############################################");
        List<MSASolution> population = new ArrayList<>(Size);
        int longestSeqLength = getLongestSeqLength();
        GenerateSingleSolutionRandomly gen = new GenerateSingleSolutionRandomly(longestSeqLength, prob);
        
        for (int i = 0; i < Size; i++)
        {
            population.add(gen.generateOneSol());
            //System.out.println(population.get(i).toString());
        }
        
        return population;
    }
    class GenerateSingleSolutionRandomly
    {
            final double normalMeanFrac = 4;
            final double normalStdDev95Frac = 8;
            final double gapLowerFrac = 0.1;
            final double gapUpperFrac = 0.5;
            MSAProblem msa;
            final JMetalRandom randomGenerator = JMetalRandom.getInstance();
            int longestSeqLength;
            
            GenerateSingleSolutionRandomly(int longestSeqLength, MSAProblem msa)
            {
                this.msa = msa;
                this.longestSeqLength = longestSeqLength;
            }
            
            public MSASolution generateOneSol()
            {
                List<List<Integer>> gapsGroups = new ArrayList<>();
                double GapPerc = 1.0+randomGenerator.nextDouble(gapLowerFrac, gapUpperFrac);
                int maxLength = (int) Math.round(GapPerc * longestSeqLength);
                
                for (int i = 0; i < msa.getNumberOfVariables(); i++)
                {
                    synchronized(randomGenerator)  //bcoz MersenneTwister is not thread-safe, generate exception when working with multiple dataset
                    {
                        gapsGroups.add(generateOneSeq(maxLength, i));
                    }
                }
                
                
                return new MSASolution(msa, gapsGroups);
            }

        private List<Integer> generateOneSeq(int maxLength, int seqId)
        {
            int totalGap = maxLength - msa.originalSequences.get(seqId).getSize();
            List<Integer> oneGapList = null;
            int assignedGap = 0;
            double mean = totalGap / normalMeanFrac;
            double stdDev = (mean -  totalGap / normalStdDev95Frac)/2;
             while(assignedGap != totalGap)
             {
                 assignedGap = 0;
                 oneGapList = new ArrayList<>();
                 for (int i = 0; i < maxLength && assignedGap < totalGap; i++)
                 {
                     int gapLen = (int)PseudoRandom.randNormal(mean, stdDev);
                     double expectedPick =  totalGap/mean;//(totalGap - assignedGap)/gapLen;//((i+1.0)/maxLength) * (totalGap/(assignedGap+1));  //
                     double adjPickProb = expectedPick /maxLength;
                     double RandUpLim = (maxLength-i)*1.0/maxLength;
                     if (randomGenerator.nextDouble(0,RandUpLim) <= adjPickProb)
                     {
                         
                         gapLen = (gapLen < 1) ? 1: gapLen;
                         gapLen = ( (assignedGap + gapLen) > totalGap) ? (totalGap - assignedGap) : gapLen;
                         if (i+gapLen > maxLength)
                         {
                             break;
                         }
                         int size = oneGapList.size();
                         if ( size > 1 && (i - oneGapList.get(size-1)) == 1) 
                         {
                             oneGapList.set(size-1, i+gapLen-1);
                         }
                         else
                         {
                             oneGapList.add(i);
                             oneGapList.add(i+gapLen-1);
                         }
                         
                         i += (gapLen-1);
                         assignedGap += gapLen;
                     }
                 }
             }
            //compressOneGapList(oneGapList);
            return oneGapList;
        }
    }
}
