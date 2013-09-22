package pedviz.clustering.clique.calc.util.struct;


public class CPerson {
  public int famId;
  public int id;
  public int idFather;
  public int idMother;
  public int sex;
  public int affection;

  public CPerson (int famId, int id, int idFather, int idMother, int sex, int affection) {
        this.famId = famId;
        this.id = id;
        this.idFather = idFather;
        this.idMother = idMother;
        this.sex = sex;
        this.affection = affection;
  }

  public int getFamId (){
   return this.famId;
  }

  public int getFather (){
   return this.idFather;
  }

  public int getMother (){
   return this.idMother;
  }

  public int getSex (){
   return this.sex;
  }

  public int getAffection (){
   return this.affection;
  }

}