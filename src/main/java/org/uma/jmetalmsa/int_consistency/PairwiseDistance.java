/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.int_consistency;

/**
 *
 * @author Nayeem
 */
public class PairwiseDistance
{

    public double getDistance(char[] A, char[] B)
    {
        double distance = 0;
        for (int j = 0; j < A.length; j++)
        {
            if (A[j] != B[j])
            {
                distance++;
            }
        }
        return distance;
    }
}

class transversion extends PairwiseDistance
{

    @Override
    public double getDistance(char[] A, char[] B)
    {
        double distance = 0;
        for (int j = 0; j < A.length; j++)
        {
            if (A[j] != B[j])
            {
                if ((A[j] + B[j]) == ('A' + 'G') || (A[j] + B[j]) == ('C' + 'T'))
                {
                    distance++;
                } else if (A[j] == '-' || B[j] == '-')
                {
                    distance++;
                } else
                {
                    distance += 2;
                }

            }
        }
        return distance;
    }
}

class Similarity extends PairwiseDistance
{

    @Override
    public double getDistance(char[] A, char[] B)
    {
        double distance = 0;
        for (int j = 0; j < A.length; j++)
        {
            if (A[j] == B[j])
            {
                distance++;
            }
        }
        return distance;
    }
}
