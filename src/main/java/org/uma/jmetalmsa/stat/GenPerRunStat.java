/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.uma.jmetalmsa.stat.FindUniqueEncodedVAR.root;

/**
 *
 * @author ali_nayeem
 */
public class GenPerRunStat {
    static String instanceName[] = {"R0", "R4", "R9", "R14", "R19"} ;
    static String obj = "SOP-AGP_SOP-AGP/";
    static String inDir = "/home/ali_nayeem/data/";
    static String outDir = "/home/ali_nayeem/data/stat/";
    static String uniqueVAR = "uniqueCombined";
    static String treePerf = "tree_perf";
    static int runCount = 20;
    
    public static void GenerateRunWiseTreePerfFile() throws Exception
    {
        for (int ins = 0; ins < instanceName.length; ins++) {
            List<AnalyzeVAR.PerfWithIndex> runWiseMinError = new ArrayList<>();
            //Map<String, Integer> VAR2IndexMap = ReadCombinedVAR.populateVARIndex(inDir+ obj + instanceName[ins] + "/" + uniqueVAR); 
            Map<String, Integer> VAR2IndexMap = ReadCombinedVAR.populateVARIndex(inDir+ obj + uniqueVAR + "_" + instanceName[ins] ); 
            List<ReadTreePerf.Error> CombinedPerfList = ReadTreePerf.populatePerfArray(inDir+ obj + instanceName[ins] + "_"+treePerf);
            for (int i = 0; i < runCount; i++) {
                List <AnalyzeVAR.PerfWithIndex> PerfWithIndexList = AnalyzeVAR.processVAR(VAR2IndexMap, CombinedPerfList, inDir+ obj + instanceName[ins] + "/" + instanceName[ins] + "_"+obj + "VAR" + i + ".tsv");
                AnalyzeVAR.writePerfWithIndexToFile(outDir+obj + instanceName[ins] + "/treePerf"+i, PerfWithIndexList);
                runWiseMinError.add(Collections.min(PerfWithIndexList));
            }
            AnalyzeVAR.writePerfWithIndexToFile(outDir+obj + instanceName[ins] + "_bestPerf", runWiseMinError);
        }
        
        //String VARPath;
    }
    
     public static void main(String[] arg) throws Exception {
         GenerateRunWiseTreePerfFile();
         
     }
    
}
