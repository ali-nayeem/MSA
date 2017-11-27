package org.uma.jmetalmsa.score.impl;

//import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.score.Score;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NumberOfAlignedColumnsScore implements Score {
  @Override
  public <S extends MSASolution> double compute(S solution,char [][]decodedSequences) {
    //int testOrder = (Integer) solution.getAttribute("testOrder");
    //if(testOrder != 1)
    //{
    //    System.out.println("Trouble@TC");
    //    System.exit(0);
    //}
    //solution.setAttribute("testOrder", testOrder+1);  
      if(solution.getAttribute("AlignedColumnCount") == null)
      {
          new EntropyScore().compute(solution, decodedSequences);
      }
    
      return (double)solution.getAttribute("AlignedColumnCount");
  }

  @Override
  public boolean isAMinimizationScore() {
    return false;
  }

  @Override
  public String getName() {
    return "TC";
  }

  @Override
  public String getDescription() {
    return "Number of aligned columns in a multiple sequence";
  }
  
  public static int getDependency()
  {
    return EntropyScore.getDependency()+1;
  }
}
