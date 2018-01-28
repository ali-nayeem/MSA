/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.uma.jmetalmsa.stat.MapEncodedVARFileToAlgo.algoWiseVarCount;
//import org.uma.jmetal.util.fileoutput.SolutionListOutput;

/**
 *
 * @author ali_nayeem
 */
public class AnalyzeVAR {
    //List<ReadTreePerf.Error> CombinedPerfList;
    //Map<String, Integer> VAR2IndexMap;
    //String VARPath;
    static int numOfLinesInVAR = 100;
    //List <PerfWithIndex> PerfWithIndexList = new ArrayList<>();

//    public AnalyzeVAR(Map<String, Integer> VAR2IndexMap, List<ReadTreePerf.Error> CombinedPerfList, String VARPath) {
//        this.CombinedPerfList = CombinedPerfList;
//        this.VAR2IndexMap = VAR2IndexMap;
//        this.VARPath = VARPath;
//    }
    
    public static List<PerfWithIndex> processVAR(Map<String, Integer> VAR2IndexMap, List<ReadTreePerf.Error> CombinedPerfList, String VARPath) throws Exception
    {
        List <PerfWithIndex> PerfWithIndexList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(VARPath));
        //Map<String, Integer> AlignmentToCount = new HashMap<>();
        StringBuilder encodedAlignment = new StringBuilder(numOfLinesInVAR * 1000);

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
            int VARIndex = VAR2IndexMap.get(encodedAlignmentString);
            PerfWithIndexList.add(new PerfWithIndex(CombinedPerfList.get(VARIndex), VARIndex));
            //VAR2IndexMap.put(encodedAlignmentString, VAR_Count++);
            //VAR_Count++;
        }
        br.close();
        return PerfWithIndexList;
    }
    
    public static void writePerfWithIndexToFile(String fileName, List PerfWithIndexList) throws Exception
    {
        FileOutputStream stream = new FileOutputStream(fileName);
        FileChannel channel = stream.getChannel();
        byte[] strBytes;
        ByteBuffer buffer;
        
//        strBytes = ("FP rate, FN rate, Robinson-Foulds distance\n").getBytes();
//        buffer = ByteBuffer.allocate(strBytes.length);
//        buffer.put(strBytes);
//        buffer.flip();
//        channel.write(buffer);
        
        StringBuilder line = new StringBuilder(PerfWithIndexList.size() * 50);
        //line.setLength(0);
        line.append("FP rate, FN rate, Robinson-Foulds distance, VARIndex\n"); //header
        for (int i = 0; i < PerfWithIndexList.size(); i++)
        {
            line.append(PerfWithIndexList.get(i)).append("\n");
        }
        
        strBytes = line.toString().getBytes();
        buffer = ByteBuffer.allocate(strBytes.length);
        buffer.put(strBytes);
        buffer.flip();
        channel.write(buffer);
        
        stream.close();
        channel.close();
    }
    public static class PerfWithIndex implements Comparable<PerfWithIndex>
    {
        ReadTreePerf.Error perf;
        int index;

        public PerfWithIndex(ReadTreePerf.Error perf, int index) {
            this.perf = perf;
            this.index = index;
        }

        @Override
        public int compareTo(PerfWithIndex o) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            return this.perf.compareTo(o.perf);
        }

        @Override
        public String toString() {
            return  perf + ", " + index;
        }
        
    }
    
    
    public static void main(String[] arg) throws Exception {
//        ReadCombinedVAR map =  new ReadCombinedVAR("/home/ali_nayeem/data/SimG_SimNG/R0/uniqueCombined");
//        map.populateVARIndex();
//        ReadTreePerf tp = new ReadTreePerf("/home/ali_nayeem/data/SimG_SimNG/R0_tree_perf");
//        tp.populatePerfArray();
//        AnalyzeVAR self = new AnalyzeVAR(map.VAR2IndexMap, tp.perfList, "/home/ali_nayeem/data/SimG_SimNG/R0/R0_SimG_SimNG/VAR0.tsv");
//        self.processVAR();
//        System.out.println(Collections.min(self.PerfWithIndexList));
//        System.out.println(Collections.max(self.PerfWithIndexList));
//        Collections.sort(self.PerfWithIndexList);
//        System.out.println("");
        

    }
}
