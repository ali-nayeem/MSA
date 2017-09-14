/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.uma.jmetalmsa.problem.MSAProblem;

/**
 *
 * @author Nayeem
 */
public class MapEncodedVARFileToAlgo
{

    static String searchRoot = "/home/ali_nayeem/data/"; //"/home/ali_nayeem/data/" "/home/ali_nayeem/NetBeansProjects/MSA/experiment/"
    static String varFileName = "VAR";
    static String pattern = "VAR";
    static String instancePath = "dataset/100S";
    static String instanceName = "R0";
    static String singleVarFileName = "AllEncodedVAR";
    static String refVarFile = "/home/ali_nayeem/data/shortShuffledVAR";
    //static int approxLineLength = 1200;

    public Map<String, Integer> getUniqueEncodedVarFile(Map<String, Integer> alignmentToCount, String encodedVarFilePath, MSAProblem problem) throws Exception
    {
        int allCount = 0;
        BufferedReader br = new BufferedReader(new FileReader(encodedVarFilePath));
        //Map<String, Integer> AlignmentToCount = new HashMap<>();

        while (br.ready())
        {
            String oneLine = "";
            StringBuilder encodedAlignment = new StringBuilder(problem.getNumberOfVariables() * problem.originalSequences.get(0).getSize());
            encodedAlignment.append("<\n");

            do
            {
                oneLine = br.readLine();
            } while (oneLine != null && !oneLine.startsWith("<"));

            while (oneLine != null)
            {
                oneLine = br.readLine();
                if (oneLine.startsWith(">"))
                {
                    break;
                }

                encodedAlignment.append(oneLine).append("\n");

            }

            if (oneLine == null)
            {
                break;
            }

            encodedAlignment.append(">");
            //AlignmentToCount.add(encodedAlignment.toString());
            String encodedAlignmentString = encodedAlignment.toString();
            alignmentToCount.put(encodedAlignmentString, alignmentToCount.getOrDefault(encodedAlignmentString, 0) + 1);
            allCount++;
        }
        br.close();
        System.out.println("Total: " + allCount);
        System.out.println("Unique:" + alignmentToCount.size());
        return alignmentToCount;
    }

    public static void main(String[] arg) throws Exception
    {
        List<String> singleVarPathList = SearchAndEncodeVAR_Linux.getPathList(searchRoot, singleVarFileName);
        Map<String, Map> algoNameToVarMap = new HashMap<>();
        for (String path : singleVarPathList)
        {
            String[] pathSegments = path.split("/");
            String algoName = pathSegments[pathSegments.length - 3] + "/" + pathSegments[pathSegments.length - 2];
            algoNameToVarMap.put(algoName, algoNameToVarMap.getOrDefault(algoName, new HashMap<>()));
        }
        String[] algoNameArray = new String[algoNameToVarMap.size()];
        for (String algoName : algoNameToVarMap.keySet())
        {
            System.out.println(algoName + " => " + algoNameArray.length);
            algoNameArray[algoNameArray.length] = algoName;
        }
    }
}
