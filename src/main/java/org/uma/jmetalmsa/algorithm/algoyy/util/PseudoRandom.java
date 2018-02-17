/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.algorithm.algoyy.util;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author Nayeem
 */
public class PseudoRandom
{

    public static int randInt(int low, int up)
    {
        return JMetalRandom.getInstance().nextInt(low, up);
    }

    public static double randNormal(double mean, double standardDeviation)
    {
        double x1, x2, w, y1;

        do
        {
            x1 = 2.0 * JMetalRandom.getInstance().nextDouble() - 1.0;
            x2 = 2.0 * JMetalRandom.getInstance().nextDouble() - 1.0;
            w = x1 * x1 + x2 * x2;
        } while (w >= 1.0);

        w = Math.sqrt((-2.0 * Math.log(w)) / w);
        y1 = x1 * w;
        y1 = y1 * standardDeviation + mean;
        return y1;
    }
}
