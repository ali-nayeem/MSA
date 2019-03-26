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
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetalmsa.solution.MSASolution;

/**
 *
 * @author Nayeem
 */
public class ExecuteAlgorithmsMSA extends ExecuteAlgorithms<MSASolution, List<MSASolution>>
{

    public ExecuteAlgorithmsMSA(Experiment<MSASolution, List<MSASolution>> configuration)
    {
        super(configuration);
    }
    
    public void combineVARs()
    {
        
    }
    
    public void findUniqueVARs()
    {
    }
    
    
    
}
