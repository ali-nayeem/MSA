package org.uma.jmetalmsa.algorithm.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.solution.MSASolution;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.util.JMetalException;

public class MOEADMSA extends AbstractMOEAD<MSASolution> {

    public MOEADMSA(Problem<MSASolution> problem,
            int populationSize,
            int resultPopulationSize,
            int maxEvaluations,
            MutationOperator<MSASolution> mutation,
            CrossoverOperator<MSASolution> crossover,
            FunctionType functionType,
            String dataDirectory,
            double neighborhoodSelectionProbability,
            int maximumNumberOfReplacedSolutions,
            int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
                dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
                neighborSize);

        randomGenerator = JMetalRandom.getInstance();
    }

    @Override
    public void run() {
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        for (int i = 0; i < populationSize; i++) {
            problem.evaluate(population.get(i));
        }
        initializeIdealPoint();
        initializeNadirPoint();

        evaluations = populationSize;
        do {
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            for (int i = 0; i < populationSize; i++) {
                int subProblemId = permutation[i];

                NeighborType neighborType = chooseNeighborType();
                List<MSASolution> parents = parentSelection(subProblemId, neighborType);

                List<MSASolution> children = crossoverOperator.execute(parents);

                MSASolution child = children.get(0);
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNadirPoint(child);
                updateNeighborhood(child, subProblemId, neighborType);
            }
        } while (evaluations < maxEvaluations);
    }

    protected void initializePopulation() {
        population = ((MSAProblem) problem).createInitialPopulation(populationSize);
    }

    @Override
    public List<MSASolution> getResult() {
        return population;
    }

    protected List<MSASolution> parentSelection(int subProblemId, NeighborType neighborType) {
        List<Integer> matingPool = matingSelection(subProblemId, 2, neighborType);

        List<MSASolution> parents = new ArrayList<>(3);

        parents.add(population.get(matingPool.get(0)));
        parents.add(population.get(matingPool.get(1)));

        return parents;
    }

    /**
     * @param subproblemId the id of current subproblem
     * @param neighbourType neighbour type
     */
    protected List<Integer> matingSelection(int subproblemId, int numberOfSolutionsToSelect, NeighborType neighbourType) {
        int neighbourSize;
        int selectedSolution;

        List<Integer> listOfSolutions = new ArrayList<>(numberOfSolutionsToSelect);

        neighbourSize = neighborhood[subproblemId].length;
        while (listOfSolutions.size() < numberOfSolutionsToSelect) {
            int random;
            if (neighbourType == NeighborType.NEIGHBOR) {
                random = randomGenerator.nextInt(0, neighbourSize - 1);
                selectedSolution = neighborhood[subproblemId][random];
            } else {
                selectedSolution = randomGenerator.nextInt(0, populationSize - 1);
            }
            boolean flag = true;
            for (Integer individualId : listOfSolutions) {
                if (individualId == selectedSolution) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                listOfSolutions.add(selectedSolution);
            }
        }

        return listOfSolutions;
    }

    @Override
    public String getName() {
        return "MOEAD";
    }

    @Override
    public String getDescription() {
        return "Version of MOEA/D for solving MSA problems";
    }

    double fitnessFunction(MSASolution individual, double[] lambda) throws JMetalException {
        double fitness;

        if (FunctionType.TCHE.equals(functionType)) {
            double maxFun = -1.0e+30;

            for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
                double diff = Math.abs(individual.getObjective(n) - idealPoint[n]) / (nadirPoint[n] - idealPoint[n]);

                double feval;
                if (lambda[n] == 0) {
                    feval = 0.0001 * diff;
                } else {
                    feval = diff * lambda[n];
                }
                if (feval > maxFun) {
                    maxFun = feval;
                }
            }

            fitness = maxFun;
        } 
        else if (FunctionType.AGG.equals(functionType)) {
            double sum = 0.0;
            for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
                sum += (lambda[n]) * ((individual.getObjective(n) - idealPoint[n]) / (nadirPoint[n] - idealPoint[n]));
            }

            fitness = sum;

        } else if (FunctionType.PBI.equals(functionType)) {
            double d1, d2, nl;
            double theta = 5.0;

            d1 = d2 = nl = 0.0;

            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                d1 += (individual.getObjective(i) - idealPoint[i]) * lambda[i];
                nl += Math.pow(lambda[i], 2.0);
            }
            nl = Math.sqrt(nl);
            d1 = Math.abs(d1) / nl;

            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                d2 += Math.pow((individual.getObjective(i) - idealPoint[i]) - d1 * (lambda[i] / nl), 2.0);
            }
            d2 = Math.sqrt(d2);

            fitness = (d1 + theta * d2);
        } 
        else {
            throw new JMetalException(" MOEAD.fitnessFunction: unknown type " + functionType);
        }
        return fitness;
    }

    protected void initializeNadirPoint() {
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            nadirPoint[i] = -1.0e+30;
        }
        for (int i = 0; i < populationSize; i++) {
            updateNadirPoint(population.get(i));
        }
    }

    protected void updateNadirPoint(MSASolution individual) {
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            if (individual.getObjective(i) > nadirPoint[i]) {
                nadirPoint[i] = individual.getObjective(i);
            }
        }
    }

    protected void updateNeighborhood(MSASolution individual, int subProblemId, NeighborType neighborType) throws JMetalException {
        int size;
        int time;

        time = 0;

        if (neighborType == NeighborType.NEIGHBOR) {
            size = neighborhood[subProblemId].length;
        } else {
            size = population.size();
        }
        int[] perm = new int[size];

        MOEADUtils.randomPermutation(perm, size);

        for (int i = 0; i < size; i++) {
            int k;
            if (neighborType == NeighborType.NEIGHBOR) {
                k = neighborhood[subProblemId][perm[i]];
            } else {
                k = perm[i];
            }
            double f1, f2;

            f1 = fitnessFunction(population.get(k), lambda[k]);
            f2 = fitnessFunction(individual, lambda[k]);

            if (f2 < f1) {
                population.set(k, (MSASolution) individual.copy());
                time++;
            }

            if (time >= maximumNumberOfReplacedSolutions) {
                return;
            }
        }
    }
}
