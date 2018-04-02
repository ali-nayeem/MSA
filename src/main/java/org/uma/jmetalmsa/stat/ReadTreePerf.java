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
public class ReadTreePerf {

    //private String filePath = "";

    List<Error> perfList = new ArrayList<>();

//    public ReadTreePerf(String file) {
//        filePath = file;
//        
//    }

    public static class Error implements Comparable<Error> {

        double FP;
        double FN;
        double RF=-1;

        public Error(double FP, double FN, double RF) {
            this.FP = FP;
            this.FN = FN;
            this.RF = RF;
        }
        
        public Error(String []num) {
            this.FP = Double.parseDouble(num[0]);
            this.FN = Double.parseDouble(num[1]);
            //this.RF = RF;
        }

        @Override
        public String toString() {
            return  FP + ", " + FN + ", " + RF;
        }

        @Override
        public int compareTo(Error o) {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            return Double.compare(this.FN, o.FN);
        }

    }

    public static List<Error> populatePerfArray(String filePath) throws Exception {
        List<Error> perfList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        br.readLine(); //skip header

        while (br.ready()) {
            String oneLine = br.readLine();
            String[] FP_FN_RF = oneLine.split(", ");
            perfList.add(new Error(Double.parseDouble(FP_FN_RF[0]), Double.parseDouble(FP_FN_RF[1]), Double.parseDouble(FP_FN_RF[2])));
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
