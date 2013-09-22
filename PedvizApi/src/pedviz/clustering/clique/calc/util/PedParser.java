package pedviz.clustering.clique.calc.util;

import java.io.RandomAccessFile;
import java.util.StringTokenizer;

import pedviz.clustering.clique.calc.util.struct.CPerson;

public class PedParser {

  private RandomAccessFile file;
  public static boolean error;
  public static boolean endOfData;

  public PedParser (String fileName) {
      error = false;
      endOfData = false;
      try {file = new RandomAccessFile(fileName, "r");}
      catch (Exception exception) {error = true;}
  }

  public CPerson readIndividual () {
    String s;
    CPerson tmpPerson;
    Integer famId, id, fatherId, motherId, sex, affection;
    Double DF = new Double(0.0);
    famId = id = fatherId = motherId = sex = affection = null;
    try {
      if ((s = file.readLine()) != null) {
          StringTokenizer t = new StringTokenizer(s);
          if (t.hasMoreTokens())
            famId = new Integer(t.nextToken());
          else error = true;
          if (t.hasMoreTokens())
            id = new Integer(t.nextToken());
          else error = true;
          if (t.hasMoreTokens())
            fatherId = new Integer(t.nextToken());
          else error = true;
          if (t.hasMoreTokens())
            motherId = new Integer(t.nextToken());
          else error = true;
          if (t.hasMoreTokens())
            sex = new Integer(t.nextToken());
          else error = true;
          if (t.hasMoreTokens())
            affection = new Integer(t.nextToken());
          else error = true;
          if (t.hasMoreTokens())
            DF = new Double(t.nextToken());
      }
      else {
         endOfData = true;
         if (file != null) file.close();
      }
      if (!error && !endOfData) return new CPerson(famId.intValue(), id.intValue(), fatherId.intValue(), motherId.intValue(), sex.intValue(), affection.intValue());
      else return null;
    }

    catch (Exception exception) {error = true;return null;}
  }
}
