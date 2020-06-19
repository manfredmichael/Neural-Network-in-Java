import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Neural_Network extends PApplet {

int [] layers={3,10,10,3};
NeuralNetwork nn;
float [][] input= {{0,0,0}, {1,0,0}, {1,1,0}, {1,1,1}, {0,1,0}, {0,0,1}, {0,1,1}};
float [][] target={{0,1,0}, {0,0.5f,0.5f}, {0,0,1}, {0,0,0}, {0,0,0}, {0.5f,0.5f,0}, {1,0,0}};

int dataset = 0;

NetBoard netBoard;
public void setup() {
  
  nn=new NeuralNetwork(layers);
  netBoard = new NetBoard();
  float cost;
  for (int i=0; i<100000; i++) {
    int randomIndex=round(random(input.length-1));
    float guess []=nn.feedForward(input[randomIndex]);
    nn.train(input[randomIndex], target[randomIndex]);
    cost = (target[randomIndex][0]-guess[0]);
    cost *= cost;
    //println(cost);
  }
  float [][]output=new float[target.length][target[0].length];
  for (int i=0; i<input.length; i++) {
    output[i]=nn.feedForward(input[i]);
  }

  //for (int i=0; i<input.length; i++) {
  //  printArray(output[i]);
  //}
}

public void draw() {
  background(51);
  nn.feedForward(input[dataset]);
  netBoard.visualizeNN();
}

public void keyPressed(){
  dataset++;
  if(dataset >= input.length)
    dataset = 0;
}

public void mousePressed(){
  for (int i=0; i<1; i++) {
    int randomIndex=round(random(input.length-1));
    float guess []=nn.feedForward(input[randomIndex]);
    nn.train(input[randomIndex], target[randomIndex]);
  }

  for (Matrix o : nn.weights) {
    o.printMatrix();
  }
}
class Matrix {
  float [][] array;
  int row;
  int column;

  Matrix(int row, int column) {
    array=new float[row][column];
    this.row=row;
    this.column=column;
    for (int i=0; i<row; i++) {
      for (int j=0; j<column; j++) {
        array[i][j]=round(random(-1, 1));
      }
    }
  }

  Matrix(Matrix other) {
    row=other.row;
    column=other.column;
    array=new float[row][column];
    for (int i=0; i<row; i++) {
      for (int j=0; j<column; j++) {
        array[i][j]=other.array[i][j];
      }
    }
  }

  Matrix(float [] input) {
    row=1;
    column=input.length;
    array=new float[row][column];
    for (int i=0; i<row; i++) {
      for (int j=0; j<column; j++) {
        array[i][j]=input[j];
      }
    }
  }

  public float get(int i, int j) {
    return array[i][j];
  }

  public float [][] getArray() {
    return array;
  }
  
  public void set(float n){
    for (int i=0; i<row; i++) {
      for (int j=0; j<column; j++) {
        array[i][j]=n;
      }
    }
  }

  public Matrix copy() {
    Matrix result=new Matrix(row, column);
    for (int i=0; i<row; i++) {
      for (int j=0; j<column; j++) {
        result.array[i][j]=array[i][j];
      }
    }
    return result;
  }

  public void printMatrix() {
    for (int i=0; i<row; i++) {
      for (int j=0; j<column; j++) {
        print(array[i][j]+" ");
      }
      println();
    }
    println();
  }

  public void T() {
    Matrix result=new Matrix(column, row);
    for (int i=0; i<column; i++) {
      for (int j=0; j<row; j++) {
        result.array[i][j]=array[j][i];
      }
    }
    row=result.row;
    column=result.column;
    array=result.array;
  }

  public void add(float n) {
    for (int i=0; i<row; i++) {
      for (int j=0; j<column; j++) {
        array[i][j]=array[i][j]+n;
      }
    }
  } 

  public void mult(float n) {
    for (int i=0; i<row; i++) {
      for (int j=0; j<column; j++) {
        array[i][j]=array[i][j]*n;
      }
    }
  }
}

class MatrixMath {
  public Matrix mult(Matrix a, Matrix b) {
    Matrix result=new Matrix(a.row, b.column);

    if (a.column==b.row) {
      for (int i=0; i<a.row; i++) {
        for (int j=0; j<b.column; j++) {
          result.array[i][j]=0;
          for (int k=0; k<b.row; k++) {
            result.array[i][j]+=a.array[i][k]*b.array[k][j];
          }
        }
      }
    } else {
      println("=========================================");
      println("this matrix column doesnt match other row");
      println("=========================================");
    }

    return result;
  }

  public Matrix add(Matrix a, Matrix b) {
    Matrix result=new Matrix(a.row, a.column); //not done error mismatch row cathcer
    if ((a.row==b.row)&&(a.column==b.column)) {
      for (int i=0; i<result.row; i++) {
        for (int j=0; j<result.column; j++) {
          result.array[i][j]=a.array[i][j]+b.array[i][j];
        }
      }
    } else {
      println("=========================================");
      println("this matrix column/row doesnt match other column/row");
      println("=========================================");
    }
    return result;
  }

  public Matrix sub(Matrix a, Matrix b) {
    Matrix result=new Matrix(a.row, a.column);
    if ((a.row==b.row)&&(a.column==b.column)) {
      for (int i=0; i<result.row; i++) {
        for (int j=0; j<result.column; j++) {
          result.array[i][j]=a.array[i][j]-b.array[i][j];
        }
      }
    } else {
      println("=========================================");
      println("this matrix column/row doesnt match other column/row");
      println("=========================================");
    }
    return result;
  }

  public Matrix getT(Matrix a) {
    Matrix result=new Matrix(a.column, a.row);
    for (int i=0; i<result.row; i++) {
      for (int j=0; j<result.column; j++) {
        result.array[i][j]=a.array[j][i];
      }
    }
    return result;
  }
  
  public Matrix hadamartProduct(Matrix a,Matrix b){
  Matrix result=new Matrix(a.row, a.column); //not done error mismatch row cathcer
    if ((a.row==b.row)&&(a.column==b.column)) {
      for (int i=0; i<result.row; i++) {
        for (int j=0; j<result.column; j++) {
          result.array[i][j]=a.array[i][j]*b.array[i][j];
        }
      }
    } else {
      println("=========================================");
      println("this matrix column/row doesnt match other column/row");
      println("=========================================");
    }
    return result;
  }

  public Matrix sigmoid(Matrix a) {
    Matrix result=new Matrix(a);
    for (int i=0; i<a.row; i++) {
      for (int j=0; j<a.column; j++) {
        float x=result.array[i][j];
        result.array[i][j]=1/(1+exp(-1*x));

        Double d = new Double(result.array[i][j]);
        if (d.isNaN()){
          print("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
          noLoop();
        }
      }
    }
    return result;
  }

  public Matrix softmax(Matrix a){
    Matrix result = new Matrix(a);
    double sum    = 0;

    for (int i=0; i<a.row; i++) {
      for (int j=0; j<a.column; j++) {
        float x = result.array[i][j];

        Double d = new Double(x);

        sum += exp(x);
        // if (d.isNaN())
        //   print("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
        // if(exp(x) == 0)
        //   print("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
      }
    }   

    for (int i=0; i<a.row; i++) {
      for (int j=0; j<a.column; j++) {
        float x = result.array[i][j];

        Double d = new Double(x);

        result.array[i][j] = (float) (exp(x) / sum);
      }
    }

    // println(sum);
    if(sum == 0)
       print("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    return result;
  }
}

MatrixMath Matrix=new MatrixMath();
class NeuralNetwork {
  ArrayList<Matrix> weights=new ArrayList<Matrix>();
  ArrayList<Matrix> biases=new ArrayList<Matrix>();
  ArrayList<Matrix> perceptrons=new ArrayList<Matrix>();
  NeuralNetwork(int [] layers) {
    for (int i=0; i<layers.length-1; i++) {
      int row=layers[i+1];
      int column=layers[i];
      weights.add(new Matrix(row, column));

      row=layers[i+1];
      column=1;
      biases.add(new Matrix(row, column));
    }
  }

  public NeuralNetwork copy() {
    int [] parameter = {0};
    NeuralNetwork clone = new NeuralNetwork(parameter);
    clone.weights.clear();
    clone.biases.clear();
    for (int i = 0; i < weights.size(); i++) {
      clone.weights.add(weights.get(i).copy());
      clone.biases.add(biases.get(i).copy());
    }
    return clone;
  }

  public float [] feedForward(float [] input) {
    perceptrons.clear();
    Matrix output=new Matrix(input);
    output.T();
    perceptrons.add(output.copy());
    for (int i=0; i<weights.size(); i++) {
      output=Matrix.mult(weights.get(i), output);

      output=Matrix.add(output, biases.get(i));

      if (i < weights.size() - 1) {
        output=Matrix.sigmoid(output);
        perceptrons.add(output.copy());
      }
    }

    output = Matrix.softmax(output);
    perceptrons.add(output.copy());
    output.T();
    return output.array[0];
  }

  public void train(float [] inputArray, float [] targetArray) {
    float learningRate=0.01f;
    ArrayList<Matrix> neurons=new ArrayList<Matrix>();
    ArrayList<Matrix> errors=new ArrayList<Matrix>();

    Matrix target=new Matrix(targetArray);
    target.T();
    Matrix output=new Matrix(inputArray);
    output.T();
    neurons.add(output.copy());

    for (int i=0; i<weights.size(); i++) {
      output=Matrix.mult(weights.get(i), output);
      output=Matrix.add(output, biases.get(i));

      if (i < weights.size() - 1) {
        output=Matrix.sigmoid(output);
        neurons.add(output.copy());
      }
    }

    output = Matrix.softmax(output);
    neurons.add(output.copy());
    errors.add(Matrix.sub(target, output));

    for (int i=weights.size()-1; i>0; i--) {
      Matrix transposedWeight=Matrix.getT(weights.get(i));
      for (int j=0; j<transposedWeight.column; j++) {
        float sumOfColumn = 0;
        for (int k=0; k<transposedWeight.row; k++) {
          sumOfColumn += abs(transposedWeight.array[k][j]);
        }
        for (int k=0; k<transposedWeight.row; k++) {
          if (sumOfColumn>=1)
            transposedWeight.array[k][j]*=(1/sumOfColumn);
        }
      }

      Matrix error=Matrix.mult(transposedWeight, errors.get(0));
      errors.add(0, error);
    }

    for (int i=weights.size()-1; i>=0; i--) {
      Matrix gradient = errors.get(i).copy();

      // if (i < weights.size() - 1) {
        Matrix derivatedSigmoid=neurons.get(i+1).copy();
        Matrix inverseMatrix=derivatedSigmoid.copy();
        inverseMatrix.set(1);
        inverseMatrix=Matrix.sub(inverseMatrix, derivatedSigmoid);
        derivatedSigmoid=Matrix.hadamartProduct(derivatedSigmoid, inverseMatrix);
        gradient=Matrix.hadamartProduct(errors.get(i), derivatedSigmoid);
      // }

      gradient.mult(learningRate);
      Matrix slope=Matrix.mult(gradient, Matrix.getT(neurons.get(i)));
      

      Matrix weight=weights.get(i).copy();
      weights.remove(i);
      weights.add(i, Matrix.add(weight, slope));

      Matrix bias=biases.get(i).copy();
      biases.remove(i);
      biases.add(i, Matrix.add(bias, gradient));
    }
  }
}

float scroll = 0;

class NetBoard {
  PGraphics board;
  int margin = 30;
  float boardX       = width / 4;
  int boardSize      = width / 2 + margin;
  float inputX       = margin / 2;
  float outputX      = boardSize - margin / 2;
  float size         = outputX - inputX;
  float resolution   = size / (layers.length - 1);
  NetBoard() {
    board = createGraphics(boardSize, height);
  }
  public void visualizeNN() {
    image(board, boardX, 0);
    board.beginDraw();
    board.background(255);
    for ( int i = 0; i < nn.perceptrons.size(); i++) {
      for ( int j = 0; j < layers[i]; j++) {
        float x      = inputX + resolution * i;
        float y      = 50 + 40 * (j * 2  + 1 - layers[i]) / 2 + scroll;
        float value  = nn.perceptrons.get(i).get(j, 0) ;
        if (i < nn.weights.size()) {
          for (int k = 0; k < layers[i + 1]; k++) {
            float xo = inputX + resolution * (i + 1);
            float yo = 50 + 40 * (k * 2  + 1 - layers[i + 1]) / 2 + scroll;
            float w  = nn.weights.get(i).get(k, j);
            if (w>0)
              board.stroke(0, 255, 0, 128 * abs(w));
            else
              board.stroke(255, 0, 0, 128 * abs(w));
            board.line(x, y, xo, yo);
          }
        }
        board.stroke(0);
        board.fill(100 + 155 * value);
        board.ellipse(x, y, 30, 30);
        board.textAlign(CENTER);
        if (value > 0.5f)
          board.fill(0);
        else
          board.fill(255);
        board.text(nf(value, 1, 2), x + 1, y + 5);
      }
    }
    board.endDraw();
    if (mousePressed) {
      scroll += mouseY- pmouseY;
    }
  }
}
  public void settings() {  size(600,600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Neural_Network" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
