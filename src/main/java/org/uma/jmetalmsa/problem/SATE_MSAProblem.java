package org.uma.jmetalmsa.problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalmsa.algorithm.algoyy.util.PseudoRandom;
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
import org.uma.jmetalmsa.util.SynchronizedMersenneTwister;

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
    
    public List<MSASolution> createInitialPopulationRandomly(int Size)
    {
        System.out.println(getName() + " :Generating init pop randomly############################################");
        List<MSASolution> population = new ArrayList<>(Size);
        int longestSeqLength = getLongestSeqLength();
        GenerateSingleSolutionRandomly gen = new GenerateSingleSolutionRandomly(longestSeqLength, this);
        
        for (int i = 0; i < Size; i++)
        {
            population.add(gen.generateOneSol());
            //System.out.println(population.get(i).toString());
        }
        
        return population;
    }
    
    public int getLongestSeqLength()
    {
        int longestSeqLength = -1;
        
        for (int i = 0; i < originalSequences.size(); i++)
        {
            if (originalSequences.get(i).getSize() > longestSeqLength) 
            {
                longestSeqLength = originalSequences.get(i).getSize();
            }
        }
        return longestSeqLength;
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

        /*private void compressOneGapList(List<Integer> oneGapList)
        {
            for (int i = 1; i < oneGapList.size()-1; i+=2)
            {
                if ( (oneGapList.get(i+1) - oneGapList.get(i)) == 1)
                {
                    oneGapList.remove(i);
                    oneGapList.remove(i);
                    i-=2;
                }
            }
        }*/
    }
    /*public List<MSASolution> createInitialPopulation(int Size, boolean removePrecomputed)
    {
        if (removePrecomputed)
        {
            Size += listOfPrecomputedStringAlignments.size();
        }
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
        if (removePrecomputed)
        {
            for (int i = 0; i < listOfPrecomputedStringAlignments.size(); i++)
            {
              population.remove(i);
            }
        }
        return population;
    }*/
}
