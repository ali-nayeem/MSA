/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.EntropyScore;
import org.uma.jmetalmsa.score.impl.GapConcentrationScore;
import org.uma.jmetalmsa.score.impl.NumberOfAlignedColumnsScore;
import org.uma.jmetalmsa.score.impl.SimilarityGapsScore;
import org.uma.jmetalmsa.score.impl.SimilarityNonGapsScore;
//import static org.uma.jmetalmsa.stat.SearchAndEncodeVAR_Linux.instanceName;

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
    static int[][] algoWiseVarCount = new int[10000][7];
    static String algoWiseVarCountFileName = "/home/ali_nayeem/data/shortAlgoWiseVARCount";

    //static int approxLineLength = 1200;
    public Map<String, Integer> getEncodedVarWithCountFromFile(Map<String, Integer> alignmentToCount, String encodedVarFilePath, MSAProblem problem) throws Exception
    {
        int allCount = 0;
        BufferedReader br = new BufferedReader(new FileReader(encodedVarFilePath));
        //Map<String, Integer> AlignmentToCount = new HashMap<>();
        StringBuilder encodedAlignment = new StringBuilder(problem.getNumberOfVariables() * 1000);

        while (br.ready())
        {
            String oneLine = "";
            encodedAlignment.setLength(0);
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

    public static void updateAlgoWiseAlignmentCount(int algoId, Map<String, Integer> algoAlignmentToCount, String encodedVarFilePath, MSAProblem problem) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(encodedVarFilePath));
        StringBuilder encodedAlignment = new StringBuilder(problem.getNumberOfVariables() * 1000);
        int refAlignmentId = 0;

        for (int i = 0; i < algoWiseVarCount.length; i++)
        {
            String oneLine = "";
            encodedAlignment.setLength(0);
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
            String encodedAlignmentString = encodedAlignment.toString();
            int encodedAlignmentCount = algoAlignmentToCount.getOrDefault(encodedAlignmentString, 0);
            algoWiseVarCount[refAlignmentId][algoId] += encodedAlignmentCount;
            refAlignmentId++;

        }
        br.close();
    }

    public static void printAlgoWiseVarCountToFile(String filepath, String header) throws Exception
    {
        FileOutputStream stream = new FileOutputStream(filepath);
        FileChannel channel = stream.getChannel();
        byte[] strBytes;
        ByteBuffer buffer;
        
        strBytes = (header + "\n").getBytes();
        buffer = ByteBuffer.allocate(strBytes.length);
        buffer.put(strBytes);
        buffer.flip();
        channel.write(buffer);
        
        StringBuilder line = new StringBuilder(algoWiseVarCount[0].length * 2);
        for (int i = 0; i < algoWiseVarCount.length; i++)
        {
            line.setLength(0);
            for (int j = 0; j < algoWiseVarCount[i].length; j++)
            {
                line.append(algoWiseVarCount[i][j]).append("\t");
            }
            line.append("\n");

            strBytes = line.toString().getBytes();
            buffer = ByteBuffer.allocate(strBytes.length);
            buffer.put(strBytes);
            buffer.flip();
            channel.write(buffer);
        }

        stream.close();
        channel.close();
    }

    public static void main(String[] arg) throws Exception
    {
        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new EntropyScore());
        MSAProblem problem = new SATE_MSAProblem(instanceName, instancePath, scoreList);
        MapEncodedVARFileToAlgo selfobj = new MapEncodedVARFileToAlgo();
        List<String> singleVarPathList = SearchAndEncodeVAR_Linux.getPathList(searchRoot, singleVarFileName + "*");
        //Map<String, Map> algoNameToVarMap = new HashMap<>();
        Map<String, Integer> algoId = new HashMap<>();
        for (String path : singleVarPathList)
        {
            String[] pathSegments = path.split("/");
            String algoName = pathSegments[pathSegments.length - 3] + "/" + pathSegments[pathSegments.length - 2];
            //algoNameToVarMap.put(algoName, getEncodedVarWithCountFromFile(algoNameToVarMap.getOrDefault(algoName, new HashMap<>()), path, problem));
            algoId.put(algoName, algoId.getOrDefault(algoName, algoId.size()));
            Map<String, Integer> algoAlignmentToCount = selfobj.getEncodedVarWithCountFromFile(new HashMap<>(), path, problem);
            updateAlgoWiseAlignmentCount(algoId.get(algoName), algoAlignmentToCount, refVarFile, problem);
            algoAlignmentToCount = null;
        }
//        String[] algoNameArray = new String[algoNameToVarMap.size()];
//        int algoId = 0;
//        for (String algoName : algoNameToVarMap.keySet())
//        {
//            System.out.println(algoName + " => " + algoId);
//            algoNameArray[algoId++] = algoName;
//        }
        String header = "";
        for (Map.Entry<String, Integer> e : algoId.entrySet())
        {
            System.out.println(e.getKey() + "=>" + e.getValue());
            header += e.getKey() + '\t';

        }
        
        printAlgoWiseVarCountToFile(algoWiseVarCountFileName, header);

    }
}
