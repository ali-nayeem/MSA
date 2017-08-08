/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.experiment;

import java.io.File;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalmsa.solution.MSASolution;

/**
 *
 * @author Nayeem
 */
public class ExperimentAlgorithmMSA extends ExperimentAlgorithm<MSASolution, List<MSASolution>>
{

    public ExperimentAlgorithmMSA(Algorithm algorithm, String algorithmTag, String problemTag)
    {
        super(algorithm, algorithmTag, problemTag);
    }
    
    
    @Override
    public void runAlgorithm(int id, Experiment<?, ?> experimentData) {
        String outputDirectoryName = experimentData.getExperimentBaseDirectory()
            + "/data/"
            + getAlgorithmTag()
            + "/"
            + getProblemTag();

    File outputDirectory = new File(outputDirectoryName);
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdirs();
      if (result) {
        JMetalLogger.logger.info("Creating " + outputDirectoryName);
      } else {
        JMetalLogger.logger.severe("Creating " + outputDirectoryName + " failed");
      }
    }

    String funFileName = outputDirectoryName + "/FUN" + id + ".tsv";
    DefaultFileOutputContext funFile = new  DefaultFileOutputContext(funFileName);
    funFile.setSeparator("\t");
    String varFileName = outputDirectoryName + "/VAR" + id + ".tsv";
    DefaultFileOutputContext varFile = new  DefaultFileOutputContext(varFileName);
    varFile.setSeparator("\n");
    
    JMetalLogger.logger.info(
            " Running algorithm: " + getAlgorithmTag() +
                    ", problem: " + getProblemTag() +
                    ", run: " + id +
                    ", funFile: " + funFileName);


    getAlgorithm().run();
    List<MSASolution> population = getAlgorithm().getResult();

    new SolutionListOutput(population)
            .setVarFileOutputContext(varFile)
            .setFunFileOutputContext(funFile)
            .print();
    }
    
    
}
