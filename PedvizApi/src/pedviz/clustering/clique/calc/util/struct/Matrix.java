package pedviz.clustering.clique.calc.util.struct;


public class Matrix {
  double [][] matrix;
  int size;

public Matrix (int size) {
  matrix = new double[size][];
  this.size=size;
  for (int x=0;x<size;x++){
    matrix[x] = new double[(size+1)-x];
  }
}

public void setTriangMatrix (double value, int key1, int key2) {
   int dim = matrix.length-1;
   if (key1>key2)
     matrix[key2][dim-key1]=value;
   else
     matrix[key1][dim-key2]=value;
 }

 public double retrieveTriangMatrix (int key1, int key2) {
   int dim = matrix.length-1;
   if (key1>key2)
     return matrix[key2][dim-key1];
   else
     return matrix[key1][dim-key2];
 }

 public int length () {
   return size;
 }

 public double[] getRow (int index) {
   return (double[])matrix[index].clone();
 }

 public void setRow (double[] row, int index) {
   matrix[index] = row;
 }

 public void cloneInto (Matrix where) {
   for (int d=0;d<size;d++) where.setRow(this.getRow(d),d);
 }

}

