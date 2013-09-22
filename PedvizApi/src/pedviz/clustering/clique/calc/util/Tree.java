package pedviz.clustering.clique.calc.util;

import java.util.Vector;

import pedviz.clustering.clique.calc.util.struct.Link;
import pedviz.clustering.clique.calc.util.struct.Person;
public class Tree {
    int numGenerazioneNonGenotipizzata;
    private Vector generation;
    private Vector link;
    private String personSex;

    private Vector linkedTrees;

    private boolean active;
    boolean alone;
    int deeperLink;

    public Tree(String id, Integer treePosition) {
        generation = new Vector();
        generation.clear();
        Vector root = new Vector();
        root.add(null);
        root.add(new Person (id));
        generation.add(null);
        generation.add(root);
        personSex = "";
        active = true;
        alone = false; // per inserire nel file di oputput anche quegli alberi privi di link
        link = new Vector();
        linkedTrees = new Vector();
        linkedTrees.add(treePosition);
        numGenerazioneNonGenotipizzata = 0;
        deeperLink = 0;
    }

    public int getPersonPosition(Vector persons, String IDPerson){
        Person tmpPerson;
        for (int x = 1;x < persons.size(); x++){
             tmpPerson = (Person) persons.elementAt(x);
             if (tmpPerson != null) {
                 if (tmpPerson.getIDPerson().equals(IDPerson))
                     return x;
             }
        }
        return -1;
    }

    public Vector getLink() {
        return this.link;
    }

    public boolean isActiveLink(String IDPerson, int generation, int position) {
        Link tmpLink;
        for (int x=0;x< link.size();x++) {
            tmpLink = (Link) link.elementAt(x);
            if (tmpLink.getIDPerson().equals(IDPerson) && tmpLink.getGeneration() == generation && tmpLink.getPosition() == position) {
                if (tmpLink.getStatus()==true) {
                    return true;
                }
            }
        }
       return false;
    }

    public boolean isUnactiveLink(String IDPerson, int generation, int position) {
        Link tmpLink;
        for (int x=0;x< link.size();x++) {
            tmpLink = (Link) link.elementAt(x);
            if (tmpLink.getIDPerson().equals(IDPerson) && tmpLink.getGeneration() == generation && tmpLink.getPosition() == position) {
                if (tmpLink.getStatus()==false) {
                    return true;
                }
            }
        }
       return false;
    }

   public int getLastGeneration(){
    //Restituisce il numero di generazione che va da 1 a n.
        int a = generation.size();
        return a -1;
    }

    public int getPersonsPerGeneration(int gen){
        int a = (int) Math.pow((double)2, (double)gen);
        return  a;
    }

    public Vector getGeneration (int gen){
          Vector g = (Vector)generation.elementAt(gen);
          return g;
    }

    public void addGenerationLevel()
    {
        int newGenerationSize = getPersonsPerGeneration (getLastGeneration());
        Vector newGeneration = new Vector ();
        for (int x = 0;x < newGenerationSize+1; x++)
            newGeneration.add(null);
        generation.add (newGeneration);
    }

    public int getPersonPosition (String IDPerson, int generation){
          Vector entireGen = this.getGeneration(generation);
          int position = getPersonPosition(entireGen, IDPerson);
          if (position != -1) return position;
          else return 0;
    }

    public String getPersonID (int generation, int position){
          //System.out.println("Albero:" + this.getTreeName());
          //System.out.println("Generazione:" + generation);
          //System.out.println("Posizione:" + position);
          Vector entireGen = this.getGeneration(generation+1);
          //System.out.println("gen size:" + entireGen.size());
          Person ID = (Person)entireGen.elementAt(position);
          if (ID != null) return ID.getIDPerson();
          else return "";
    }

    public String getPersonWifeHusbandID (int generation, int position){
          if ((position / 2)*2 != position)
          return this.getPersonID(generation, position+1);
          else return this.getPersonID(generation, position-1);
    }

