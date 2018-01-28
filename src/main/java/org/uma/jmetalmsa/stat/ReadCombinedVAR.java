/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
//import org.uma.jmetalmsa.problem.MSAProblem;

/**
 *
 * @author ali_nayeem
 */
public class ReadCombinedVAR {
    //private String filePath = "";
    static private int numOfLinesInVAR = 100;
    
//    public ReadCombinedVAR(String filePath) {
//        this.filePath = filePath;
//    }

    
    static public Map<String, Integer> populateVARIndex(String filePath) throws Exception
    {
        Map<String, Integer> VAR2IndexMap = new HashMap<>();
        int VAR_Count = 0;
        BufferedReader br = new BufferedReader(new FileReader(filePath));
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
            VAR2IndexMap.put(encodedAlignmentString, VAR_Count++);
            //VAR_Count++;
        }
        br.close();
        System.out.println("Total: " + VAR_Count);
        //System.out.println("Unique:" + alignmentToCount.size());
        return VAR2IndexMap;
       
    }
    
    public static void main(String[] arg) throws Exception
    {
        //ReadCombinedVAR self =  new ReadCombinedVAR("/home/ali_nayeem/data/SimG_SimNG/R4/uniqueCombined");
        //self.populateVARIndex();
        
    }
    
}
