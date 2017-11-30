package org.uma.jmetalmsa.util.distancematrix.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalmsa.util.distancematrix.DistanceMatrix;

import java.util.HashMap;
import java.util.Map;
import org.biojava.nbio.alignment.SubstitutionMatrixHelper;
//import org.biojava.nbio.alignment.template.SubstitutionMatrix;
//import org.biojava.nbio.core.sequence.compound.NucleotideCompound;

/**
 */

public class NUC44_V1 implements DistanceMatrix {
  private static final short DEFAULT_GAP_PENALTY = -4 ;
  private short g;// GAP PENALTY

  public short[][] NUC44 = SubstitutionMatrixHelper.getNuc4_4().getMatrix();
  //SubstitutionMatrix<NucleotideCompound> NUC44 = SubstitutionMatrixHelper.getNuc4_4();

  public static Map<Character, Integer> map = new HashMap<>() ;

  public NUC44_V1(short gapPenalty) {
    g = gapPenalty ;
    /*for (int i = 0; i < (NUC44.length - 1); i++) {
      NUC44[i][23] = g ;
    }

    for (int i = 0; i < (NUC44.length - 1); i++) {
      NUC44[23][i] = g ;
    }*/
  }

  public NUC44_V1() {
    this(DEFAULT_GAP_PENALTY) ;
  }

  @Override
  public int getDistance(char a1, char a2) {
    if(a1 == '-' || a2 == '-')
    {
        if( (a2^a1) == 0)
        {
            return 1;
        }
        else
        {
            return g; 
        }
        
    }
   
    return NUC44[get(a1)][get(a2)] ;
  }

  @Override
  public int getGapPenalty() {
    return g;
  }

  private int get(char c) {
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
  }

  @Override
  public String toString() {
    String result = "    " ;
    for (int i = 0 ; i < NUC44.length; i++) {
      if (i < 10) {
        result += "   " + i;
      } else {
        result += "  " + i;
      }
    }
    result += "\n    " ;
    for (int i = 0 ; i < NUC44.length; i++) {
        result += "----";

    }
    result += "\n" ;

    for (int i = 0 ; i < NUC44.length; i++) {
      if (i > 9) {
        result += "" + i + " | ";
      } else {
        result += " " + i + " | ";
      }
      for (int j = 0 ; j < NUC44.length; j++) {
        int value = NUC44[i][j] ;
        if ((value < 0) || (value > 9)) {
          result += " " + NUC44[i][j] + " ";
        } else {
          result += "  " + NUC44[i][j] + " ";
        }
      }
      result += "\n" ;
    }

    return result ;
  }
  
  public static void main(String[] arg)
  {
      String a = "ATCG-";
      String b = "ATCG-";
      NUC44_V1 nuc = new NUC44_V1();
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