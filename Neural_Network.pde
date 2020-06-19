int [] layers={3,10,10,3};
NeuralNetwork nn;
float [][] input= {{0,0,0}, {1,0,0}, {1,1,0}, {1,1,1}, {0,1,0}, {0,0,1}, {0,1,1}};
float [][] target={{0,1,0}, {0,0.5,0.5}, {0,0,1}, {0,0,0}, {0,0,0}, {0.5,0.5,0}, {1,0,0}};

int dataset = 0;

NetBoard netBoard;
void setup() {
  size(600,600);
  nn=new NeuralNetwork(layers);
  netBoard = new NetBoard();
  float cost;
  for (int i=0; i<10000; i++) {
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

void draw() {
  background(51);
  nn.feedForward(input[dataset]);
  netBoard.visualizeNN();
}

void keyPressed(){
  dataset++;
  if(dataset >= input.length)
    dataset = 0;
}

void mousePressed(){
  for (int i=0; i<1; i++) {
    int randomIndex=round(random(input.length-1));
    float guess []=nn.feedForward(input[randomIndex]);
    nn.train(input[randomIndex], target[randomIndex]);
  }

  for (Matrix o : nn.weights) {
    o.printMatrix();
  }
}
