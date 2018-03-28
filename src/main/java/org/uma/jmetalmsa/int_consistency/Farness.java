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
public class Farness extends Closeness
{
    //CalculateIntConsistency stuff;
    //int numOfNeighbor;

    public Farness(CalculateIntConsistency stuff, int numOfNeighbor)
    {
        super(stuff, numOfNeighbor);
    }

//    Closeness(CalculateIntConsistency aThis, int i)
//    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
    void getTwoEnds( int[] twoEnds)
    {
        
        //for (int i = 0; i < twoEnds.length; i++)
        //{
            twoEnds[0] = stuff.refTaxaNeighbors.get(0).id;
            twoEnds[1] = stuff.refTaxaNeighbors.get(stuff.refTaxaNeighbors.size()-1).id;
      //  }
    }
    double calculateFarness(int[] twoEnds)
    {
        double sum = 0;
        int count=0;
        for (Neighbor n : stuff.refTaxaNeighbors) // (int i = 1; i < stuff.taxaCount; i++)
        {
            int i = n.id;
            boolean match = false;
            for (int j : twoEnds)
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
            sum += stuff.relDistArray[i].calculateFarness1(twoEnds);
            count++;
            if (count == stuff.refTaxaNeighbors.size()/4)
            {
                break;
            }
        }
        return sum; ///(stuff.taxaCount - closeNeigborIndices.length - 1);
    }
    
    @Override
    double calculate()
    {
        double result = 0;
        //int endIndex = stuff.refTaxaNeighbors.size() - (numOfNeighbor-1);
        int[] twoEnds = new int[2];
        getTwoEnds(twoEnds);
        result = calculateFarness(twoEnds);
        
                
        return result;
    }
    
}
