import de.voidplus.dollar.*;

OneDollar one;
String name;
PVector start, centroid, end;

void setup(){
  size(850, 500);
  background(255);
  
  name = "-";
  start = new PVector();
  centroid = new PVector();
  end = new PVector();
  
  // 1. Create instance of class OneDollar:
  one = new OneDollar(this);
  println(one);                  // Print all settings
  one.setVerbose(true);          // Activate console verbose
  
  // 2. Add gestures (templates):
  one.learn("triangle", new int[] {137,139,135,141,133,144,132,146,130,149,128,151,126,155,123,160,120,166,116,171,112,177,107,183,102,188,100,191,95,195,90,199,86,203,82,206,80,209,75,213,73,213,70,216,67,219,64,221,61,223,60,225,62,226,65,225,67,226,74,226,77,227,85,229,91,230,99,231,108,232,116,233,125,233,134,234,145,233,153,232,160,233,170,234,177,235,179,236,186,237,193,238,198,239,200,237,202,239,204,238,206,234,205,230,202,222,197,216,192,207,186,198,179,189,174,183,170,178,164,171,161,168,154,160,148,155,143,150,138,148,136,148} );
  one.learn("circle", new int[] {127,141,124,140,120,139,118,139,116,139,111,140,109,141,104,144,100,147,96,152,93,157,90,163,87,169,85,175,83,181,82,190,82,195,83,200,84,205,88,213,91,216,96,219,103,222,108,224,111,224,120,224,133,223,142,222,152,218,160,214,167,210,173,204,178,198,179,196,182,188,182,177,178,167,170,150,163,138,152,130,143,129,140,131,129,136,126,139} );
  // one.forget("circle");

  // 3. Bind templates to methods (callbacks):
  one.on("triangle circle", "detected");
  // one.off("circle");
}

// 4. Implement callbacks:
void detected(String gesture, float percent, int startX, int startY, int centroidX, int centroidY, int endX, int endY){
  name = gesture;
  start.x = startX; start.y = startY;
  centroid.x = centroidX; centroid.y = centroidY;
  end.x = endX; end.y = endY;
  
  println("Gesture: "+gesture+", "+startX+"/"+startY+", "+centroidX+"/"+centroidY+", "+endX+"/"+endY);
}

void draw(){
  background(255);
  
  // Sketch: Draw circle
  stroke(200);
  if(name.equals("circle")){
    stroke(0);
  }
  noFill();
  ellipse(width/10*3, height/2, 200, 200);
  
  // Sketch: Draw triangle
  stroke(200);
  if(name.equals("triangle")){
    stroke(0);
  }
  pushMatrix();
    translate(width/10*7,height/2);
    triangle(0, -100, 100, 100, -100, 100);
  popMatrix();
  
  fill(0); noStroke();
  text( "Detected gesture: "+name, 30, 40 );
  text( "Draw anticlockwise a circle or triangle.", 30, height-30 );
  
  fill(#a3be8c); // green
  text( "Start: "+(int)start.x+" / "+(int)start.y, 30, 60 );
  ellipse(start.x, start.y, 5, 5);
  
  fill(#bf616a); // red
  text( "Centroid: "+(int)centroid.x+" / "+(int)centroid.y, 30, 80 );
  ellipse(centroid.x, centroid.y, 5, 5);
  
  fill(#b48ead); // purple
  text( "End: "+(int)end.x+" / "+(int)end.y, 30, 100 );
  ellipse(end.x, end.y, 5, 5);

  // Optional draw:
  one.draw();
}

// 5. Track data:
void mouseDragged(){
  one.track(mouseX, mouseY);
}
