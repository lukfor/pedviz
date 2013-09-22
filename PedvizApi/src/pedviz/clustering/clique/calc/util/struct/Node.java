package pedviz.clustering.clique.calc.util.struct;

public class Node {
    protected Node mother;
    protected Node father;
    protected String id;

    public Node(String id) {
        this.id = id;
        mother = father = null;
    }

    public void setMother (Node mother) {
        this.mother = mother;
    }

    public void setFather (Node father) {
        this.father = father;
    }

      public Node getMother () {
        return mother;
    }

    public Node getFather () {
        return father;
    }

    public String getId () {
        return id;
    }

}



