package org.uma.jmetalmsa.mutation;

import java.util.ArrayList;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalmsa.solution.MSASolution;

import java.util.List;

public class RandomMSAMutation extends MultipleMSAMutation
{

    public RandomMSAMutation(double mutationProbability, List<MutationOperator<MSASolution>> mutationOperatorList)
    {
        super(mutationProbability, mutationOperatorList);
    }

    @Override
    public MSASolution execute(MSASolution solution)
    {
        if (null == solution)
        {
            throw new JMetalException("Null parameter");
        }
        if (randomGenerator.nextDouble() < mutationProbability)
        {
            int randIndex = randomGenerator.nextInt(0, mutationOperatorList.size() - 1);
            MutationOperator<MSASolution> mutation = mutationOperatorList.get(randIndex);
            solution = mutation.execute(solution);
        }

        return solution;
    }

}
