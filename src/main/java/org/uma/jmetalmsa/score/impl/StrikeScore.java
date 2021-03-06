package org.uma.jmetalmsa.score.impl;

import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.strike.Strike;

import java.util.List;
import org.uma.jmetalmsa.score.Score;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StrikeScore implements Score {
  private Strike strike ;
  private List<StringBuilder> listOfSequenceNames ;

   public void initializeParameters(String pdbPath, List<StringBuilder> listOfSequenceNames) {
    strike = new Strike(pdbPath, listOfSequenceNames) ;
    this.listOfSequenceNames = listOfSequenceNames ;
  }

  @Override
  public <S extends MSASolution> double compute(S solution, char [][]decodedSequences) {
    return strike.compute(decodedSequences, listOfSequenceNames, false) ;
  }

  @Override
  public boolean isAMinimizationScore() {
    return false;
  }

  @Override
  public String getName() {
    return "Strike objective " ;
  }

  @Override
  public String getDescription() {
    return "Strike objective";
  }
}
