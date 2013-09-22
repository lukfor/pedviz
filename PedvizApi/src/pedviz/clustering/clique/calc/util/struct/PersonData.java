package pedviz.clustering.clique.calc.util.struct;

import java.util.Vector;

public class PersonData {
  private int id;
  private int idFather;
  private int idMother;
  private int sex;
  private int affection;
  private int genotyped;
  private int personIndex;
  private int fatherIndex;
  private int motherIndex;
  private int index;
  private Vector childs;

  public PersonData (int id, int idFather, int idMother, int sex, int affection, int personIndex, int genotyped) {
    this.id = id;
    this.idFather = idFather;
    this.idMother = idMother;
    this.sex = sex;
    this.affection = affection;
    this.genotyped = genotyped;
    this.personIndex = personIndex;
    this.fatherIndex = 0;
    this.motherIndex = 0;
    this.index = 0;
    childs = new Vector();
  }


  public boolean isFounder (){
    if (this.getFather()==0) return true;
    else return false;
  }

  public Vector getParents (int person){
    return new Vector();
  }

  public int getId (){
    return this.id;
  }

  public int getPersonIndex (){
    return this.personIndex;
  }

  public void setPersonIndex (int personIndex){
    this.personIndex = personIndex;
  }

//per riindicizzare il pedigree con i genitori prima dei figli e tutti i founder all'inizio
  public int getIndex (){
    return this.index;
  }

  public void setIndex (int index){
    this.index = index;
  }
//end

  public int getFatherIndex (){
    return this.fatherIndex;
  }

  public int getFather (){
    return this.idFather;
  }

  public int getMother (){
    return this.idMother;
  }

  public int getMotherIndex (){
    return this.motherIndex;
  }

  public int getSex (){
    return this.sex;
  }

  public int getAffection (){
    return this.affection;
  }

  public boolean getAffected (){
    return this.affection==0?false:true;
  }

  
  public int getGenotyped (){
	    return this.genotyped;
  }


  public void setFatherIndex (int fatherIndex){
    this.fatherIndex =  fatherIndex;
  }

  public void setMotherIndex (int motherIndex){
    this.motherIndex = motherIndex;
  }

  public void addChild (int id){
    childs.add(new Integer(id));
  }

  public Vector getChilds (){
    return childs;
  }

}