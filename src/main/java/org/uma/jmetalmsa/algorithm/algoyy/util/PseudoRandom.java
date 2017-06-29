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
        return JMetalRandom.getInstance().nextInt(low,up);
    }
}
