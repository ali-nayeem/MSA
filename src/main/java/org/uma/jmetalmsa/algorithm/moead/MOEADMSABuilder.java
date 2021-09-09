//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetalmsa.algorithm.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEAD;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetalmsa.crossover.SPXMSACrossover;
import org.uma.jmetalmsa.mutation.ShiftClosedGapsMSAMutation;
import org.uma.jmetalmsa.solution.MSASolution;

/**
 * Builder class for algorithm MOEA/D and variants
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class MOEADMSABuilder implements AlgorithmBuilder<AbstractMOEAD<MSASolution>> {
  public enum Variant {MOEAD, HYBRID} ;

  protected Problem<MSASolution> problem ;

  /** T in Zhang & Li paper */
  protected int neighborSize;
  /** Delta in Zhang & Li paper */
  protected double neighborhoodSelectionProbability;
  /** nr in Zhang & Li paper */
  protected int maximumNumberOfReplacedSolutions;

  protected MOEAD.FunctionType functionType;

  protected CrossoverOperator<MSASolution> crossover;
  protected MutationOperator<MSASolution> mutation;
  protected String dataDirectory;

  protected int populationSize;
  protected int resultPopulationSize ;

  protected int maxEvaluations;

  protected int numberOfThreads ;

  protected Variant moeadVariant ;
  protected int div1, div2;

  /** Constructor */
  public MOEADMSABuilder(Problem<MSASolution> problem, Variant variant) {
    this.problem = problem ;
    populationSize = 300 ;
    resultPopulationSize = 300 ;
    maxEvaluations = 150000 ;
    crossover = new SPXMSACrossover(0.8) ;
    mutation = new ShiftClosedGapsMSAMutation(0.2);
    functionType = MOEAD.FunctionType.TCHE ;
    neighborhoodSelectionProbability = 0.1 ;
    maximumNumberOfReplacedSolutions = 2 ;
    dataDirectory = "" ;
    neighborSize = 20 ;
    numberOfThreads = 1 ;
    moeadVariant = variant ;
    
  }

  /* Getters/Setters */
  public int getNeighborSize() {
    return neighborSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getResultPopulationSize() {
    return resultPopulationSize;
  }

  public String getDataDirectory() {
    return dataDirectory;
  }

  public MutationOperator<MSASolution> getMutation() {
    return mutation;
  }

  public CrossoverOperator<MSASolution> getCrossover() {
    return crossover;
  }

  public MOEAD.FunctionType getFunctionType() {
    return functionType;
  }

  public int getMaximumNumberOfReplacedSolutions() {
    return maximumNumberOfReplacedSolutions;
  }

  public double getNeighborhoodSelectionProbability() {
    return neighborhoodSelectionProbability;
  }

  public int getNumberOfThreads() {
    return numberOfThreads ;
  }

  public MOEADMSABuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public MOEADMSABuilder setResultPopulationSize(int resultPopulationSize) {
    this.resultPopulationSize = resultPopulationSize;

    return this;
  }

  public MOEADMSABuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public MOEADMSABuilder setNeighborSize(int neighborSize) {
    this.neighborSize = neighborSize ;

    return this ;
  }

  public MOEADMSABuilder setNeighborhoodSelectionProbability(double neighborhoodSelectionProbability) {
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability ;

    return this ;
  }

  public MOEADMSABuilder setFunctionType(MOEAD.FunctionType functionType) {
    this.functionType = functionType ;

    return this ;
  }

  public MOEADMSABuilder setMaximumNumberOfReplacedSolutions(int maximumNumberOfReplacedSolutions) {
    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions ;

    return this ;
  }

  public MOEADMSABuilder setCrossover(CrossoverOperator<MSASolution> crossover) {
    this.crossover = crossover ;

    return this ;
  }

  public MOEADMSABuilder setMutation(MutationOperator<MSASolution> mutation) {
    this.mutation = mutation ;

    return this ;
  }

  public MOEADMSABuilder setDataDirectory(String dataDirectory) {
    this.dataDirectory = dataDirectory ;

    return this ;
  }

  public MOEADMSABuilder setNumberOfThreads(int numberOfThreads) {
    this.numberOfThreads = numberOfThreads ;

    return this ;
  }
  
  public MOEADMSABuilder setDiv(int div1, int div2)
  {
      this.div1 = div1;
      this.div2 =  div2;
      return this;
  }

  public AbstractMOEAD<MSASolution> build() {
    AbstractMOEAD<MSASolution> algorithm = null ;
    if (moeadVariant.equals(Variant.MOEAD)) {
      algorithm = new MOEADMSA(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
          crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
          maximumNumberOfReplacedSolutions, neighborSize);
    }
    else if (moeadVariant.equals(Variant.HYBRID)) {
      algorithm = new HybridMOEAD2(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
          crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
          maximumNumberOfReplacedSolutions, neighborSize, div1, div2);
    }

    return algorithm ;
  }
}
