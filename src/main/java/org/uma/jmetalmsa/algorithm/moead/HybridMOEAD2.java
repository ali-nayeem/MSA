/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.algorithm.moead;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalmsa.algorithm.algoyy.util.TwoLevelWeightVectorGenerator;
import org.uma.jmetalmsa.algorithm.algoyy.util.VectorGenerator;
import org.uma.jmetalmsa.experiment.SolutionListOutputMSA;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.solution.util.ArrayChar;

/**
 *
 * @author ali_nayeem
 */
public class HybridMOEAD2 extends MOEADMSA
{

    protected int div1, div2, runId;
    protected String initSolutionsRoot = "/Users/ali_nayeem/PycharmProjects/pasta-extension/scripts";
    protected String toolPath = "/Users/ali_nayeem/NetBeansProjects/muscle_extesion/muscle";
    long timestamp = java.lang.System.currentTimeMillis();
    static int instanceCount = 0;
    String toolIn, toolOut, var;
    protected String dataset;
    protected boolean logVAR = false;

    public HybridMOEAD2(Problem<MSASolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations, MutationOperator<MSASolution> mutation, CrossoverOperator<MSASolution> crossover, FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize, int div1, int div2)
    {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
        this.div1 = div1;
        this.div2 = div2;
        runId = ++instanceCount;
        //initSolutionsRoot += problem.getName();
        dataset = problem.getName();
        String statDir = initSolutionsRoot + "/stat/Run" + runId;
        File directory = new File(statDir);
        if (! directory.exists()){
            directory.mkdir();
        // If you require it to make the entire directory path including parents,
        // use directory.mkdirs(); here instead.
    }
        toolIn = statDir + "/in-" + problem.getName() + timestamp + runId + ".txt";
        toolOut = statDir + "/out-" + problem.getName() + timestamp + runId + ".txt";
        var = statDir + "/VAR";
    }

    @Override
    public String getDescription()
    {
        return "Version of MOEA/D hybridized with MUSCLE"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName()
    {
        return "Hybrid-MOEAD-MUSCLE"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void initializeUniformWeight()
    {
        try
        {
            //VectorGenerator vg = new TwoLevelWeightVectorGenerator(div1, div2, problem.getNumberOfObjectives());
            //lambda = vg.getVectors();
            Scanner sc = new Scanner(new BufferedReader(new FileReader(initSolutionsRoot + "/weights4D-30.csv")));
            int rows = 30;
            int columns = 4;
            double [][] myArray = new double[rows][columns];
            while(sc.hasNextLine()) {
                for (int i=0; i<myArray.length; i++) {
                    String[] line = sc.nextLine().trim().split(",");
                    for (int j=0; j<line.length; j++) {
                        myArray[i][j] = Double.parseDouble(line[j]);
                    }
                }
            }
            lambda = myArray;
            populationSize = lambda.length;//vg.getVectors().length;
            neighborhood = new int[populationSize][neighborSize];
//        if (populationSize % 2 != 0)
//        {
//            populationSize += 1;
//        }
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(HybridMOEAD2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void initializePopulation()
    {
          //population = ((MSAProblem) problem).createInitialPopulation(populationSize);
        population = new ArrayList<>(populationSize);
        while (population.size() < populationSize)
        {
            try
            {
                List<ArrayChar> strAlignment = ((MSAProblem) problem).readDataFromFastaFile(initSolutionsRoot + "/output/muscle-balibase/4obj-30w/" 
                        + dataset + "/" + population.size() + ".aln");
                MSASolution s = new MSASolution(strAlignment, ((MSAProblem) problem));
                population.add(s);
            } catch (IOException ex)
            {
                Logger.getLogger(HybridMOEAD2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CompoundNotFoundException ex)
            {
                Logger.getLogger(HybridMOEAD2.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    @Override
    public void run()
    {
        initializeUniformWeight();
        initializePopulation();
        initializeNeighborhood();

        for (int i = 0; i < populationSize; i++)
        {
            problem.evaluate(population.get(i));
        }
        initializeIdealPoint();
        initializeNadirPoint();

        evaluations = populationSize;
        int gen = 0;
        do
        {
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            for (int i = 0; i < populationSize; i++)
            {
                int subProblemId = permutation[i];

                NeighborType neighborType = chooseNeighborType();
                List<MSASolution> parents = parentSelection(subProblemId, neighborType);

                List<MSASolution> children = crossoverOperator.execute(parents);

                MSASolution child = children.get(randomGenerator.nextInt(0, 1));
                mutationOperator.execute(child);
                //callMSAtool(child, lambda[subProblemId]);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNadirPoint(child);
                updateNeighborhood(child, subProblemId, neighborType);
            }
            System.out.println("[Thread-" + Thread.currentThread().getId() + "]\t" + problem.getName() + ", run:" + runId + ", Gen:" + ++gen);
            if(gen % 10 == 0 && logVAR)
            {
                DefaultFileOutputContext varFile = new  DefaultFileOutputContext(var + gen + ".txt");
                new SolutionListOutputMSA(population)
                .setVarFileOutputContext(varFile)
                .print();
            }
        } while (evaluations < maxEvaluations); //To change body of generated methods, choose Tools | Templates.
        saveIndividualSolutions(population);
    }
    
    protected void saveIndividualSolutions(List<MSASolution> pop)
    {
        String outDir = initSolutionsRoot + "/output/muscle-balibase/MOEAD-4obj-30w/" + dataset;
        try
        {
            Files.createDirectories(Paths.get( outDir ));
     
            for (int i = 0; i < pop.size(); i++)
            {
                pop.get(i).printSolutionToFasta(outDir + "/" + i + ".aln");
            }
        
        } catch (Exception ex)
        {
            Logger.getLogger(HybridMOEAD2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void initializeNeighborhood()
    {
        double[] x = new double[populationSize];
        int[] idx = new int[populationSize];

        for (int i = 0; i < populationSize; i++)
        {
            // calculate the distances based on weight vectors
            for (int j = 0; j < populationSize; j++)
            {
                x[j] = MOEADUtils.distVector(lambda[i], lambda[j]);
                idx[j] = j;
            }

            // find 'niche' nearest neighboring subproblems
            MOEADUtils.minFastSort(x, idx, populationSize, neighborSize);

            System.arraycopy(idx, 0, neighborhood[i], 0, neighborSize);
        }
    }

    protected void callMSAtool(MSASolution sol, double[] lambda) //      ./muscle -simg $w1 -simng $w2 -osp $w3 -gap $w4 -in $datapath -out $msa -objscore sp  #-quiet

    {
        try //      ./muscle -simg $w1 -simng $w2 -osp $w3 -gap $w4 -in $datapath -out $msa -objscore sp  #-quiet
        {
            sol.printSolutionToFasta(toolIn);
            String cmd = toolPath + " -simg " + lambda[0] + " -simng " + lambda[1] + " -osp " + lambda[2] + " -gap " + lambda[3] +
                    " -in " + toolIn + " -out " + toolOut + " -maxiters 1 -refine -quiet"; //-quiet 
            runBashCommand(cmd);
            List<ArrayChar> strAlignment = ((MSAProblem) problem).readDataFromFastaFile(toolOut);
            sol = new MSASolution(strAlignment, ((MSAProblem) problem));
        } catch (Exception ex)
        {
            Logger.getLogger(HybridMOEAD2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void runBashCommand(String command) throws Exception
    {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        String line = "";
//        while ((line = reader.readLine()) != null)
//        {
//            System.out.println(line);
//        }
//        reader.close();
//        return reader;
    }

}
