package org.uma.jmetalmsa.problem;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalmsa.crossover.SPXMSACrossover;
import org.uma.jmetalmsa.mutation.InsertARandomGapMSAMutation;
import org.uma.jmetalmsa.mutation.MergeAdjunctedGapsGroupsMSAMutation;
import org.uma.jmetalmsa.mutation.MultipleShuffledMSAMutation;
import org.uma.jmetalmsa.mutation.RandomMSAMutation;
import org.uma.jmetalmsa.mutation.ShiftClosedGapsMSAMutation;
import org.uma.jmetalmsa.mutation.SplitANonGapsGroupMSAMutation;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.solution.util.ArrayChar;

public class BAliBASE_MSAProblem extends MSAProblem {
  public String PDBPath;
  public String InstanceBalibase;
  public String BalibasePath;
  public String PreComputedPath;

  /**
   * Constructor
   */
  public BAliBASE_MSAProblem(String problemName, String dataBaseDirectory, List<Score> scoreList)
          throws IOException, CompoundNotFoundException {
    super(scoreList);

     setName(problemName);

    setPaths(problemName, dataBaseDirectory);
    List<String> dataFiles = new ArrayList<>();
    dataFiles.add(PreComputedPath + ".tfa_clu");
    dataFiles.add(PreComputedPath + ".tfa_muscle");
    dataFiles.add(PreComputedPath + ".tfa_kalign");
    dataFiles.add(PreComputedPath + ".tfa_retalign");
    dataFiles.add(PreComputedPath + ".fasta_aln");
    dataFiles.add(PreComputedPath + ".tfa_probcons");
    dataFiles.add(PreComputedPath + ".tfa_mafft");
    dataFiles.add(PreComputedPath + ".tfa_fsa");
    dataFiles.add(PreComputedPath + ".tfa_pasta");

    listOfSequenceNames = new ArrayList<>();
    listOfSequenceNames = readSeqNameFromAlignment(BalibasePath + InstanceBalibase + ".tfa");

    originalSequences = readDataFromFastaFile(BalibasePath + InstanceBalibase + ".tfa");

    setNumberOfVariables(originalSequences.size());
    setNumberOfConstraints(0);

    listOfPrecomputedStringAlignments = readPreComputedAlignments(dataFiles);

  }


  
  public void setPaths(String problemName, String dataBaseDirectory) {

    String Group = "RV" + problemName.substring(2, 4).toString();

    //Directory with the PDB Files
    PDBPath = dataBaseDirectory + "/aligned/strike/" + Group + "/" + problemName + "/";

    //Balibase Directory
    BalibasePath = dataBaseDirectory + "/bb3_release/" + Group + "/";

    InstanceBalibase = problemName;

    //Directory with the PreAlignments
    PreComputedPath = dataBaseDirectory + "/aligned/" + Group + "/" + InstanceBalibase;


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
