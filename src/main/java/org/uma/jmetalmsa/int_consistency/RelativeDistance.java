/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.int_consistency;

import com.aparapi.Kernel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.problem.SATE_MSAProblem;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.score.impl.EntropyScore;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.stat.CalculateObjetivesFromVAR;
//import org.uma.jmetalmsa.solution.MSASolution;

/**
 *
 * @author Nayeem
 */
public class RelativeDistance
{

    int refId;
    double[] dist;
    List<Neighbor> neighborList;
    double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
    int[] rank;

    public RelativeDistance(int refId, int size)
    {
        this.refId = refId;
        dist = new double[size];
    }

    public void generateRelativeDist(byte[][] msa, PairwiseDistance getDist)
    {
        for (int i = 0; i < dist.length; i++)
        {
            if (i == refId)
            {
                dist[i] = 0;
            } else
            {
                dist[i] = getDist.getDistance(msa[refId], msa[i]);
                if (dist[i] < min)
                {
                    min = dist[i];
                }

                if (dist[i] > max)
                {
                    max = dist[i];
                }
            }
        }

        //normalize
        for (int i = 0; i < dist.length; i++)
        {
            double del = max - min;
//            if(i == refId)  
//            {
//                dist[i] = -1;
//            }
            //else
            //{
            dist[i] = (dist[i] - min) / del;
            // }
        }

    }

    public List<Neighbor> calculateSortedNeighbor()
    {
        if (dist == null)
        {
            throw new JMetalException("You need to calculate dist first!!!");
        }
        neighborList = new ArrayList<>();
        for (int i = 0; i < dist.length; i++)
        {
            if (i == refId)
            {
                continue;
            }
            neighborList.add(new Neighbor(i, dist[i]));
        }
        Collections.sort(neighborList);
        //neighborList.remove(0);
        rank = new int[dist.length];

        for (int i = 0; i < neighborList.size(); i++)
        {
            rank[neighborList.get(i).id] = i;
        }
        rank[refId] = -1;
        return neighborList;
    }

    double calculateCloseness1(int[] closeNeigborIndices)
    {
        double sum = 0;
        double min = dist[closeNeigborIndices[0]];
        for (int i = 1; i < closeNeigborIndices.length; i++)
        {
            if (dist[closeNeigborIndices[i]] < min)
            {
                min = dist[closeNeigborIndices[i]];
            }

        }

        for (int i = 0; i < closeNeigborIndices.length; i++)
        {
            sum += (dist[closeNeigborIndices[i]] - min);
        }

        return sum;
    }

    class Neighbor implements Comparable<Neighbor>
    {

        int id;
        double dist;

        public Neighbor(int id, double dist)
        {
            this.id = id;
            this.dist = dist;
        }

        @Override
        public String toString()
        {
            return "Neighbor{" + "id=" + id + ", dist=" + dist + '}';
        }

        @Override
        public int compareTo(Neighbor o)
        {
            return Double.compare(o.dist, this.dist); //Double.compare(this.dist, o.dist);
        }

    }

    public static void main(String[] arg) throws Exception
    {
//        String instancePath = "dataset/100S";
//        String instanceName = "R0";
//        String inputFilePath = "F:\\Phd@CSE,BUET\\Com. Biology\\MSA\\Dataset\\scripts\\input\\NumGaps_SOP\\precomputedInit\\uniqueCombined_R0Small";
//        List<Score> scoreList = new ArrayList<>();
//        scoreList.add(new EntropyScore());
//        MSAProblem problem = new SATE_MSAProblem(instanceName, instancePath, scoreList);
//        //numOfSeq = problem.getNumberOfVariables();
//        CalculateObjetivesFromVAR ob = new CalculateObjetivesFromVAR();
//        List<MSASolution> pop = ob.createPopulationFromEncodedVarFile(inputFilePath, problem);
//        char[][] msa = pop.get(4).decodeToMatrix();
//        RelativeDistance self = new RelativeDistance(5, pop.get(0).getMSAProblem().getNumberOfVariables());
//        self.generateRelativeDist(msa, new PairwiseDistance());
//        List<Neighbor> neighborList = self.calculateSortedNeighbor();
//        System.out.println(neighborList.get(3));
    }

