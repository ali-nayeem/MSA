# Multi-objective formulation of MSA for phylogeny estimation (Do application-aware measures guide towards better phylogenetic tree?)
This is a Java Netbeans project built on top of [jMetalMSA](https://github.com/jMetal/jMetalMSA), an open source software tool, where we have added necessary codes to compute multiple sequence problem (MSA) using evolutionary multi-objective (EMO) algorithms and necessary datasets. All dependencies (i.e., required modules) of this project are managed by [Apache Maven](https://maven.apache.org/), so it is very easy to install and run. Below we describe different features of this repository. This work was published in the journal. 

## Requirements
To use this project, the following software tools are required:
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html?ssSourceSiteId=otnes)
* [Apache Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)

## Downloading and compiling
To download this project, just clone the Git repository hosted in GitHub:
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
Here we summarize different features that we used in this study

### Evolutionary algorithm
In our study, we used the following algorithms:
* NSGA-II (`org.uma.jmetalmsa.algorithm.nsgaii`)
* NSGA-III (`org.uma.jmetalmsa.algorithm.algoyy.NSGAIIIYY`)

For NSGA-III, we adopt the Java implementation of Dr. Yuan Yuan avilable at https://github.com/yyxhdy/ManyEAs

### Crossover Operator
The crossover operator is the Single-Point Crossover adapted to alignments, randomly selects a position from the parent A
by splitting it into two blocks and the parent B is tailored so that the right piece can be joined to the left piece of
the first parent (PA1) and vice versa. Selected blocks are crossed between these two parents

### Mutation Operators
The list of mutation operators included in jMetalMSA are:
* Shift-closed gaps: Closed gaps are randomly chosen and shifted to another position.
* Non-gap group splitting: a non-gap group is selected randomly, and it is split into two groups.
* One gap insertion: Inserts a gap in a random position for each sequence.
* Two adjacent gap groups merging: Selects a random group of gaps and merge with its nearest group of gaps.
* Multiple mutation

### Objective function
The scores that are currently available in jMetalMSA are:
* Sum of Pairs
* Weighted Sum of Pairs with Affine Gaps
* Single sTRucture Induced Evaluation (STRIKE).
* Percentage of Totally Conserved Columns.
* Percentage of Non-Gaps

### Datasets
We used three datasets listed below:
 * 100-taxon simulated dataset (inside `dataset/100S/`)
 * Biological rRNA datasets (`dataset/100S/23S.E` and `dataset/100S/23S.E.aa_ag`)
 * BAliBASE 3.0 Benchmark (inside `example/bb3_release/`)

### Experiementation
To experiment with NSGA-II and NSGA-III on three datasets we implemented the three Java classes in package `org.uma.jmetalmsa.experiment` as follows:
 * NSGAIIStudy 
 * NSGAIIStudyBalibase
 * NSGAIIIStudy


### Running experiment

To execute the class named `NSGAIIStudy`, run the following command in terminal from the project root:

```
java -cp target/jmetalmsa-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetalmsa.experiment.NSGAIIStudy
```
