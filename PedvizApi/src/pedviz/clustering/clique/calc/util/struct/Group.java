package pedviz.clustering.clique.calc.util.struct;

import java.util.Vector;
public class Group {
      private Vector linkedTrees;

      public Group (Integer t) {
          linkedTrees = new Vector();
          linkedTrees.addElement(t);
      }

      public void add (Integer treeNum) {
           if (!isHere(treeNum))
                  linkedTrees.addElement(treeNum);
      }

      public boolean isHere (Integer elem) {
          if (linkedTrees.indexOf(elem) == -1)
              return false;
          else
              return true;
      }

      public int size() {
          return linkedTrees.size();
      }

      public Integer linkedTree (int ind) {
              return (Integer)linkedTrees.elementAt(ind);
      }

      public void sort () {
       Integer lastTree;
       Integer prevTree;
       boolean spostato = true;
       while (spostato && linkedTrees.size() > 0 && spostato == true) {
           spostato = false;
           prevTree = null;
           for (int x = 0; x < linkedTrees.size();x++) {
                lastTree = (Integer)linkedTrees.elementAt(x);
                if (prevTree != null) {
                    if ((prevTree.intValue() > lastTree.intValue())) {
                        linkedTrees.setElementAt(lastTree, x-1);
                        linkedTrees.setElementAt(prevTree, x);
                        spostato = true;
                        lastTree = prevTree;
                    }
                }
                prevTree = lastTree;
            }
        }
    }
 
    public void removeLinkedTrees () {
        linkedTrees.clear();
    }
}