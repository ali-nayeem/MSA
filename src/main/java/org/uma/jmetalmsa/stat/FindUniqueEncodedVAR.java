/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.EntropyScore;
import org.uma.jmetalmsa.score.impl.GapConcentrationScore;
import org.uma.jmetalmsa.score.impl.NumberOfAlignedColumnsScore;
import org.uma.jmetalmsa.score.impl.SimilarityGapsScore;
import org.uma.jmetalmsa.score.impl.SimilarityNonGapsScore;
import org.uma.jmetalmsa.solution.MSASolution;
import static org.uma.jmetalmsa.stat.CalculateObjetivesFromVAR.varFilePath;
import static org.uma.jmetalmsa.stat.SearchAndEncodeVAR_Linux.instanceName;

/**
 *
 * @author Nayeem
 */
public class FindUniqueEncodedVAR
{

    static int approxLineLength = 1200;
    static String instancePath = "dataset/100S";
    static String instanceName = "R19";
    static String root = "SOP-AGP_SOP-AGP/";
    static String encodedVarFileName = "/home/ali_nayeem/data/" + root + instanceName +"/combinedVAR";
    static String uniqueVarFileName = "/home/ali_nayeem/data/" + root  +"/uniqueCombined"+"_"+instanceName;
    //static String shuffledVarFileName = "/home/ali_nayeem/MSA/experiment/shuffledVAR";

    public Set<String> getUniqueEncodedVarFile(String encodedVarFilePath, MSAProblem problem) throws Exception
    {
        int allCount = 0;
        BufferedReader br = new BufferedReader(new FileReader(encodedVarFilePath));
        Set<String> uniqueAlignments = new LinkedHashSet<>();

        while (br.ready())
        {
            allCount++;
            String oneLine = "";
            StringBuilder encodedAlignment = new StringBuilder(problem.getNumberOfVariables() * approxLineLength);
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
            String encodedAlignmentStr = encodedAlignment.toString();
            
            if (uniqueAlignments.contains(encodedAlignmentStr))
            {
                System.out.println(allCount);
            } 
            else
            {
                uniqueAlignments.add(encodedAlignmentStr);
            }
        }
        br.close();
        System.out.println("Total: " + allCount);
        System.out.println("Unique:" + uniqueAlignments.size());
        return uniqueAlignments;
    }

    public void printUniqueEncodedVarToFile(Set<String> allVar, String filepath) throws IOException
    {
        FileOutputStream stream = new FileOutputStream(filepath);
        FileChannel channel = stream.getChannel();
        byte[] strBytes;
        ByteBuffer buffer;

        for (String var : allVar)
        {
            strBytes = (var + "\n").getBytes();
            buffer = ByteBuffer.allocate(strBytes.length);
            buffer.put(strBytes);
            buffer.flip();
            channel.write(buffer);

        }

        stream.close();
        channel.close();
    }

    public void printUniqueEncodedVarToFile(List<String> allVar, String filepath) throws IOException
    {
        FileOutputStream stream = new FileOutputStream(filepath);
        FileChannel channel = stream.getChannel();
        byte[] strBytes;
        ByteBuffer buffer;

        for (String var : allVar)
        {
            strBytes = (var + "\n").getBytes();
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
        //scoreList.add(new NumberOfAlignedColumnsScore());
        //scoreList.add(new NumberOfGapsScore());
        //scoreList.add(new SimilarityGapsScore());
        //scoreList.add(new SimilarityNonGapsScore());
        //scoreList.add(new GapConcentrationScore());

        MSAProblem problem = new SATE_MSAProblem(instanceName, instancePath, scoreList);
        FindUniqueEncodedVAR selfObj = new FindUniqueEncodedVAR();
        Set<String> uniqueVar = selfObj.getUniqueEncodedVarFile(encodedVarFileName, problem);
        selfObj.printUniqueEncodedVarToFile(uniqueVar, uniqueVarFileName);

        //List<String> listOfVar = new ArrayList<>(uniqueVar);
        //Collections.shuffle(listOfVar);
        //selfObj.printUniqueEncodedVarToFile(listOfVar, shuffledVarFileName);

        //CalculateObjetivesFromVAR obj = new CalculateObjetivesFromVAR();
        //ob.printPopulationToEncodedvarFile(pop, uniqueVarFileName);
        //List <MSASolution> pop = obj.createPopulationFromEncodedVarFile(uniqueVarFileName, problem);
        //obj.evaluatePopulationToFile(pop, "FUN." + problem.getName() + ".unique", 1, problem);
    }

}
