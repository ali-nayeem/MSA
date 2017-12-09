package org.uma.jmetalmsa.score.impl;

import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.util.distancematrix.DistanceMatrix;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.solution.util.ArrayChar;
import org.uma.jmetalmsa.util.MSADistance;

import java.util.List;
import org.uma.jmetalmsa.score.Score;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class WeightedSumOfPairsScore implements Score {
  public DistanceMatrix sustitutionMatrix;
  public MSADistance distanceMatrix;
  public double[][] weightMatrix;
  public double weightGapOpen = 6;
  public double weightGapExtend = 0.85;;
  double wSOP = Double.MIN_VALUE;

  public WeightedSumOfPairsScore(DistanceMatrix sustitutionMatrix, double weightGapOpen, double weightGapExtend) {
    this.sustitutionMatrix = sustitutionMatrix;
    distanceMatrix = new MSADistance();
    this.weightGapOpen = weightGapOpen;
    this.weightGapExtend = weightGapExtend;
  }
  
  public WeightedSumOfPairsScore(DistanceMatrix sustitutionMatrix) {
    this.sustitutionMatrix = sustitutionMatrix;
    distanceMatrix = new MSADistance();
    //this.weightGapOpen = weightGapOpen;
    //this.weightGapExtend = weightGapExtend;
  }

  public void initializeWeightMatrix(List<ArrayChar> originalSequences) {
    weightMatrix = getWMatrix(originalSequences);
  }

  @Override
  public <S extends MSASolution> double compute(S solution,char [][]decodedSequences) {
    initializeWeightMatrix(solution.getOriginalSequences());  
    int lengthSequences = solution.getAlignmentLength();
    int numberOfVariables = solution.getNumberOfVariables();

    double sumOfPairs = 0;
    double sumOfPairsOfLthColumn;
    int i, j;

    for (int l = 0; l < lengthSequences; l++) {
      sumOfPairsOfLthColumn = 0;

      for (i = 0; i < numberOfVariables - 1; i++) {
        for (j = i + 1; j < numberOfVariables; j++)
          sumOfPairsOfLthColumn += weightMatrix[i][j] * sustitutionMatrix.getDistance(decodedSequences[i][l], decodedSequences[j][l]);
      }

      sumOfPairs += sumOfPairsOfLthColumn;
    }

    double affineGapPenaltyScore = 0;
    for (i = 0; i < numberOfVariables; i++) {
      affineGapPenaltyScore += getAffineGapPenalty(solution.getVariableValue(i));
    }

    return sumOfPairs - affineGapPenaltyScore;

  }

  public double getAffineGapPenalty(List<Integer> gapsGroups) {
    int weightToOpenTheGap = 0, weightToExtendTheGap = 0;

    weightToOpenTheGap = gapsGroups.size() / 2;

    for (int i = 0; i < gapsGroups.size(); i += 2) {
      weightToExtendTheGap += gapsGroups.get(i + 1) - gapsGroups.get(i);
    }

    return (weightGapOpen * weightToOpenTheGap) + (weightGapExtend * weightToExtendTheGap);
  }

  public double[][] getWMatrix(List<ArrayChar> Seqs) {
    int NumSeqs = Seqs.size();
    char[] Si;
    char[] Sj;

    double[][] wMatrix = new double[NumSeqs - 1][];

    for (int i = 0; i < NumSeqs - 1; ++i) {
      wMatrix[i] = new double[NumSeqs];
      for (int j = i + 1; j < NumSeqs; ++j) {
        Si = Seqs.get(i).getCharArray();
        Sj = Seqs.get(j).getCharArray();

        wMatrix[i][j] = 1 - (distanceMatrix.getLevenshteinDistance(Si, Sj) / (Si.length > Sj.length ? Si.length : Sj.length));
      }
    }

    return wMatrix;
  }

  public void printWMatrix(double[][] W) {

    for (int i = 0; i < W.length; ++i) {
      for (int j = 0; j <= W.length; ++j) {
        System.out.printf("%.4f\t", W[i][j]);
      }
      System.out.println();
    }

  }


  @Override
  public boolean isAMinimizationScore() {
    return false;
  }

  @Override
  public String getName() {
    return "wSOP";
  }

  @Override
  public String getDescription() {
    return "Weighted sum of pairs";
  }
}
