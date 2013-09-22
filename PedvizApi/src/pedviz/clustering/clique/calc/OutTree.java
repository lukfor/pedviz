package pedviz.clustering.clique.calc;

import java.util.Vector;

import pedviz.clustering.clique.ui.db.DataBase;


class OutTree {
      private Vector generation;
      private Vector ID;
      private Vector IDFather;
      private Vector IDMother;
      private Vector sex;
      private Vector affection;
      int memMaxGeneration;
      int numFounders;
      int numNonFounders;
     


      public OutTree () {
          generation = new Vector();
          ID = new Vector();
          IDFather = new Vector();
          IDMother = new Vector();
          sex = new Vector();
	      affection=new Vector();
          memMaxGeneration = 0;
          numFounders = 0;
          numNonFounders = 0;
          
      }

      public void add (String ID, String IDFather, String IDMother, String sex, int affection) {
           if (!isHere(ID)) {
                  this.generation.add(new Integer(0));
                  this.ID.add(ID);
                  this.IDFather.add(IDFather);
                  this.IDMother.add(IDMother);
                  this.sex.add(sex);
		  this.affection.add(Integer.toString(affection));
                  if (IDFather.equals("0")) numFounders++;
                      else numNonFounders++;
           }
      }

      public int IDAt (int index) {
              return ((new Integer((String)ID.elementAt(index))).intValue());
      }
      
      public int IndexAtId(int id){
    	  return ID.indexOf(id);
      }
      
      public int IDFatherAt (int index) {
          return ((new Integer((String)IDFather.elementAt(index))).intValue());
      }
      public int IDMotherAt (int index) {
          return ((new Integer((String)IDMother.elementAt(index))).intValue());
      }
      public int SexAt (int index) {
          return ((new Integer((String)sex.elementAt(index))).intValue());
      }
      public int AffectionAt (int index) {
          return ((new Integer((String)affection.elementAt(index))).intValue());

      }

      public String elementAt (int index) {
              int firstGen = ((Integer)generation.elementAt(0)).intValue();
              int gen = ((Integer)generation.elementAt(index)).intValue();// - firstGen;
              return (ID.elementAt(index) + "\t" + IDFather.elementAt(index) + "\t" + IDMother.elementAt(index) + "\t" + gen);
      }

      public String elementCyrillicAt (int index) {
              //return ("1" + "\t" + ID.elementAt(index) + "\t" + IDFather.elementAt(index) + "\t" + IDMother.elementAt(index) + "\t" + sex.elementAt(index) + "\t" + "0");
              return ("\t" + ID.elementAt(index) + "\t" + IDFather.elementAt(index) + "\t" + IDMother.elementAt(index) + "\t" + sex.elementAt(index) + "\t" + affection.elementAt(index));
      }
      
      
      /*
       * GUI
       * 
       */
      
      /*
       * famId = CliqueID = setID
       * 
       */
      public void elementCyrillicAt (int index, int famId,DataBase db, int type, int cliqueid) {
          //return ("\t" + ID.elementAt(index) + "\t" + IDFather.elementAt(index) + "\t" + IDMother.elementAt(index) + "\t" + sex.elementAt(index) + "\t" + affection.elementAt(index));
    	  try
          { 

    		 if(type==0)
               db.update("INSERT INTO BUILDCONPED(famId,id,idFather,idMother,sex,affection) VALUES("+famId+","+ID.elementAt(index)+","+IDFather.elementAt(index)+
		                   ","+IDMother.elementAt(index)+","+sex.elementAt(index)+","+affection.elementAt(index)+")");
    		 else{
                //generation?
    			 int gen = ((Integer)generation.elementAt(index)).intValue()+Math.abs(memMaxGeneration);
    			 
    		  if(cliqueid==-1) 
    			 db.update("INSERT INTO BUILDPED(famId,gen,id,idFather,idMother,sex,affection) VALUES("+famId+","+gen+","+ID.elementAt(index)+","+IDFather.elementAt(index)+
		                   ","+IDMother.elementAt(index)+","+sex.elementAt(index)+","+affection.elementAt(index)+")");
    		  else
     			 db.update("INSERT INTO BUILDPED(famId,gen,id,idFather,idMother,sex,affection) VALUES("+cliqueid+","+gen+","+ID.elementAt(index)+","+IDFather.elementAt(index)+
		                   ","+IDMother.elementAt(index)+","+sex.elementAt(index)+","+affection.elementAt(index)+")");
    		 }
           }
          catch(Exception e){
        		 System.out.println(e.getCause()+e.getMessage());
          }
     }
      
      
      public int getGeneration(int id){
    	  int index = ID.indexOf(id);
    	  if(index!=-1)
    	   return ((Integer)generation.elementAt(index)).intValue()+Math.abs(memMaxGeneration);
    	  else
    	   return -2;
      }
      
