/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.score.impl;

import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.util.distancematrix.DistanceMatrix;

/**
 *
 * @author Nayeem
 */
public class SumOfPairMinusAffineGapPenaltyScore implements Score
{
    private SumOfPairsScore SP;
    private AffineGapPenaltyScore AGP;

    public SumOfPairMinusAffineGapPenaltyScore(DistanceMatrix distanceMatrix, double weightGapOpen, double weightGapExtend)
    {
        SP = new SumOfPairsScore(distanceMatrix);
        AGP = new AffineGapPenaltyScore(weightGapOpen, weightGapExtend);
    }
    
    

    @Override
    public <S extends MSASolution> double compute(S solution, char[][] decodedSequences)
    {
        return SP.compute(solution, decodedSequences) - AGP.compute(solution, decodedSequences);
    }

    @Override
    public boolean isAMinimizationScore()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return "SOP-AGP";
    }

    @Override
    public String getDescription()
    {
        return "SOP minus AGP";
    }
    
}
