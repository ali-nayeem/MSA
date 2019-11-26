# Multi-objective formulation of MSA for phylogeny estimation (Do application-aware measures guide towards better phylogenetic tree?)
This is a JAVA Netbeans project built on top of [jMetalMSA](https://github.com/jMetal/jMetalMSA), an open source software tool, where we have added necessary codes to compute multiple sequence problem (MSA) using evolutionary multi-objective (EMO) algorithms and necessary datasets. All dependencies (i.e., required modules) of this project are managed by [Apache Maven](https://maven.apache.org/), so it is very easy to install and run. Below we describe different features of this repository. This work was published in the journal. 

## Requirements
To use jMetalMSA the following software packages are required:
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html?ssSourceSiteId=otnes)
* [Apache Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)

## Downloading and compiling
To download jMetalMSA just clone the Git repository hosted in GitHub:
```
git clone https://github.com/ali-nayeem/JMetal4.5_Netbeans.git
```
Once cloned, you can compile the software and generate a jar file with the following command:
```
mvn package
```
This sentence will generate a directory called `target` which will contain a file called `jmetalmsa-1.0-SNAPSHOT-jar-with-dependencies.jar`


## Architecture of jMetalMSA

![alt tag](https://github.com/jMetal/jMetalMSA/blob/master/architecture/jmetalmsaarchitecture.png)

The object-oriented architecture of jMetalMSA is shown in Figure above, is composed of four core classes
(Java interfaces). Three of them (MSAProblem, MSAAlgorithm, and MSASolution) inherits from their
counterparts in [jMetal](https://github.com/jMetal/jMetal) (the inheritance relationships are omitted in the diagram), and there is a class Score to represent a
given MSA scoring function.

## Summary of features

##List of Algorithms
The list of metaheuristics currently available in jMetalMSA include the evolutionary algorithms
* NSGA-II [1]
* NSGA-III [2]
* SMS-EMOA [3]
* SPEA2 [4]
* PAES [5]
* MOEA/D [6]
* MOCell [7]
* GWASF-GA [8].

## Crossover Operator
The crossover operator is the Single-Point Crossover adapted to alignments, randomly selects a position from the parent A
by splitting it into two blocks and the parent B is tailored so that the right piece can be joined to the left piece of
the first parent (PA1) and vice versa. Selected blocks are crossed between these two parents

## Mutation Operators
The list of mutation operators included in jMetalMSA are:
* Shift-closed gaps: Closed gaps are randomly chosen and shifted to another position.
* Non-gap group splitting: a non-gap group is selected randomly, and it is split into two groups.
* One gap insertion: Inserts a gap in a random position for each sequence.
* Two adjacent gap groups merging: Selects a random group of gaps and merge with its nearest group of gaps.
* Multiple mutation

## Scores
The scores that are currently available in jMetalMSA are:
* Sum of Pairs
* Weighted Sum of Pairs with Affine Gaps
* Single sTRucture Induced Evaluation (STRIKE).
* Percentage of Totally Conserved Columns.
* Percentage of Non-Gaps


## Runing jMetalMSA

To execute the MOCell algorithm to align a particular dataset of sequences with three objectives: SOP, TC and Non-Gaps, just run this command:

````
java -cp target/jmetalmsa-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetalmsa.runner.MOCellRunner sequencesFileName PDB_ContactsDataDirectory listOfPreComputedAlignments NumberOfEvaluations PopulationSize
```
* sequencesFileName: the filename of the sequences dataset (in FASTA Format).
* dataDirectory: The Path that contains the Structural Information files (PDB's (*.pdb) and Strike Contact Matrix (*.contacts)) of the sequences to align and the Pre-Computed alignments to use to generate the Initial population of the algorithm.  
* listOfPreComputedAlignments: A list of filenames of the pre-alignments separated by `-`, only the file names must be defined, because jMetalMSA will be search these files into the `dataDirectory`.
* NumberOfEvaluations: Number of the Maximun Evaluations of the algorithm.
* PopulationSize: Size of the population of the algorithm

To execute the NSGA-II with  three objectives STRIKE, TC and %Non-Gaps (MOSAStrE) to solve a problem in BAliBASE, just run this command:

````
java -cp target/jMetalMSA-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetalmsa.runner.MOSAStrERunnerBAliBASE balibaseProblemName dataDirectory NumberOfEvaluations PopulationSize
```
* balibaseProblemName: the BAliBASE instance name, for instance `BB12001`.
* dataDirectory: The Path that contains the Structural Information files (PDB's (*.pdb) and Strike Contact Matrix (*.contacts)) of the sequences to align and the Pre-Computed alignments to use to generate the Initial population of the algorithm.  
* NumberOfEvaluations: Number of the Maximun Evaluations of the algorithm.
* PopulationSize: Size of the population of the algorithm

For solving BAliBASE problems, jMetalMSA searches the Sequences Files in FASTA format, the Contacts files and the pre-computed alignments, as follows:

* Directory with the PDB Files:   dataDirectory + /aligned/strike/ + Group + / + balibaseProblemName + /
* Balibase Directory: dataDirectory + /bb3_release/ + Group + /
* Directory with the PreAlignments:  dataDirectory + /aligned/ + Group + / + balibaseProblemName;

## Results

The output of the program are two files:
* `VAR.tsv`: contains the Pareto front approximation. For each solution, this file contains a line with the values of the three objectives.
* `FUN.tsv`: contains the Pareto set approximation. Each solution is represented in FASTA format.
