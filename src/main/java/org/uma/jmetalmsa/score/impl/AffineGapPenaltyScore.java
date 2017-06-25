package org.uma.jmetalmsa.score.impl;

import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.util.distancematrix.DistanceMatrix;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.solution.util.ArrayChar;
import org.uma.jmetalmsa.util.MSADistance;

import java.util.List;
import org.uma.jmetalmsa.score.Score;

/**
 * @author Nayeem
 */
public class AffineGapPenaltyScore implements Score
{
   
    public double weightGapOpen = 6;
    public double weightGapExtend = 0.85;

    public AffineGapPenaltyScore(double weightGapOpen, double weightGapExtend)
    {
        this.weightGapOpen = weightGapOpen;
        this.weightGapExtend = weightGapExtend;
    }

    public AffineGapPenaltyScore()
    {

    }

    @Override
    public <S extends MSASolution> double compute(S solution, char[][] decodedSequences)
    {
        int lengthSequences = solution.getAlignmentLength();
        int numberOfVariables = solution.getNumberOfVariables();

        double affineGapPenaltyScore = 0;
        for (int i = 0; i < numberOfVariables; i++)
        {
            affineGapPenaltyScore += getAffineGapPenalty(solution.getVariableValue(i));
        }

        return affineGapPenaltyScore;

    }

    public double getAffineGapPenalty(List<Integer> gapsGroups)
    {
        int weightToOpenTheGap = 0, weightToExtendTheGap = 0;

        weightToOpenTheGap = gapsGroups.size() / 2;

        for (int i = 0; i < gapsGroups.size(); i += 2)
        {
            weightToExtendTheGap += gapsGroups.get(i + 1) - gapsGroups.get(i);
        }

        return (weightGapOpen * weightToOpenTheGap) + (weightGapExtend * weightToExtendTheGap);
    }

    @Override
    public boolean isAMinimizationScore()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Affine gap penalty";
    }

    @Override
    public String getDescription()
    {
        return "Affine gap penalty";
    }
}
