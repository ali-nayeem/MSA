/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
//mport static java.util.Collections.list;
import java.util.List;

/**
 *
 * @author ali_nayeem
 */
public class ReadMsaPerf {

    //private String filePath = "";

    List<ReadTreePerf.Error> perfList = new ArrayList<>();

//    public ReadTreePerf(String file) {
//        filePath = file;
//        
//    }

    public static class PerfScore extends ReadTreePerf.Error {

        double[] score = new double[6];
        int criteria = 4;

        public PerfScore(double[] sc) {
            super(sc[0], sc[1], sc[2]);
            score = sc;
        }
        
        public PerfScore(String[] sc) {
            super(0, 0, 0);
            score[0] = Double.parseDouble(sc[0]);
            score[1] = Double.parseDouble(sc[1]);
        }
 

        @Override
        public String toString() { 
            return  score[0] + ", " + score[1] + ", " + score[2]+ ", " + score[3] + ", " + score[4]+ ", " + score[5];
        }

        //@Override
        public int compareTo(PerfScore o) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            return Double.compare(this.score[criteria], this.score[criteria]);
        }

    }

    public static List<ReadTreePerf.Error> populatePerfArray(String filePath) throws Exception {
        List<ReadTreePerf.Error> perfList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        br.readLine(); //skip header

        while (br.ready()) {
            String oneLine = br.readLine();
            String[] allScore = oneLine.split(" ");
            
            for (int i = 0; i < allScore.length / 6; i++) {
                double[] score = new double[6];
                for (int j = 0; j < 6; j++) {
                    score[j] = Double.parseDouble(allScore[6*i+j]);
                }
                perfList.add(new PerfScore(score));
            }
            //perfList.add(new PerfScore(Double.parseDouble(FP_FN_RF[0]), Double.parseDouble(FP_FN_RF[1]), Double.parseDouble(FP_FN_RF[2])));
            //perfList.add(new Error(FP_FN_RF));
            //System.out.println("");
        }
        return perfList;
    }
    
    public static List<ReadTreePerf.Error> populatePerfArray2(String filePath) throws Exception {
        List<ReadTreePerf.Error> perfList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        br.readLine(); //skip header

        while (br.ready()) {
            String oneLine = br.readLine();
            String[] allScore = oneLine.split(" ");
            perfList.add(new PerfScore(allScore));
            
            //perfList.add(new PerfScore(Double.parseDouble(FP_FN_RF[0]), Double.parseDouble(FP_FN_RF[1]), Double.parseDouble(FP_FN_RF[2])));
            //perfList.add(new Error(FP_FN_RF));
            //System.out.println("");
        }
        return perfList;
    }
    

    public static void main(String[] arg) throws Exception {
//        ReadTreePerf self = new ReadTreePerf("/home/ali_nayeem/data/SimG_SimNG/R19_tree_perf");
//        self.populatePerfArray();
//        System.out.println(Collections.min(self.perfList));
//        System.out.println(Collections.max(self.perfList));
//        Collections.sort(self.perfList);
//        System.out.println("");

    }

}