    double calculateCloseness2(int[] closeNeigborIndices)
    {
        double sum = 0;

        calculateSortedNeighbor();

        double min = dist[closeNeigborIndices[0]];
        double max = dist[closeNeigborIndices[0]];
        for (int i = 1; i < closeNeigborIndices.length; i++)
        {
            if (dist[closeNeigborIndices[i]] < min)
            {
                min = dist[closeNeigborIndices[i]];
            }
            if (dist[closeNeigborIndices[i]] > max)
            {
                max = dist[closeNeigborIndices[i]];
            }
        }
        return max - min;
    }

    double calculateCloseness3(int[] closeNeigborIndices)
    {
        double sum = 0;
        calculateSortedNeighbor();

        double min = rank[closeNeigborIndices[0]];
        double max = rank[closeNeigborIndices[0]];
        for (int i = 1; i < closeNeigborIndices.length; i++)
        {
            if (rank[closeNeigborIndices[i]] < min)
            {
                min = rank[closeNeigborIndices[i]];
            }
            if (rank[closeNeigborIndices[i]] > max)
            {
                max = rank[closeNeigborIndices[i]];
            }
        }

        if ((max - min) == (closeNeigborIndices.length - 1))
        {
            return 1;
        } else
        {
            return 0;
        }
    }

    double calculateFarness1(int[] twoEnds)
    {
        return Math.abs(dist[twoEnds[0]] - dist[twoEnds[1]]);
    }

    public void generateRelativeDistGPU(byte[][] msa, byte[][] countResult)
    {
        int N = msa[0].length;
        //byte[][] countResult = new byte[msa.length][N];
        //Device device = Device.firstGPU();
        //Range range = device.createRange(N * msa.length);
        Kernel kernel = new PairwiseDist2D(msa, dist, N, refId);
        //kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
        //kernel.setAutoCleanUpArrays(true);
        kernel.execute(N * msa.length);
        kernel.dispose();

        //final Kernel kernel2 = new Summary(countResult, dist);
        //kernel2.execute(dist.length);
        for (int i = 0; i < dist.length; i++)
        {
//            for (int j = 0; j < msa[i].length; j++)
//            {
//                dist[i] += countResult[i][j];
//            }
            if (dist[i] < min)
            {
                min = dist[i];
            }

            if (dist[i] > max)
            {
                max = dist[i];
            }
        }
        double del = max - min;
        for (int i = 0; i < dist.length; i++)
        {
            dist[i] = (dist[i] - min) / del;
          
        }
    }

}

class PairwiseDist2D extends Kernel
{

    byte[][] A;

    //byte[][] B;
    double[] D;
    byte[][] C;

    int N;
    int R;

    public PairwiseDist2D(byte[][] A, double[] d, int N, int R)
    {
        this.A = A;
        //this.B = B;
        this.D = d;
        this.C = new byte[A.length][A[0].length];
        this.N = N;
        this.R = R;
        //this.setExplicit(true);
//        if (R == 0)
//        {
//            this.put(A);
//        }

    }

    public PairwiseDist2D(byte[][] A, int N, int R)
    {
        this.A = A;
        this.C = new byte[A.length][A[0].length];
        this.N = N;
        this.R = R;
    }

    @Override
    public void run()
    {
        int id = getGlobalId();
        int i = id / N;
        int j = id % N;
        C[i][j] = (byte) ((A[R][j] == A[i][j]) ? 1 : 0);
        localBarrier();
        if (id < D.length)
        {
            for (int k = 0; k < C[id].length; k++)
            {
                D[id] += C[id][k];
            }
        }

//      for (int k = 0; k < N; k++) {
//         C[i][j] += (byte) (A[i][k] * B[k][j]);
//      }
    }
}

class Summary extends Kernel
{

    byte[][] C;
    double[] dist;

    public Summary(byte[][] C, double[] dist)
    {
        this.C = C;
        this.dist = dist;
    }

    @Override
    public void run()
    {
        int id = getGlobalId();
        for (int i = 0; i < C[id].length; i++)
        {
            dist[id] += C[id][i];
        }
    }

}