      public boolean isHere (String ID) {
          if (this.ID.indexOf(ID) == -1)
              return false;
          else
              return true;
      }

      public int size() {
          return ID.size();
      }


    public void sortGenerationsUp () {
           //System.out.println("--------UP----------");
           int     objGen = 0;
           int     numMaxLoops = 20;
           Object  obj;
           Integer gen;
           Integer oldGen;
           Integer lastGeneration = new Integer(1);
           int spostCount = 0;
           boolean spostato = true;
           while (spostato && ID.size() > 0 && numMaxLoops > 0) {
               spostato = false;

             //  System.out.println("Spostati: " + spostCount);
               spostCount = 0;
               for (int x = 0; x < ID.size();x++) {
                    obj = null;
                    oldGen = (Integer)generation.elementAt(x);
                    obj = lookForYoungestParent((String)IDFather.elementAt(x), (String)IDMother.elementAt(x));

                    if (obj != null) {
                        objGen = (((Integer)obj).intValue()) + 1;
                    }

                    if (obj != null && (oldGen.intValue() != objGen)){
                        gen = new Integer (objGen);
                        generation.setElementAt(gen, x);
                        spostato = true;
                    }

                    if (spostato) {
                        spostCount++;
                        //System.out.println(this.elementAt(x) + "\t" + objGen);
                    }
                    if (memMaxGeneration > ((Integer)generation.elementAt(x)).intValue())
                        memMaxGeneration = ((Integer)generation.elementAt(x)).intValue();
               }
               numMaxLoops --;
          }
    }

    public void sortGenerationsDown () {
           //System.out.println("--------Down----------");
           int     objGen = 0;
           int     numMaxLoops = 20;
           Object  obj;
           Integer gen;
           Integer oldGen;
           Integer lastGeneration = new Integer(1);
           int spostCount = 0;
           boolean spostato = true;
           while (spostato && ID.size() > 0 && numMaxLoops > 0) {
               spostato = false;

             //  System.out.println("Spostati: " + spostCount);
               spostCount = 0;
               for (int x = 0; x < ID.size();x++) {
                    obj = null;
                    oldGen = (Integer)generation.elementAt(x);
                    obj = lookForOldestChild((String)ID.elementAt(x), (String)sex.elementAt(x));

                    if (obj != null) {
                        objGen = (((Integer)obj).intValue()) - 1;
                    }

                    if (obj != null && (oldGen.intValue() != objGen)){
                        gen = new Integer (objGen);
                        generation.setElementAt(gen, x);
                        spostato = true;
                    }

                    if (spostato) {
                        spostCount++;
                        //System.out.println(this.elementAt(x) + "\t" + objGen);
                    }
                    if (memMaxGeneration > ((Integer)generation.elementAt(x)).intValue())
                        memMaxGeneration = ((Integer)generation.elementAt(x)).intValue();
               }
           numMaxLoops --;
          }
      }