    public int lookGenerationFor (String IDPerson){
          String person = "";
          Vector entireGen;
          int numGen = getLastGeneration();
          for (int x = 1;x <= numGen; x++)
          {
              entireGen = this.getGeneration(x);
              if (getPersonPosition(entireGen, IDPerson) != -1) return x;
          }
          return 0;
    }

    //restituisce generazione + posizione
    public int [] lookCoordinatesFor (String IDPerson){
          String person = "";
          Vector entireGen;
          int position;
          int[] result ={0, 0};
          int numGen = getLastGeneration();
          for (int x = 1;x <= numGen; x++)
          {
              entireGen = this.getGeneration(x);
              position = getPersonPosition(entireGen, IDPerson);
              if (position != -1) {
                  result[0] = (x);
                  result[1] = (position);
                  return result;
              }
          }
          return result;
    }

    public void setParent (String IDChild, int childGeneration, String IDParent, boolean isFather, boolean status){
        int childPosition = getPersonPosition (IDChild, childGeneration);
        int parentPosition;
        if (isFather) parentPosition = ((childPosition ) * 2)  - 1;
        else          parentPosition = ((childPosition ) * 2);
        int parentGeneration = childGeneration + 1;
        if (getLastGeneration() < parentGeneration)
            addGenerationLevel();
        Vector v = getGeneration(parentGeneration);
        Person person = new Person (IDParent, status);
        v.setElementAt(person, parentPosition);
    }

    public String getParent (String IDChild, int childGeneration, boolean isFather){
        int childPosition = getPersonPosition (IDChild, childGeneration);
        int parentPosition;
        Person tmpPerson;
        if (isFather) parentPosition = ((childPosition ) * 2)  - 1;
        else          parentPosition = ((childPosition ) * 2);
        int parentGeneration = childGeneration + 1;
        String elem;

        if (parentGeneration > getLastGeneration())
            elem = "0";
        else{
            Vector v = getGeneration(parentGeneration);
            tmpPerson = (Person)v.elementAt(parentPosition);
            if (tmpPerson == null) elem = "0";
            else elem = tmpPerson.getIDPerson();
        }
        return (elem);
//        v.setElementAt(IDParent, parentPosition);
    }

    public String getParent (String IDChild, int childGeneration, int childPosition, boolean isFather){
        int parentPosition;
        Person tmpPerson;
        if (isFather) parentPosition = ((childPosition ) * 2)  - 1;
        else          parentPosition = ((childPosition ) * 2);
        int parentGeneration = childGeneration + 1;
        String elem;
        if (parentGeneration > getLastGeneration())
            elem = "0";
        else{
            Vector v = getGeneration(parentGeneration);
            tmpPerson = (Person)v.elementAt(parentPosition);
            if (tmpPerson == null) elem = "0";
            else elem = tmpPerson.getIDPerson();
        }
        return (elem);
    }

    public boolean isHere (String IDPerson){
        int childGeneration = lookGenerationFor(IDPerson);
        if (childGeneration == 0) return false;
        else return true;
    }

    public String getFather (String IDChild, int childGeneration){
        return getParent (IDChild, childGeneration, true);
    }

    public String getFather (String IDChild, int childGeneration, int childPosition){
        return getParent (IDChild, childGeneration, childPosition, true);
    }

    public String getMother (String IDChild, int childGeneration){
        return getParent (IDChild, childGeneration, false);
    }

    public String getMother (String IDChild, int childGeneration, int childPosition){
        return getParent (IDChild, childGeneration, childPosition, false);
    }

    public String getFather (String IDChild){
        int childGeneration = lookGenerationFor(IDChild);
        return getParent (IDChild, childGeneration, true);
    }

    public String getMother (String IDChild){
        int childGeneration = lookGenerationFor(IDChild);
        return getParent (IDChild, childGeneration, false);
    }

    public String getTreeName (){
        Vector p = (Vector)generation.elementAt(1);
        return (String)((Person)p.elementAt(1)).getIDPerson();
    }

    public void setFather (String IDChild, int childGeneration, String IDFather, boolean status){
        setParent (IDChild, childGeneration, IDFather, true, status);
    }

