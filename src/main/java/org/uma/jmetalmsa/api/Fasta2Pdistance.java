/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.api;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetalmsa.stat.pDistance;

/**
 *
 * @author ali_nayeem
 */
public class Fasta2Pdistance
{

    public static void main(String[] arg) throws Exception
    {
        if (arg.length < 1)
        {
            System.out.println("Please provide the full path of the input alignment in FASTA format. Exiting !!");
            //System.exit(0);
            return;
        }
        pDistance pDist = new pDistance();
        try
        {
           pDist.readRefAlignment(arg[0]); //"example/MAN/demo_align.txt");
           pDist.printAllPdistances();
        } catch (Exception ex)
        {
            Logger.getLogger(pDistance.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Alignment file not found. Exiting !!");
            return;
        }
        //Li

    }

}
