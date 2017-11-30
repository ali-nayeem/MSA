package org.uma.jmetalmsa.util.distancematrix.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalmsa.util.distancematrix.DistanceMatrix;

import java.util.HashMap;
import java.util.Map;
import org.biojava.nbio.alignment.SubstitutionMatrixHelper;
import org.biojava.nbio.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;

/**
 * Class implementing the PAM 250 substitution matrix
 *
 * Matrix based on "pam" Version 1.0.7 [13-Aug-03]
 * PAM 250 substitution matrix, scale = ln(2)/3 = 0.231049
 *
 * Expected score = -0.844, Entropy = 0.354 bits
 *
 * Lowest score = -8, Highest score = 17
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class NUC44_Old implements DistanceMatrix {
  private static final short DEFAULT_GAP_PENALTY = -4 ;
  private short g;// GAP PENALTY

  //public short[][] NUC44 = SubstitutionMatrixHelper.getNuc4_4().getMatrix();
  SubstitutionMatrix<NucleotideCompound> NUC44 = SubstitutionMatrixHelper.getNuc4_4();

  public static Map<Character, Integer> map = new HashMap<>() ;

  /*public NUC44(short gapPenalty) {
    g = gapPenalty ;
    for (int i = 0; i < (NUC44.length - 1); i++) {
      NUC44[i][23] = g ;
    }

    for (int i = 0; i < (NUC44.length - 1); i++) {
      NUC44[23][i] = g ;
    }
  }*/

  public NUC44_Old() {
    //this(DEFAULT_GAP_PENALTY) ;
  }

  @Override
  public int getDistance(char a1, char a2) {
      
    NucleotideCompound i = NUC44.getCompoundSet().getCompoundForString(a1+"");
    NucleotideCompound j = NUC44.getCompoundSet().getCompoundForString(a2+""); 
   
    
    return (int) NUC44.getValue(i, j);
  }

  @Override
  public int getGapPenalty() {
    return g;
  }

  /*private int get(char c) {
    switch (c) {
      case 'A': return 0 ;
      case 'T': return 1 ;
      case 'G': return 2 ;
      case 'C': return 3 ;
      case 'S': return 4 ;
      case 'W': return 5 ;
      case 'R': return 6 ;
      case 'Y': return 7 ;
      case 'K': return 8 ;
      case 'M': return 9 ;
      case 'B': return 10;
      case 'V': return 11;
      case 'H': return 12;
      case 'D': return 13;
      case 'N': return 14;
      //case '-': return 23;
      default: throw new JMetalException("Invalid char: " + c) ;
    }
  }*/

  @Override
  public String toString() {
    return NUC44.getMatrixAsString() ;
  }
  
  public static void main(String[] arg)
  {
      String a = "ATCG-";
      String b = "ATCG-";
      NUC44_Old nuc = new NUC44_Old();
      for (int i = 0; i < a.length(); i++)
      {
          for (int j = 0; j < b.length(); j++)
          {
              char p = a.charAt(i);
              char q = b.charAt(j);
              System.out.print("("+p+","+q+")=>"+nuc.getDistance(p, q));
          }
          System.out.println("");
      }
      //String s = nuc.toString();
  }
}