/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
//import static org.uma.jmetalmsa.stat.FindUniqueEncodedVAR.root;
//import static org.uma.jmetalmsa.stat.FindUniqueEncodedVAR_Balibase.dir;

/**
 *
 * @author ali_nayeem
 */
public class GenPerRunStat_MSA {
    //static String instanceName[] = {"BB40001"} ; //"R0", "R4", "R9", "R14","R19" "23S.E", "23S.E.aa_ag"
    static String set = "SimG_SimNG/";
    static String inDir = "/media/ali_nayeem/secondary/UBUNTU/precomputedInit/";//"/home/ali_nayeem/data/"; ///home/ali_nayeem/data/biological
    static String outDir = "/media/ali_nayeem/secondary/UBUNTU/precomputedInit/stat/";
    static String uniqueVAR = "uniqueCombined";
    static String treePerf = "msa_perf";
    static int runCount = 20;
    
    public static void GenerateRunWiseTreePerfFile() throws Exception
    {
        //File f = new File(inDir+set);
        ArrayList<String> names = new ArrayList<String>(
                                        Arrays.asList("R0","R4","R9","R14","R19", "R2","R6","R10","R13","R17")); //"23S.E", "23S.E.aa_ag"
        //ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
     
        for(String instanceName : names)
        {
//            if (!instanceName.matches("BB.....") ) 
//             {
//                 continue;
//             }
            List<AnalyzeVAR.PerfWithIndex> runWiseMinError = new ArrayList<>();
            //Map<String, Integer> VAR2IndexMap = ReadCombinedVAR.populateVARIndex(inDir+ obj + instanceName[ins] + "/" + uniqueVAR); 
            Map<String, Integer> VAR2IndexMap = ReadCombinedVAR.populateVARIndex(inDir+ set + uniqueVAR + "_" + instanceName ); 
            List<ReadTreePerf.Error> CombinedPerfList = ReadMsaPerf.populatePerfArray(inDir+ set + instanceName + "_SP_1");
            File directory = new File(String.valueOf(outDir + set + instanceName));
            if(!directory.exists()){
                directory.mkdir();
            }
            for (int i = 0; i < runCount; i++) {
                List <AnalyzeVAR.PerfWithIndex> PerfWithIndexList = AnalyzeVAR.processVAR(VAR2IndexMap, CombinedPerfList, inDir+ set + instanceName + "/" +instanceName + "_" + set  + "VAR" + i + ".tsv");
                //AnalyzeVAR.writePerfWithIndexToFile(outDir+obj + instanceName[ins] + "/treePerf"+i, PerfWithIndexList);
                //Collections.sort(PerfWithIndexList);
                AnalyzeVAR.writePerfWithIndexToFile(outDir + set + instanceName + "/msaPerf"+i, PerfWithIndexList);
                //runWiseMinError.add(Collections.min(PerfWithIndexList));
                //System.out.print(i+" ");
            }
            //AnalyzeVAR.writePerfWithIndexToFile(outDir + instanceName + "_bestMsaPerf", runWiseMinError);
        }
        
        //String VARPath;
    }
    
     public static void main(String[] arg) throws Exception {
         GenerateRunWiseTreePerfFile();
         
     }
    
}
