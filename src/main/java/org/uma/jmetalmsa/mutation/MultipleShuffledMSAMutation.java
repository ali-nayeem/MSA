package org.uma.jmetalmsa.mutation;

import java.util.ArrayList;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalmsa.solution.MSASolution;

import java.util.List;

public class MultipleShuffledMSAMutation extends MultipleMSAMutation
{

    public MultipleShuffledMSAMutation(double mutationProbability, List<MutationOperator<MSASolution>> mutationOperatorList)
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
            shuffleMutationOperatorList();
            for (MutationOperator<MSASolution> mutation : mutationOperatorList)
            {
                solution = mutation.execute(solution);

            }          
        }


        return solution;
    }

    private void shuffleMutationOperatorList()
    {
        for (int i = mutationOperatorList.size() - 1; i > 0; i--)
        {
            int index = randomGenerator.nextInt(0, i);
            // Simple swap
            MutationOperator<MSASolution> mutation = mutationOperatorList.get(index);
            mutationOperatorList.set(index, mutationOperatorList.get(i));
            mutationOperatorList.set(i, mutation);
        }
    }

}
