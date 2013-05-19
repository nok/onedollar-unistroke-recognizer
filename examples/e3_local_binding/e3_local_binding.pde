import de.voidplus.dollar.*;


OneDollar one;

void setup(){
  size(400,400);
  background(250);
  smooth(); noFill(); stroke(30);
  
  one = new OneDollar(this);
  one.setVerbose(true);
  println(one);
  
  one.add("triangle", new Integer[] {137,139,135,141,133,144,132,146,130,149,128,151,126,155,123,160,120,166,116,171,112,177,107,183,102,188,100,191,95,195,90,199,86,203,82,206,80,209,75,213,73,213,70,216,67,219,64,221,61,223,60,225,62,226,65,225,67,226,74,226,77,227,85,229,91,230,99,231,108,232,116,233,125,233,134,234,145,233,153,232,160,233,170,234,177,235,179,236,186,237,193,238,198,239,200,237,202,239,204,238,206,234,205,230,202,222,197,216,192,207,186,198,179,189,174,183,170,178,164,171,161,168,154,160,148,155,143,150,138,148,136,148} );
}

void draw(){
  background(250);  
  one.draw();                   // optionally, you can draw the relevant candidates
}


void detectedTriangle(String gesture, int x, int y, int c_x, int c_y){
  println("# gesture: "+gesture+" ( "+x+" / "+y+" )");
}


void mousePressed(){
  one.start(100);
  one.bind(100, "triangle", "detectedTriangle");
}
void mouseDragged(){
  one.update(100, mouseX, mouseY);
}
void mouseReleased(){
  one.end(100);
}
