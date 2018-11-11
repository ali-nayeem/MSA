package org.uma.jmetalmsa.score.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.uma.jmetalmsa.problem.DNA_MSAProblem;
import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.util.distancematrix.DistanceMatrix;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.score.Score;
import org.uma.jmetalmsa.solution.util.ArrayChar;
import org.uma.jmetalmsa.util.distancematrix.impl.NUC44_V1;

public class SumOfPairsScore implements Score {
  private DistanceMatrix distanceMatrix;

  public SumOfPairsScore(DistanceMatrix distanceMatrix) {
    this.distanceMatrix = distanceMatrix;
  }

  @Override
  public <S extends MSASolution> double compute(S solution,char [][]decodedSequences) {
    double sumOfPairs = 0;

    int numberOfVariables = decodedSequences.length;
    int lengthSequences = decodedSequences[0].length;

    for (int i = 0; i < (numberOfVariables - 1); i++) {
      for (int j = i + 1; j < numberOfVariables; j++) {
        for (int k = 0; k < lengthSequences; k++) {
          sumOfPairs += distanceMatrix.getDistance(decodedSequences[i][k], decodedSequences[j][k]);
        }
      }
    }

    return sumOfPairs;
  }

  @Override
  public boolean isAMinimizationScore() {
    return false;
  }

  @Override
  public String getName() {
    return "SOP";
  }

  @Override
  public String getDescription() {
    return "Sum of pairs";
  }
  public static void main (String arg[]) //throws IOException, CompoundNotFoundException
  {
//      String[] dummy = new String[1];
//      dummy[0] = "dataset/100S/R/input.fasta_fsa";
//      arg = dummy;
      if (arg.length < 1)
      {
          System.out.println("Please provide the input alignment as FASTA. Exiting !!");
          //System.exit(0);
          return;
      }
      List<Score> scoreList = new ArrayList<>();
      Score sop = new SumOfPairsScore(new NUC44_V1());
      scoreList.add(sop);
      //MSAProblem problem = new MSAProblem(scoreList);

      DNA_MSAProblem problem = null; 
      try
      {
          problem = new DNA_MSAProblem(scoreList, arg[0]); //"example/MAN/demo_align.txt");
      } catch (Exception ex)
      {
          Logger.getLogger(SumOfPairsScore.class.getName()).log(Level.SEVERE, null, ex);
          System.out.println("Alignment file not found. Exiting !!");
          return;
      }
      //List<ArrayChar> strAlignment = problem.readDataFromFastaFile("example/MAN/demo_align.txt");
      //problem.originalSequences = strAlignment;
      //problem.
      MSASolution sol = new MSASolution(problem.alignedSeq, problem);
      //sol.setSizeOfOriginalSequences(problem.originalSequences);
      System.out.println(sop.compute(sol, sol.decodeToMatrix()));
  }
}
