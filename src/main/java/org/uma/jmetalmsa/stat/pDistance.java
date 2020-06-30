/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.print.Collation;
import org.apache.commons.io.FileUtils;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.uma.jmetalmsa.solution.util.ArrayChar;

/**
 *
 * @author ali_nayeem
 */
public class pDistance
{
    
    ArrayList<String> speciesName;
    List<ArrayChar> sequenceList;
    static String dataDirectory = "example";
    static String problemNameArray[] =
    {
        "BB11005", "BB11018", "BB11020", "BB11033",
        "BB12001", "BB12013", "BB12022", "BB12035", "BB12044",
        "BB30002", "BB30008", "BB30015", "BB30022",
        "BB20001", "BB20010" ,"BB20022", "BB20033", "BB20041",
        "BB40001", "BB40013", "BB40025", "BB40038", "BB40048",
        "BB50001", "BB50005", "BB50010", "BB50016"
    };    
    static String features[] =
    {
        "dataset", "avg", "max", "min"
    };

    List<ArrayChar> readRefAlignmentBalibase(String problemName) throws Exception
    {
        String Group = "RV" + problemName.substring(2, 4).toString();
        String refAlignmentPath = dataDirectory + "/aligned/" + Group + "/" + problemName + ".msf_tfa"; //".fasta"
        LinkedHashMap<String, ProteinSequence> sequences = FastaReaderHelper.readFastaProteinSequence(new File(refAlignmentPath));
        List<ArrayChar> sequenceList = new ArrayList<>();
        for (Map.Entry<String, ProteinSequence> entry : sequences.entrySet())
        {
            sequenceList.add(new ArrayChar(entry.getValue().getSequenceAsString()));

        }
        return sequenceList;
    }
    
    public List<ArrayChar> readRefAlignment(String FastaPath) throws Exception
    {
        String refAlignmentPath = FastaPath; //dataDirectory + "/aligned/" + Group + "/" + problemName + ".msf_tfa"; //".fasta"
        LinkedHashMap<String, ProteinSequence> sequences = FastaReaderHelper.readFastaProteinSequence(new File(refAlignmentPath));
        sequenceList = new ArrayList<>();
        speciesName = new ArrayList<>();
        for (Map.Entry<String, ProteinSequence> entry : sequences.entrySet())
        {
            sequenceList.add(new ArrayChar(entry.getValue().getSequenceAsString()));
            speciesName.add(entry.getKey());

        }
        return sequenceList;
    }

    int calculateHammingDistance(ArrayChar a, ArrayChar b)
    {
        int count = 0;
        for (int i = 0; i < a.getSize(); i++)
        {
            if (a.charAt(i) != b.charAt(i))
            {
                count++;
            }
        }
        return count;
    }

    Double calculateSubstitution(ArrayChar a, ArrayChar b)
    {
        int subCount = 0, pairCount = 0;
        for (int i = 0; i < a.getSize(); i++)
        {
            if (a.charAt(i) != '-' && b.charAt(i) != '-')
            {
                if (a.charAt(i) != b.charAt(i))
                {
                    subCount++;
                }
                pairCount++;
            }
        }
        //System.out.println(pairCount);
        return 1.0 * subCount / pairCount;
    }

    List<Double> calculateAllPdistances(List<ArrayChar> sequenceList)
    {
        List<Double> distList = new ArrayList<>();
        //double[][] distMatrix = new double[sequenceList.size()][sequenceList.size()];
        for (int i = 0; i < sequenceList.size() - 1; i++)
        {
            int dist = 0;
            for (int j = i + 1; j < sequenceList.size(); j++)
            {
                distList.add(calculateSubstitution(sequenceList.get(i), sequenceList.get(j)));
            }
        }
        return distList;
    }
    
    public void printAllPdistances()
    {
        List<Double> distList = new ArrayList<>();
        //double[][] distMatrix = new double[sequenceList.size()][sequenceList.size()];
        for (int i = 0; i < sequenceList.size() - 1; i++)
        {
            int dist = 0;
            for (int j = i + 1; j < sequenceList.size(); j++)
            {
                System.out.println(speciesName.get(i) + " & " + speciesName.get(j) + " : " + calculateSubstitution(sequenceList.get(i), sequenceList.get(j))); 
            }
        }
    }

    List<Integer> countSiteForAllSequence(List<ArrayChar> sequenceList)
    {
        List<Integer> siteCountList = new ArrayList<>();
        for (int i = 0; i < sequenceList.size(); i++)
        {
            ArrayChar a = sequenceList.get(i);
            int count = 0;
            for (int j = 0; j < a.getSize(); j++)
            {
                if (a.charAt(j) != '-')
                {
                    count++;
                }
            }
            siteCountList.add(count);
        }
        return siteCountList;
    }

    Double getAvg(List<Double> distList)
    {
        Double sum = 0.0;
        for (double val : distList)
        {
            sum += val;
        }
        return sum / distList.size();
    }

    Map<String, String> calculateStat(String problem, List<Double> distList)
    {
        Map<String, String> statMap = new HashMap<>();
        statMap.put("dataset", problem);
        statMap.put("avg", getAvg(distList).toString());
        statMap.put("max", Collections.max(distList).toString());
        statMap.put("min", Collections.min(distList).toString());
        return statMap;

    }

    String getStatLine(Map<String, String> statMap)
    {
        String line = statMap.get(features[0]);
        for (int i = 1; i < features.length; i++)
        {
            line = line + "," + statMap.get(features[i]);
        }
        line = line + '\n';
        return line;
    }

    void writeAllPdistanceToFile()
    {

    }

    String getAllPdistLine(String problem, List<Double> distList)
    {
        String line = problem;
        for (Double val : distList)
        {
            line = line + "," + val.toString();
        }
        line = line + '\n';
        return line;
    }

    public static void main(String[] arg) throws Exception
    {
        pDistance pDist = new pDistance();
        String allPDist = "";
        //String problem = "BB11001";//_ShortproblemNameArray[0];
        //System.out.println(pDist.getStatLine(pDist.calculateStat(problem, distList)));
        for (int i = 0; i < problemNameArray.length; i++)
        {
            String problem = problemNameArray[i];
            List<ArrayChar> sequenceList = pDist.readRefAlignmentBalibase(problem);
            List<Double> distList = pDist.calculateAllPdistances(sequenceList);
            allPDist += pDist.getAllPdistLine(problem, distList);
        }
        FileUtils.writeStringToFile(new File("balibase_all_p_distance.txt"), allPDist);
    }
}
