/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.util;

/**
 *
 * @author Nayeem
 */
import java.security.SecureRandom;
import java.util.Random;
import org.apache.commons.math3.random.MersenneTwister;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

public class SynchronizedMersenneTwister extends Random implements PseudoRandomGenerator
{

    private static final long serialVersionUID = -4586969514356530381L;

    private static Random SEEDER;

    private static SynchronizedMersenneTwister INSTANCE;

    private static ThreadLocal<MersenneTwister> LOCAL_RANDOM;
  
    private static final String name = "SynchronizedMersenneTwister" ;

    static
    {
        SEEDER = new SecureRandom();

        LOCAL_RANDOM = new ThreadLocal<MersenneTwister>()
        {

            @Override
            protected MersenneTwister initialValue()
            {
                synchronized (SEEDER)
                {
                    return new MersenneTwister(SEEDER.nextLong());
                }
            }

        };

        INSTANCE = new SynchronizedMersenneTwister();
    }

    private SynchronizedMersenneTwister()
    {
        super();
    }
    
    public static SynchronizedMersenneTwister getInstance()
    {
        return INSTANCE;
    }

    private MersenneTwister current()
    {
        return LOCAL_RANDOM.get();
    }

    public synchronized void setSeed(long seed)
    {
        current().setSeed(seed);
    }

    public void nextBytes(byte[] bytes)
    {
        current().nextBytes(bytes);
    }

    @Override
    public int nextInt()
    {
        return current().nextInt();
    }

    @Override
    public int nextInt(int n)
    {
        return current().nextInt(n);
    }

    public long nextLong()
    {
        return current().nextLong();
    }

    public boolean nextBoolean()
    {
        return current().nextBoolean();
    }

    public float nextFloat()
    {
        return current().nextFloat();
    }

    @Override
    public double nextDouble()
    {
        return current().nextDouble();
    }

    @Override
    public synchronized double nextGaussian()
    {
        return current().nextGaussian();
    }

    @Override
    public int nextInt(int lowerBound, int upperBound)
    {
        return lowerBound + current().nextInt((upperBound - lowerBound) + 1);
    }

   @Override
  public double nextDouble(double lowerBound, double upperBound) {
    return lowerBound + current().nextDouble()*(upperBound - lowerBound) ;
  }

    @Override
    public long getSeed()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName()
    {
        return name;
    }
    
    public synchronized double nextGaussian(double mean, double standardDeviation)
    {
        return nextGaussian() * standardDeviation + mean;
    }

}
