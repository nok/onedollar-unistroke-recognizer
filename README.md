# OneDollar-Unistroke-Recognizer

Implementation of the [$1 Gesture Recognizer](http://depts.washington.edu/aimgroup/proj/dollar/), a two-dimensional template based gesture recognition, for [Processing](http://processing.org/).


## About

The [$1 Gesture Recognizer](http://depts.washington.edu/aimgroup/proj/dollar/) is a research project by Wobbrock, Wilson and Li of the University of Washington and Microsoft Research. It describes a simple algorithm for accurate and fast recognition of drawn gestures.

Gestures can be recognised at any position, scale, and under any rotation. The system requires little training, achieving a 97% recognition rate with only one template for each gesture.

> Wobbrock, J.O., Wilson, A.D. and Li, Y. (2007). [Gestures without libraries, toolkits or training: A $1 recognizer for user interface prototypes](http://faculty.washington.edu/wobbrock/pubs/uist-07.1.pdf). Proceedings of the ACM Symposium on User Interface Software and Technology (UIST '07). Newport, Rhode Island (October 7-10, 2007). New York: ACM Press, pp. 159-168.


## Download

* [OneDollarUnistrokeRecognizer.zip v0.2.4](https://raw.github.com/voidplus/onedollar-unistroke-recognizer/master/download/OneDollarUnistrokeRecognizer.zip)


## Installation

Unzip and put the extracted *OneDollarUnistrokeRecognizer* folder into the libraries folder of your Processing sketches. Reference and examples are included in the *OneDollarUnistrokeRecognizer* folder.


## Usage


Import the library, create the *OneDollar* object, add gestures, bind and implement callbacks. That's all, have fun!

```java
import de.voidplus.dollar.*;

OneDollar one;

void setup(){
    size(500, 500);
    background(255);
    // ...
    
    one = new OneDollar(this);
    one.add("circle", new Integer[] {127,141,124,140,120,139,118,139 /* ... */ });
    one.add("triangle", new Integer[] {137,139,135,141,133,144,132,146 /* ... */ });
    one.bind("circle triangle","detected");
}

void draw(){
    background(255);
    // ...
    
    noFill(); stroke(50);
	one.draw(); // optionally, you can draw the relevant candidates
}

void detected(String gesture, int x, int y, int centroid_x, int centroid_y){
	println("Detected gesture: "+gesture+" (Position: X: "+x+" / Y: "+y+", Centroid: X: "+centroid_x+" / Y: "+centroid_y+")");
}

void mousePressed(){ one.start(100); }  // 100 = id
void mouseDragged(){ one.update(100, mouseX, mouseY); }
void mouseReleased(){ one.end(100); }
```

For extended instructions look into the wiki: [**Usage**](https://github.com/voidplus/onedollar-unistroke-recognizer/wiki/Usage)


## Examples

* [Simple](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e0_simple/e0_simple.pde)
* [Basic](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e1_basic/e1_basic.pde)
* [Callbacks](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e2_several_callbacks/e2_several_callbacks.pde)
* [Binding](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e3_local_binding/e3_local_binding.pde)
* [Gestures](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e4_more_gestures/e4_more_gestures.pde)
* [Settings](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e5_settings/e5_settings.pde)

<!--

With dependencies:

* [LeapMotion](https://github.com/voidplus/onedollar-unistroke-recognizer/blob/master/examples/e6_leapmotion/e6_leapmotion.pde) with [https://github.com/voidplus/leap-motion-processing](https://github.com/voidplus/leap-motion-processing)

-->

## Tested

System:

* OSX
* Windows

Processing Version:

* 2.0.1
* 2.0b9
* 2.0b8
* 2.0b7
* 2.0b6
* 2.0b5
* 1.5.1




## Dependencies

None.


## Questions?

Don't be shy and feel free to contact me via [Twitter](http://twitter.voidplus.de).


## License

The library is Open Source Software released under the [MIT License](https://raw.github.com/voidplus/onedollar-unistroke-recognizer/master/MIT-LICENSE.txt). It's developed by [Darius Morawiec](http://voidplus.de).