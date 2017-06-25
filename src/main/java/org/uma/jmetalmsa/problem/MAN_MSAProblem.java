/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.uma.jmetalmsa.score.Score;

/**
 *
 * @author Nayeem
 */
public class MAN_MSAProblem extends MSAProblem
{
    public MAN_MSAProblem(List<Score> scoreList, String problemPath)
          throws IOException, CompoundNotFoundException {
    super(scoreList);
    listOfSequenceNames = new ArrayList<>();
    listOfSequenceNames = readSeqNameFromAlignment(problemPath);
    originalSequences = readDataFromFastaFile(problemPath);
    setNumberOfVariables(originalSequences.size());
    setNumberOfConstraints(0);
  }
}
