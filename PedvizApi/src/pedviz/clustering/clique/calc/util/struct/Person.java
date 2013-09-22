package pedviz.clustering.clique.calc.util.struct;

public class Person {
      String IDPerson;
      boolean active;
      boolean activatedForReconstruction;

      public Person (String IDPerson){
             this.IDPerson = IDPerson;
             this.active = true;
             this.activatedForReconstruction = false;
      }

      public Person (String IDPerson, boolean status){
             this.IDPerson = IDPerson;
             this.active = status;
      }

      public String getIDPerson () {
              return this.IDPerson;
      }

      public boolean isActive () {
              return this.active;
      }

      public void activate () {
              this.active = true;
      }

      public void inactivate () {
              this.active = false;
      }

      public void activateForReconstruction () {
              this.activatedForReconstruction = true;
      }

      public void unactivateForReconstruction () {
              this.activatedForReconstruction = false;
      }

      public boolean isActivatedForReconstruction () {
              return this.activatedForReconstruction;
      }

}