    public void sortGenerationsZeroDown () {
       //    System.out.println("--------Zero Down----------");
           int     objGen = 0;
           int     numMaxLoops = 20;
           Object  obj;
           Integer gen;
           Integer oldGen;
           Integer lastGeneration = new Integer(1);
           int spostCount = 0;
           boolean spostato = true;
           while (spostato && ID.size() > 0 && numMaxLoops > 0) {
               spostato = false;

         //      System.out.println("Spostati: " + spostCount);
               spostCount = 0;
               for (int x = 0; x < ID.size();x++) {
                    obj = null;
                    oldGen = (Integer)generation.elementAt(x);
                    if (((String)IDFather.elementAt(x)).equals("0"))
                    {
                        obj = lookForOldestChild((String)ID.elementAt(x), (String)sex.elementAt(x));

                        if (obj != null) {
                            objGen = (((Integer)obj).intValue()) - 1;
                        }

                        if (obj != null && (oldGen.intValue() != objGen)){
                            gen = new Integer (objGen);
                            generation.setElementAt(gen, x);
                            spostato = true;
                        }

                        if (spostato) {
                            spostCount++;
                            //System.out.println(this.elementAt(x) + "\t" + objGen);
                       }
                    }
               }
           numMaxLoops --;
          }
      }


    public void takeNoParentsDown () {
           //System.out.println("--------No Parents Down----------");
           int     objGen = 0;
           int     numMaxLoops = 20;
           Object  obj;
           Integer gen;
           Integer oldGen;
           Integer lastGeneration = new Integer(1);
           int spostCount = 0;
           boolean spostato = true;
           while (spostato && ID.size() > 0 && numMaxLoops > 0) {
               spostato = false;
               //System.out.println("Spostati: " + spostCount);
               spostCount = 0;
               for (int x = 0; x < ID.size();x++) {
                    obj = null;
                    oldGen = (Integer)generation.elementAt(x);
                    obj = lookForOldestChild((String)ID.elementAt(x), (String)sex.elementAt(x));
                    if (obj != null) {
                            objGen = memMaxGeneration;
                        }

                        if (obj != null && (oldGen.intValue() != objGen)){
                            gen = new Integer (objGen);
                            generation.setElementAt(gen, x);
                            spostato = true;
                        }

                        if (spostato) {
                            spostCount++;
                            //System.out.println(this.elementAt(x) + "\t" + objGen);
                       }
               }
           numMaxLoops --;
          }
      }

    public void ChildsTakeParentsDown () {
           //System.out.println("--------Childs take Parents Down----------");
           int     objGen = 0;
           int     numMaxLoops = 20;
           Object  obj;
           Integer gen;
           Integer oldGen;
           Integer lastGeneration = new Integer(1);
           int spostCount = 0;
           boolean spostato = true;
           while (spostato && ID.size() > 0 && numMaxLoops > 0) {
               spostato = false;

               //System.out.println("Spostati: " + spostCount);
               spostCount = 0;
               for (int x = ID.size()-1; x >= 0;x--) {
                    obj = null;
                    oldGen = (Integer)generation.elementAt(x);

                        obj = lookForOldestChild((String)ID.elementAt(x), (String)sex.elementAt(x));

                        if (obj != null) {
                            objGen = (((Integer)obj).intValue()) - 1;
                        }

                        if (obj != null && (oldGen.intValue() != objGen)){
                            gen = new Integer (objGen);
                            generation.setElementAt(gen, x);
                            spostato = true;
                        }

                        if (spostato) {
                            spostCount++;
                            //System.out.println(this.elementAt(x) + "\t" + objGen);
                       }
               }
           numMaxLoops --;
          }
      }


