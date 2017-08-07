package org.uma.jmetalmsa.score.impl;

//import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.score.Score;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NumberOfGapsScore implements Score {
  @Override
  public <S extends MSASolution> double compute(S solution, char [][]decodedSequences) {
    //int testOrder = (Integer) solution.getAttribute("testOrder");
    //if(testOrder != 2)
    //{
    //    System.out.println("Trouble@"+getName());
    //    System.exit(0);
    //}
    //solution.setAttribute("testOrder", testOrder+1);  
      
    double numberOfGaps = solution.getNumberOfGaps();
    return numberOfGaps;
  }

  @Override
  public boolean isAMinimizationScore() {
    return true;
  }

  @Override
  public String getName() {
    return "NumGaps";
  }

  @Override
  public String getDescription() {
    return "Number of gaps in a multiple sequence";
  }
  
  public static int getDependency()
  {
      return 0;
  } 
}
