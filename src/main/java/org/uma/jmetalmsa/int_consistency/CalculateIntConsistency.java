/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.int_consistency;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.EntropyScore;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.stat.CalculateObjetivesFromVAR;
//import org.uma.jmetalmsa.util.MSADistance;

/**
 *
 * @author Nayeem
 */
public class CalculateIntConsistency
{
    byte[][] msa;
    int taxaCount;
    RelativeDistance [] relDistArray;
    List<RelativeDistance.Neighbor> refTaxaNeighbors;
    PairwiseDistance pd = new Similarity();
    Closeness criteria = new Closeness(this, 3);
    int refTaxaId;
    public CalculateIntConsistency(MSASolution msa)
    {
        this.msa = convertToByteArray(msa.decodeToMatrix());
        taxaCount = msa.getMSAProblem().getNumberOfVariables();
        relDistArray = new RelativeDistance[taxaCount];
        int max = -1, maxId=-1;
        for (int i = 0; i < taxaCount; i++)
        {
            if (msa.getAlignmentLength(i)>max)
            {
                max = msa.getAlignmentLength(i);
                maxId = i;
            }
        }
        refTaxaId = maxId;
    }
    public CalculateIntConsistency(MSASolution msa, PairwiseDistance pd)
    {
        this(msa);
        this.pd = pd;
    }
    public byte[][] convertToByteArray(char [][] ca)
    {
        byte[][] ba = new byte[ca.length][ca[0].length];
        
        for (int i = 0; i < ca.length; i++)
        {
            for (int j = 0; j < ca[0].length; j++)
            {
                ba[i][j] = (byte)ca[i][j];
            }
        }
        return ba;
    }
    void generateAllRetaiveDistances()
    {
        for (int i = 0; i < taxaCount; i++)
        {
            relDistArray[i] = new RelativeDistance(i, taxaCount);
            //relDistArray[i].generateRelativeDist(msa, pd);
            relDistArray[i].generateRelativeDistGPU(msa);
            System.out.println("Taxa: "+i);
        }
        refTaxaNeighbors = relDistArray[refTaxaId].calculateSortedNeighbor();
    }
    
    double mainCalculation()
    {
        return criteria.calculate();
    }
    
    
    public static void main(String[] arg) throws Exception
    {
        String instancePath = "dataset/100S";
        String instanceName = "23S.E"; //23S.E
        String inputFilePath =  "F:\\Phd@CSE,BUET\\Com. Biology\\MSA\\Dataset\\scripts\\input\\NumGaps_SOP\\precomputedInit\\uniqueCombined_"+instanceName;
        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new EntropyScore());
        MSAProblem problem = new SATE_MSAProblem(instanceName, instancePath, scoreList);
        //numOfSeq = problem.getNumberOfVariables();
        CalculateObjetivesFromVAR ob = new CalculateObjetivesFromVAR();
        List<MSASolution> pop = ob.createPopulationFromEncodedVarFile(inputFilePath, problem);
        //char [][] msa = pop.get(0).decodeToMatrix();
        for (int i = 0; i < pop.size(); i++)
        {
            CalculateIntConsistency self = new CalculateIntConsistency(pop.get(i));
            self.generateAllRetaiveDistances();
            System.out.println(self.mainCalculation());
        }
        
        //System.out.println(neighborList.get(3));
    }
    
}
