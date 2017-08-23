/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.EntropyScore;
import org.uma.jmetalmsa.score.impl.GapConcentrationScore;
import org.uma.jmetalmsa.score.impl.NumberOfAlignedColumnsScore;
import org.uma.jmetalmsa.score.impl.SimilarityGapsScore;
import org.uma.jmetalmsa.score.impl.SimilarityNonGapsScore;
import static org.uma.jmetalmsa.stat.CalculateObjetivesFromVAR.instance;

/**
 *
 * @author Nayeem
 */
public class SearchAndEncodeVAR_Linux
{

    static String root = "/home/ali_nayeem/data/";
    static String varFileName = "VAR";
    static String pattern = "VAR";
    static String instancePath = "dataset/100S";
    static String instanceName = "R0";

    public List<String> getPathListOfDirs(String rootPath) throws Exception
    {
        List<String> dirPathList = new ArrayList<>();
        String fileNameSuffix = "0.tsv";
        String command = "find " + root + "-type f -name " + varFileName + fileNameSuffix;
        BufferedReader reader = runBashCommand(command);
        
        String line = "";
        while ((line = reader.readLine()) != null)
        {
            line = line.substring(0, line.length() - fileNameSuffix.length());
            dirPathList.add(line);
        }

        return dirPathList;
    }

    public List<String> getPathListOfVarFiles()
    {
        List<String> varpathList = new ArrayList<>();

        return varpathList;
    }

    public BufferedReader runBashCommand(String command) throws Exception
    {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return reader;
    }

    public static void main(String[] arg) throws Exception
    {
        List<Score> scoreList = new ArrayList<>();
        scoreList.add(new EntropyScore());
        scoreList.add(new NumberOfAlignedColumnsScore());
        //scoreList.add(new NumberOfGapsScore());
        scoreList.add(new SimilarityGapsScore());
        scoreList.add(new SimilarityNonGapsScore());
        scoreList.add(new GapConcentrationScore());

        MSAProblem problem = new SATE_MSAProblem(instanceName, instancePath, scoreList);
    }
}
