package org.uma.jmetalmsa.problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalmsa.crossover.SPXMSACrossover;
import org.uma.jmetalmsa.mutation.InsertARandomGapMSAMutation;
import org.uma.jmetalmsa.mutation.MergeAdjunctedGapsGroupsMSAMutation;
import org.uma.jmetalmsa.mutation.MultipleMSAMutation;
import org.uma.jmetalmsa.mutation.MultipleShuffledMSAMutation;
import org.uma.jmetalmsa.mutation.RandomMSAMutation;
import org.uma.jmetalmsa.mutation.ShiftClosedGapsMSAMutation;
import org.uma.jmetalmsa.mutation.SplitANonGapsGroupMSAMutation;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.solution.util.ArrayChar;

public class SATE_MSAProblem extends MSAProblem
{
  //public String PDBPath;
    //public String InstanceBalibase;

    public String DataPath;
    public String PreComputedPath;
    public String inputFile = "input.fasta";

    /**
     * Constructor
     */
    public SATE_MSAProblem(String problemName, String dataBaseDirectory, List<Score> scoreList)
            throws IOException, CompoundNotFoundException
    {
        super(scoreList);
        
        List<String> scoreNameList = new ArrayList<>();
        for (Score s : scoreList)
        {
            scoreNameList.add(s.getName());
        }
        Collections.sort(scoreNameList);

        String problemNameWithScoreList = problemName;
        for (String name : scoreNameList)
        {
            name = name.replace(' ', '-');
            problemNameWithScoreList += "_" + name;
        }
        setName(problemNameWithScoreList);

        setPaths(problemName, dataBaseDirectory);
        List<String> dataFiles = new ArrayList<>();
        dataFiles.add(PreComputedPath + "_clustalo");
        dataFiles.add(PreComputedPath + "_clustalw");
        dataFiles.add(PreComputedPath + "_fsa");
        dataFiles.add(PreComputedPath + "_kalign");
        dataFiles.add(PreComputedPath + "_mafft");
        dataFiles.add(PreComputedPath + "_muscle");
        dataFiles.add(PreComputedPath + "_pasta");
        dataFiles.add(PreComputedPath + "_prank");
        dataFiles.add(PreComputedPath + "_tcoffee");

        listOfSequenceNames = new ArrayList<>();
        listOfSequenceNames = readSeqNameFromAlignment(DataPath + inputFile);

        originalSequences = readDataFromFastaFile(DataPath + inputFile);

        setNumberOfVariables(originalSequences.size());
        setNumberOfConstraints(0);

        listOfPrecomputedStringAlignments = readPreComputedAlignments(dataFiles);

    }

    public void setPaths(String problemName, String dataBaseDirectory)
    {

    //String Group = "RV" + problemName.substring(2, 4).toString();
    //Directory with the PDB Files
        //PDBPath = dataBaseDirectory + "/aligned/strike/" + Group + "/" + problemName + "/";
        //SATE Instance Directory
        DataPath = dataBaseDirectory + "/" + problemName + "/";

    //InstanceBalibase = problemName;
        //Directory with the PreAlignments
        PreComputedPath = DataPath + inputFile;

    }

    @Override
    public List<MSASolution> createInitialPopulation(int Size)
    {
        List<MSASolution> population = new ArrayList<>(Size);

        JMetalRandom randomGenerator = JMetalRandom.getInstance();

        for (List<ArrayChar> sequenceList : listOfPrecomputedStringAlignments)
        {

            MSASolution newIndividual = new MSASolution(sequenceList, this);
            population.add(newIndividual);
        }

        int parent1, parent2;
        List<MSASolution> children, parents;
        SPXMSACrossover crossover = new SPXMSACrossover(1);
        
        InsertARandomGapMSAMutation mut1 = new InsertARandomGapMSAMutation(1.0);
        MergeAdjunctedGapsGroupsMSAMutation mut2 = new MergeAdjunctedGapsGroupsMSAMutation(1.0);
        ShiftClosedGapsMSAMutation mut3 = new ShiftClosedGapsMSAMutation(1.0);
        SplitANonGapsGroupMSAMutation mut4 = new SplitANonGapsGroupMSAMutation(1.0);
        List<MutationOperator<MSASolution>> mutList1 = new ArrayList<>();
        mutList1.add(mut1);
        mutList1.add(mut2);
        mutList1.add(mut3);
        mutList1.add(mut4);
        MultipleShuffledMSAMutation mut5 = new MultipleShuffledMSAMutation(1.0, mutList1);
        
        List<MutationOperator<MSASolution>> mutList2 = new ArrayList<>();
        mutList2.add(mut1);
        mutList2.add(mut2);
        mutList2.add(mut3);
        mutList2.add(mut4);
        mutList2.add(mut5);
        
        RandomMSAMutation finalMut = new RandomMSAMutation(0.6, mutList2);

        while (population.size() < Size)
        {
            parents = new ArrayList<>();

            parent1 = randomGenerator.nextInt(0, population.size() - 1);
            do
            {
                parent2 = randomGenerator.nextInt(0, population.size() - 1);
            } while (parent1 == parent2);
            parents.add(population.get(parent1));
            parents.add(population.get(parent2));

            children = crossover.execute(parents);
            
            finalMut.execute(children.get(0));
            finalMut.execute(children.get(1));
            
            population.add(children.get(0));
            population.add(children.get(1));

        }
        return population;
    }

}
