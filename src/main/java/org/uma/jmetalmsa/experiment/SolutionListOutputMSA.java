/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.experiment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
//import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetalmsa.solution.MSASolution;

/**
 *
 * @author Nayeem
 */
public class SolutionListOutputMSA extends SolutionListOutput
{

    public SolutionListOutputMSA(List<? extends Solution<?>> solutionList)
    {
        super(solutionList);
    }
    
    @Override
    public void printVariablesToFile(FileOutputContext context, List<? extends Solution<?>> solutionList) {
    BufferedWriter bufferedWriter = context.getFileWriter();

    try {
      if (solutionList.size() > 0) {
        //int numberOfVariables = solutionList.get(0).getNumberOfVariables();
        for (int i = 0; i < solutionList.size(); i++) {
          MSASolution msaSol = (MSASolution) solutionList.get(i);
          
          bufferedWriter.write(msaSol.getEncodedAlignment());
          
          bufferedWriter.newLine();
        }
      }

      bufferedWriter.close();
    } catch (IOException e) {
      throw new JMetalException("Error writing data ", e) ;
    }

  }
}
