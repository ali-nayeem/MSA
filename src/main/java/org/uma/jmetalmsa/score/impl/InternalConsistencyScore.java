package org.uma.jmetalmsa.score.impl;

//import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.int_consistency.CalculateIntConsistency;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.score.Score;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InternalConsistencyScore implements Score {
  @Override
  public <S extends MSASolution> double compute(S solution, char [][]decodedSequences) {
    CalculateIntConsistency self = new CalculateIntConsistency(solution,  decodedSequences);
    self.generateAllRetaiveDistances();
    return self.mainCalculation();
  }

  @Override
  public boolean isAMinimizationScore() {
    return true;
  }

  @Override
  public String getName() {
    return "InCon";
  }

  @Override
  public String getDescription() {
    return "Internal consistency";
  }
  
  public static int getDependency()
  {
      return 0;
  } 
}
