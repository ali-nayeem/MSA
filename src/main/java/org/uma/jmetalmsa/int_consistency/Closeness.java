/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.int_consistency;

import org.uma.jmetalmsa.int_consistency.RelativeDistance.Neighbor;

/**
 *
 * @author Nayeem
 */
public class Closeness
{
    CalculateIntConsistency stuff;
    int numOfNeighbor;

    public Closeness(CalculateIntConsistency stuff, int numOfNeighbor)
    {
        this.stuff = stuff;
        this.numOfNeighbor = numOfNeighbor;
    }

//    Closeness(CalculateIntConsistency aThis, int i)
//    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
    void gatherNeighbors(int s, int[] closeNeigborIndices)
    {
        for (int i = 0; i < closeNeigborIndices.length; i++)
        {
            closeNeigborIndices[i] = stuff.refTaxaNeighbors.get(s + i).id;
        }
    }
    double calculateCloseness(int[] closeNeigborIndices)
    {
        double sum = 0;
        int count=0;
        for (Neighbor n : stuff.refTaxaNeighbors) // (int i = 1; i < stuff.taxaCount; i++)
        {
            int i = n.id;
            boolean match = false;
            for (int j : closeNeigborIndices)
            {
                if (i == j)
                {
                    match = true;
                    break;
                }
            }
            if (match)
            {
                continue;
            }
            sum += stuff.relDistArray[i].calculateCloseness2(closeNeigborIndices);
            count++;
            if (count == stuff.refTaxaNeighbors.size()/4)
            {
                break;
            }
        }
        return sum; ///(stuff.taxaCount - closeNeigborIndices.length - 1);
    }
    
    double calculate()
    {
        double result = 0;
        int endIndex = stuff.refTaxaNeighbors.size() - (numOfNeighbor-1);
        int[] closeNeigborIndices = new int[numOfNeighbor];
        for (int i = 0; i < endIndex; i++) //i+=numOfNeighbor
        {
            gatherNeighbors(i, closeNeigborIndices);
            result += calculateCloseness(closeNeigborIndices);
        }
                
        return result;
    }
    
}
