/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.stat;

/**
 *
 * @author Nayeem
 */
public class NormalizedVAR
{
    public int id;
    public double Entropy;
    public double TC;
    public double Gap;
    public double SimG;
    public double SimNG;
    public double GapCon;

    public NormalizedVAR(int id, String line)
    {
        this.id = id;
        String[] objList = line.split("\t");
        Entropy = Double.parseDouble(objList[0]);
        TC = Double.parseDouble(objList[1]);
        Gap = Double.parseDouble(objList[2]);
        SimG = Double.parseDouble(objList[3]);
        SimNG = Double.parseDouble(objList[3]);
        GapCon = Double.parseDouble(objList[4]);
    }
    
    
}
