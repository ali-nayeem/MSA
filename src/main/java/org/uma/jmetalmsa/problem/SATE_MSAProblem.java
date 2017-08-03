package org.uma.jmetalmsa.problem;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.uma.jmetalmsa.score.Score;

public class SATE_MSAProblem extends MSAProblem {
  //public String PDBPath;
  //public String InstanceBalibase;
  public String DataPath;
  public String PreComputedPath;
  public String inputFile = "input.fasta";

  /**
   * Constructor
   */
  public SATE_MSAProblem(String problemName, String dataBaseDirectory, List<Score> scoreList)
          throws IOException, CompoundNotFoundException {
    super(scoreList);
    
    List<String> scoreNameList = new ArrayList<> ();
    for (Score s : scoreList)
    {
        scoreNameList.add(s.getName());
    }
    Collections.sort(scoreNameList);
    
    String problemNameWithScoreList = problemName;
    for (String name : scoreNameList)
    {
        name = name.replace(' ', '-');
        problemNameWithScoreList += "_" + name;
    }
    setName(problemNameWithScoreList);

    setPaths(problemName, dataBaseDirectory);
    List<String> dataFiles = new ArrayList<>();
    dataFiles.add(PreComputedPath + "_clustalo");
    dataFiles.add(PreComputedPath + "_clustalw");
    dataFiles.add(PreComputedPath + "_fsa");
    dataFiles.add(PreComputedPath + "_kalign");
    dataFiles.add(PreComputedPath + "_mafft");
    dataFiles.add(PreComputedPath + "_muscle");
    dataFiles.add(PreComputedPath + "_pasta");
    dataFiles.add(PreComputedPath + "_prank");
    dataFiles.add(PreComputedPath + "_tcoffee");

    listOfSequenceNames = new ArrayList<>();
    listOfSequenceNames = readSeqNameFromAlignment(DataPath + inputFile);

    originalSequences = readDataFromFastaFile(DataPath + inputFile);

    setNumberOfVariables(originalSequences.size());
    setNumberOfConstraints(0);

    listOfPrecomputedStringAlignments = readPreComputedAlignments(dataFiles);

  }


  
  public void setPaths(String problemName, String dataBaseDirectory) {

    //String Group = "RV" + problemName.substring(2, 4).toString();

    //Directory with the PDB Files
    //PDBPath = dataBaseDirectory + "/aligned/strike/" + Group + "/" + problemName + "/";

    //SATE Instance Directory
    DataPath = dataBaseDirectory + "/" + problemName + "/";

    //InstanceBalibase = problemName;

    //Directory with the PreAlignments
    PreComputedPath = DataPath + inputFile;


  }

 
}
