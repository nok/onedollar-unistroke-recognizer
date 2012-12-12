# OneDollar-Unistroke-Recognizer

Implementation of the [$1 Gesture Recognizer](http://depts.washington.edu/aimgroup/proj/dollar/), a template based gesture recognition, for [Processing](http://processing.org/).


## About

The [$1 Gesture Recognizer](http://depts.washington.edu/aimgroup/proj/dollar/) is a research project by Wobbrock, Wilson and Li of the University of Washington and Microsoft Research. It describes a simple algorithm for accurate and fast recognition of drawn gestures.

Gestures can be recognised at any position, scale, and under any rotation. The system requires little training, achieving a 97% recognition rate with only one template for each gesture.

> Wobbrock, J.O., Wilson, A.D. and Li, Y. (2007). [Gestures without libraries, toolkits or training: A $1 recognizer for user interface prototypes](http://faculty.washington.edu/wobbrock/pubs/uist-07.1.pdf). Proceedings of the ACM Symposium on User Interface Software and Technology (UIST '07). Newport, Rhode Island (October 7-10, 2007). New York: ACM Press, pp. 159-168.


## Download

* [OneDollarUnistrokeRecognizer.zip v0.1](https://raw.github.com/voidplus/OneDollar-Unistroke-Recognizer/master/download/OneDollarUnistrokeRecognizer.zip)

## Installation

Unzip and put the extracted *OneDollarUnistrokeRecognizer* folder into the libraries folder of your Processing sketches. Reference and examples are included in the *OneDollarUnistrokeRecognizer* folder.

## Dependencies
None.

## Tested
System: OSX, Windows<br>
Processing Version: 1.5.1, 2.0b5, 2.0b6, 2.0b7

## Examples

* [Basic](https://github.com/voidplus/OneDollar-Unistroke-Recognizer/blob/master/examples/e1_basic/e1_basic.pde)
* [Callbacks](https://github.com/voidplus/OneDollar-Unistroke-Recognizer/blob/master/examples/e2_several_callbacks/e2_several_callbacks.pde)
* [Binding](https://github.com/voidplus/OneDollar-Unistroke-Recognizer/blob/master/examples/e3_local_binding/e3_local_binding.pde)
* [Gestures](https://github.com/voidplus/OneDollar-Unistroke-Recognizer/blob/master/examples/e4_more_gestures/e4_more_gestures.pde)
* [Settings](https://github.com/voidplus/OneDollar-Unistroke-Recognizer/blob/master/examples/e5_settings/e5_settings.pde)

## Usage

### Basic

Import the library, create the recognizer object, add gestures, bind callbacks, implement callbacks and run the recognizer:

```java
import de.voidplus.dollar.*;

OneDollar one;

void setup(){
	// ...

	one = new OneDollar(this);
	one.add("circle", new Integer[] {127,141,124,140,120,139,118,139 /* ... */ });
	one.bind("circle","detected");
}

void draw(){
	// ...

	one.check();
}

void detected(String gesture, int x, int y, int centroid_x, int centroid_y){
	println("# gesture: "+gesture+" ( "+x+" / "+y+" )");
}

void mousePressed(){ one.start(100); } 	// 100 = id
void mouseDragged(){ one.update(100, mouseX, mouseY); }
void mouseReleased(){ one.end(100); }
``` 

### Data Structures

```java
one.add("circle", new Integer[] {127,141,124,140,120,139 /* ... */ });

// one.add("name", new Integer[] {x1,y1, x2,y2, x3,y3 /* ... */ });
// one.remove("name");
```

### Settings & Debugging

```java
import de.voidplus.dollar.*;

OneDollar one;

void setup(){
	// ...
	
	one = new OneDollar(this);
									// online gestures
	one.setMinLength(50);			// set the minimum length
  	one.setMaxLength(2500);			// set the maximum length
	one.setMaxTime(1000);			// set the maximum time
	
									// onedollar unistroke algorithm
	one.setMinScore(85);			// 85% similarity
	one.setRotationAngle(3);		// 3 degree rotation, higher=faster, 360/3=120 calculations
	one.setFragmentationRate(64); 	// 64 sample/remap of the gesture, lower=faster
  
	println(one);					// print the settings to console
	one.setVerbose(true);			// activate the verbose / debug mode
}

void draw(){
	// ...
	
	one.check();
	
	noFill(); stroke(255);
	one.draw();                   	// draw the relevant candidates
}
```

### Callbacks

```java
import de.voidplus.dollar.*;

OneDollar one;
Foo foo;

void setup(){
	// ...
	
	one = new OneDollar(this);
	foo = new Foo();
	
	one.add("circle", new Integer[] {127,141,124,140,120,139,118,139 /* ... */ });
	one.bind("circle","detected");		// global
	one.bind("circle", foo, "bar");		// local
}

void draw(){
	// ...
	
	one.check();
}

void detected(String gesture, int x, int y, int centroid_x, int centroid_y){	// global
	println("# gesture: "+gesture+" ( "+x+" / "+y+" )");
}

public class Foo {
	void bar(String gesture, int x, int y, int centroid_x, int centroid_y){ 	// local
		println("# gesture: "+gesture+" ( "+x+" / "+y+" )");
	}
}
```

## Snapshots

![Snapshot](https://raw.github.com/voidplus/OneDollar-Unistroke-Recognizer/master/reference/p5snap.png)

## License

The library is Open Source Software released under the [MIT License](https://raw.github.com/voidplus/OneDollar-Unistroke-Recognizer/master/MIT-LICENSE.txt). It's developed by [Darius Morawiec](http://voidplus.de).