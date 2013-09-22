package pedviz.clustering.clique.calc.util.struct;


public class Link {
      private String IDPerson;
      private int generation;
      private int position;
      private boolean active;
      // aggiungere il trovato per cercare il padre e la madre
      // (ciclicamente per gli altri eventuali collegati)
      public Link (String IDPerson, int generation, int position, boolean active){
             this.IDPerson = IDPerson;
             this.generation = generation;
             this.position = position;
             this.active = active;
      }

      public String getIDPerson () {
              return this.IDPerson;
      }

      public int getGeneration () {
              return this.generation;
      }

      public int getPosition () {
              return this.position;
      }

      public boolean getStatus () {
              return this.active;
      }

      public void modifyLink (int generation, int position) {
             this.generation = generation;
             this.position = position;
      }
}
