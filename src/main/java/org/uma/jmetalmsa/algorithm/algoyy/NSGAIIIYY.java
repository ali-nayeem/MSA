/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.algorithm.algoyy;

import java.util.ArrayList;
import java.util.List;
//import jmetal.core.*;

//import jmetal.util.*;
//import jmetal.util.ranking.NondominatedRanking;
//import jmetal.util.ranking.Ranking;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetalmsa.algorithm.algoyy.util.VectorGenerator;
import org.uma.jmetalmsa.algorithm.algoyy.util.OneLevelWeightVectorGenerator;
import org.uma.jmetalmsa.algorithm.algoyy.util.TwoLevelWeightVectorGenerator;
import org.uma.jmetalmsa.algorithm.algoyy.util.Niching;

/**
 *
 * @author Nayeem
 */
public class NSGAIIIYY<S extends Solution<?>> implements Algorithm<List<S>>
{

    protected int populationSize_;
    protected final int maxEvaluations_;

    private final int div1_;
    private final int div2_;

    private List<S> population_;
    List<S> offspringPopulation_;
    //List<S> union_;

    protected final Problem<S> problem_;

    protected final SolutionListEvaluator<S> evaluator_;

    protected int generations_;
    protected int evaluations_ = 0;

    protected CrossoverOperator<S> crossover_;
    protected MutationOperator<S> mutation_;
    protected SelectionOperator<List<S>, S> selection_;

    protected double[][] lambda_; // reference points

    protected boolean normalize_; // do normalization or not

    public NSGAIIIYY(Problem<S> problem, int maxEvaluations, int populationSize, int div1, int div2, boolean normalize,
            CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
            SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator)
    {
        super();
        this.problem_ = problem;
        this.maxEvaluations_ = maxEvaluations;
        this.populationSize_ = populationSize;
        this.div1_ = div1;
        this.div2_ = div2;
        this.normalize_ = normalize;

        this.crossover_ = crossoverOperator;
        this.mutation_ = mutationOperator;
        this.selection_ = selectionOperator;

        this.evaluator_ = evaluator;
    }

    protected List<S> createInitialPopulation()
    {
        List<S> population = new ArrayList<>(populationSize_);
        for (int i = 0; i < populationSize_; i++)
        {
            S newIndividual = problem_.createSolution();
            population.add(newIndividual);
        }
        return population;
    }

    @Override
    public void run()
    {
        //int maxGenerations_;

        generations_ = 0;

        VectorGenerator vg = new TwoLevelWeightVectorGenerator(div1_, div2_,
                problem_.getNumberOfObjectives());
        lambda_ = vg.getVectors();

        populationSize_ = vg.getVectors().length;
        if (populationSize_ % 2 != 0)
        {
            populationSize_ += 1;
        }

        population_ = createInitialPopulation();
        evaluatePopulation(population_);

        while (evaluations_ < maxEvaluations_)
        {
            offspringPopulation_ = new ArrayList<>(populationSize_);
            for (int i = 0; i < populationSize_; i += 2)
            {
                List<S> parents = new ArrayList<>(2);
                parents.add(selection_.execute(population_));
                parents.add(selection_.execute(population_));

                List<S> offspring = crossover_.execute(parents);

                mutation_.execute(offspring.get(0));
                mutation_.execute(offspring.get(1));

                offspringPopulation_.add(offspring.get(0));
                offspringPopulation_.add(offspring.get(1));
            }

            evaluatePopulation(offspringPopulation_);

            List<S> union_ = new ArrayList<>();
            union_.addAll(population_);
            union_.addAll(offspringPopulation_);

            // Ranking the union
            //Ranking ranking = new NondominatedRanking(union_);
            Ranking<S> ranking = computeRanking(union_);

            int remain = populationSize_;
            int index = 0;
            //List<S> front = null;
            population_.clear();

            // Obtain the next front
            List<S> front = ranking.getSubfront(index);

            while ((remain > 0) && (remain >= front.size()))
            {

                for (int k = 0; k < front.size(); k++)
                {
                    population_.add(front.get(k));
                } // for

                // Decrement remain
                remain = remain - front.size();

                // Obtain the next front
                index++;
                if (remain > 0)
                {
                    front = ranking.getSubfront(index);
                } // if
            }

            if (remain > 0)
            { // front contains individuals to insert

                new Niching(population_, front, lambda_, remain, normalize_)
                        .execute();
                remain = 0;
            }

            generations_++;

        }

        //Ranking ranking = new NondominatedRanking(population_);
        //return ranking.getSubfront(0);
    }

    @Override
    public List<S> getResult()
    {
        return getNonDominatedSolutions(population_);
    }

    protected List<S> getNonDominatedSolutions(List<S> solutionList)
    {
        return SolutionListUtils.getNondominatedSolutions(solutionList);
    }

    @Override
    public String getName()
    {
        return "NSGAIIIYY";
    }

    @Override
    public String getDescription()
    {
        return "Nondominated Sorting Genetic Algorithm version III. Version of Yuan Yuan";
    }

    protected List<S> evaluatePopulation(List<S> population)
    {
        population = evaluator_.evaluate(population, problem_);
        //Add: evaluate constraints
        evaluations_ += populationSize_;

        return population;
    }

    protected Ranking<S> computeRanking(List<S> solutionList)
    {
        Ranking<S> ranking = new DominanceRanking<>();
        ranking.computeRanking(solutionList);

        return ranking;
    }

} // NSGA-III