    public void setMother (String IDChild, int childGeneration, String IDFather, boolean status){
        setParent (IDChild, childGeneration, IDFather, false, status);
    }

    public void setFather (String IDChild, String IDFather){
        int childGeneration = lookGenerationFor(IDChild);
        setParent (IDChild, childGeneration, IDFather, true, true);
    }

    public void setMother (String IDChild, String IDFather){
        int childGeneration = lookGenerationFor(IDChild);
        setParent (IDChild, childGeneration, IDFather, false, true);
    }

    public void setLink (String IDPerson, int generation, int position, boolean active){
        Link tmpLink;
        boolean modified = false;
        //Segna tutti i link

        for (int x = 0;x < link.size(); x++){
             tmpLink = (Link) link.elementAt(x);
             if (tmpLink != null) {
                 if (tmpLink.getIDPerson().equals(IDPerson)) {
                    if (generation == tmpLink.getGeneration() && position == tmpLink.getPosition())
        //          tmpLink.modifyLink(generation, position);
                    modified = true;
                 }
             }
        }
        if (!modified) {
            tmpLink = new Link (IDPerson, generation, position, active);
            link.addElement(tmpLink);
        }
        //setParent (IDChild, childGeneration, IDFather, true);
    }

    public void setLinkedTree (int numTreeint, int generationInt, int ownGenerationInt){
        Integer numTree = new Integer (numTreeint);
        Integer generation = new Integer (generationInt);
        Integer ownGeneration = new Integer (ownGenerationInt);
        Integer tmpNumTree;
        if (deeperLink<(generationInt + ownGenerationInt)) 
        	deeperLink=generationInt + ownGenerationInt;
        
        for (int t=0; t<linkedTrees.size(); t++) {
            tmpNumTree = (Integer)linkedTrees.elementAt(t);
            if (tmpNumTree.equals(numTree)){
                return;
            }
        }
        //System.out.println(numTreeint);
        //System.out.println((String)this.getTreeName());
        linkedTrees.addElement(numTree);
    }

    public void setLinkedTree (int numTreeint){
        Integer numTree = new Integer (numTreeint);
        Integer tmpNumTree;
        for (int t=0; t<linkedTrees.size(); t++) {
            tmpNumTree = (Integer)linkedTrees.elementAt(t);
            if (tmpNumTree.equals(numTree)){
                return;
            }
        }
        linkedTrees.addElement(numTree);
    }

    public boolean containLinkedTree (int numTreeint){
        Integer numTree = new Integer (numTreeint);
        Integer tmpNumTree;
        for (int t=0; t<linkedTrees.size(); t++) {
            tmpNumTree = (Integer)linkedTrees.elementAt(t);
            if (tmpNumTree.equals(numTree)){
                return true;
            }
        }
        return false;
    }

    public int getDeeperLink (){
        return deeperLink;
}

    
    public void setPersonSex (String sex){
            this.personSex = new String(sex);
    }

    public String getPersonSex (){
            return this.personSex;
    }

    public void sortLink () {
       Link lastLink;
       Link prevLink;
       Link cngLink;
       boolean spostato = true;
       while (spostato && link.size() > 0 && spostato == true) {
           spostato = false;
           prevLink = null;
           for (int x = 0; x < link.size();x++) {
                lastLink = (Link)link.elementAt(x);
                if (prevLink != null) {
                    if ((prevLink.getGeneration() < lastLink.getGeneration()) || (prevLink.getGeneration() == lastLink.getGeneration() && prevLink.getPosition() < lastLink.getPosition())) {
                        link.setElementAt(lastLink, x-1);
                        link.setElementAt(prevLink, x);
                        spostato = true;
                        lastLink = prevLink;
                    }
                }
                prevLink = lastLink;
            }
        }
    }

    public Vector getLinkedTrees () {
           return this.linkedTrees;
    }

    public void activate () {
              this.active = true;
    }

    public void inactivate () {
              this.active = false;
    }

    public boolean isActive () {
            return this.active;
    }


}