    private Integer lookForYoungestParent (String IDFather, String IDMother) {
          Integer YoungestFather = null;
          Integer YoungestZeroFather = null;
          Integer YoungestMother = null;
          Integer YoungestZeroMother = null;
          if (IDFather.equals("0") && IDMother.equals("0")) return YoungestMother;
          int count = 0;
          for (int x = 0; x < ID.size();x++) {
               if (ID.elementAt(x).equals(IDFather)) {
                      if (((String)this.IDFather.elementAt(x)).equals("0"))
                       YoungestZeroFather = (Integer)generation.elementAt(x);
                      else
                       YoungestFather = (Integer)generation.elementAt(x);
                  count++;
               }
               if (ID.elementAt(x).equals(IDMother)) {
                      if (((String)this.IDFather.elementAt(x)).equals("0"))
                       YoungestZeroMother = (Integer)generation.elementAt(x);
                      else
                       YoungestMother = (Integer)generation.elementAt(x);
                  count++;
               }
               if (count == 2) x = ID.size();
          }

          if (YoungestMother != null && YoungestFather != null) {
                  if (YoungestMother.intValue() > YoungestFather.intValue())
                    return YoungestMother;
                  else
                    return YoungestFather;
          }

          if (YoungestMother != null && YoungestFather == null) {
                    return YoungestMother;
          }

          if (YoungestMother == null && YoungestFather != null) {
                    return YoungestFather;
          }

          if (YoungestZeroMother != null && YoungestZeroFather != null) {
                  if (YoungestZeroMother.intValue() > YoungestZeroFather.intValue())
                    return YoungestZeroMother;
                  else
                    return YoungestZeroFather;
          }

          if (YoungestZeroMother != null && YoungestZeroFather == null) {
                    return YoungestZeroMother;
          }

          if (YoungestZeroMother == null && YoungestZeroFather != null) {
                    return YoungestZeroFather;
          }

          return null;
      }

      private Integer lookForOldestChild (String IDParent, String sex) {
          Integer Oldest = null;
          for (int x = 0; x < ID.size();x++) {
               //if (sex.equals("1")) {
                   if (IDFather.elementAt(x).equals(IDParent)) {
                   if (Oldest == null || (Oldest.intValue() > ((Integer)generation.elementAt(x)).intValue()))
                       Oldest = (Integer)generation.elementAt(x);
                   }
               //}
               //if (sex.equals("2")) {
                   if (IDMother.elementAt(x).equals(IDParent)) {
                   if (Oldest == null || (Oldest.intValue() > ((Integer)generation.elementAt(x)).intValue()))
                       Oldest = (Integer)generation.elementAt(x);
                   }
               //}
          }
          return Oldest;
      }

      public void sort () {
       Integer lastGen;
       Integer prevGen;
       String tmpID;
       String tmpIDFather;
       String tmpIDMother;
       String tmpSex;
       String tmpAffection;
       boolean spostato = true;
       while (spostato && ID.size() > 0) {
           spostato = false;
           prevGen = new Integer(0);
           for (int x = 0; x < ID.size();x++) {
                lastGen = (Integer)generation.elementAt(x);
                if ((prevGen.intValue() > lastGen.intValue()) && x!=0) { //&& prevGen.intValue()!=0
                    tmpID = (String)ID.elementAt(x);
                    tmpIDFather = (String)IDFather.elementAt(x);
                    tmpIDMother = (String)IDMother.elementAt(x);
                    tmpSex = (String)sex.elementAt(x);
		    tmpAffection = (String)affection.elementAt(x);

		    generation.setElementAt(prevGen, x);
                    ID.setElementAt(ID.elementAt(x-1), x);
                    IDFather.setElementAt(IDFather.elementAt(x-1), x);
                    IDMother.setElementAt(IDMother.elementAt(x-1), x);
                    sex.setElementAt(sex.elementAt(x-1), x);
		    affection.setElementAt(affection.elementAt(x-1), x);

		    generation.setElementAt(lastGen, x-1);
                    ID.setElementAt(tmpID, x-1);
                    IDFather.setElementAt(tmpIDFather, x-1);
                    IDMother.setElementAt(tmpIDMother, x-1);
                    sex.setElementAt(tmpSex, x-1);
		    affection.setElementAt(tmpAffection, x-1);

		    spostato = true;
                    lastGen = prevGen;
                }
                prevGen = lastGen;
            }
        }
    }
}

