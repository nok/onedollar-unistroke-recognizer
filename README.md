# OneDollar-Unistroke-Recognizer

Implementation of the [$1 Gesture Recognizer](http://depts.washington.edu/aimgroup/proj/dollar/), a two-dimensional template based gesture recognition, for [Processing](http://processing.org/).


## Table of Contents

- [About](#about)
- [Download](#download)
- [Installation](#installation)
- [Tested](#tested)
- [Examples](#examples)
- [Usage](#usage)
- [Changelog](#changelog)
- [Questions?](#questions)
- [License](#license)


## About

The [$1 Gesture Recognizer](http://depts.washington.edu/aimgroup/proj/dollar/) is a research project by Wobbrock, Wilson and Li of the University of Washington and Microsoft Research. It describes a simple algorithm for accurate and fast recognition of drawn gestures.

Gestures can be recognised at any position, scale, and under any rotation. The system requires little training, achieving a 97% recognition rate with only one template for each gesture.

> Wobbrock, J.O., Wilson, A.D. and Li, Y. (2007). [Gestures without libraries, toolkits or training: A $1 recognizer for user interface prototypes](http://faculty.washington.edu/wobbrock/pubs/uist-07.1.pdf). Proceedings of the ACM Symposium on User Interface Software and Technology (UIST '07). Newport, Rhode Island (October 7-10, 2007). New York: ACM Press, pp. 159-168.


## Download

* [OneDollarUnistrokeRecognizer.zip v1.0.2](download/OneDollarUnistrokeRecognizer.zip?raw=true)

View [releases](https://github.com/nok/onedollar-unistroke-recognizer/releases) for older versions of that library.


## Installation

Unzip and put the extracted *OneDollarUnistrokeRecognizer* folder into the libraries folder of your Processing sketches. Reference and examples are included in the *OneDollarUnistrokeRecognizer* folder.


## Tested

System:

* **OS X** (*Mac OS 10.7 and higher*)
* **Windows** (*Windows 7 and 8*)
* **Linux** (*should work*)

Processing version:

* **2.2.1**
* 2.0.3
* 2.0.2
* 2.0.1
* 2.0b9
* 2.0b8
* 2.0b7

Older versions of Processing aren't supported because of [these](https://github.com/processing/processing/wiki/Library-Basics#body) changes. View [releases](https://github.com/nok/onedollar-unistroke-recognizer/releases) for older versions of that library.


## Examples

* [Simple](examples/e0_simple/e0_simple.pde)
* [Basic](examples/e1_basic/e1_basic.pde)
* [Callbacks](examples/e2_several_callbacks/e2_several_callbacks.pde)
* [Binding](examples/e3_local_binding/e3_local_binding.pde)
* [Gestures](examples/e4_more_gestures/e4_more_gestures.pde)
* [Settings](examples/e5_settings/e5_settings.pde)


## Usage

Import the library, create an instance of *OneDollar*, add gestures, define binds, implement these callbacks and track your moves. And of course, have fun. The following example shows the simplest usage of the library. Please check the [others examples](#examples) to discover more useful features.

```java
import de.voidplus.dollar.*;

OneDollar one;

void setup(){
    size(500, 500);
    background(255);
    // ...
    
    // 1. Create instance of class OneDollar:
    one = new OneDollar(this);
    // println(one);               // Print all settings
    // one.setVerbose(true);       // Activate console verbose
    
    // 2. Add gestures (templates):
    one.learn("circle", new Integer[] { 127,141 , 124,140 , 120,139 , 118,139 /* ... */ });
    one.learn("triangle", new Integer[] { 137,139 , 135,141 , 133,144 , 132,146 /* ... */ });
    one.learn("rectangle", new Integer[] { 135,141 , 133,144 , 132,146 , 132,146 /* ... */ });
    // one.forget("circle");

    // 3. Bind templates to methods (callbacks):
    one.on("circle", "foo");
    one.on("triangle rectangle", "bar");
    // one.off("circle");
}

void draw(){
    background(255);
    // ...

    // Optional draw:
    one.draw();
}

// 4. Implement callbacks:
void foo(String gestureName, float percentOfSimilarity, int startX, int startY, int centroidX, int centroidY, int endX, int endY){
    println("Gesture: " + gesture + ", " + startX + "/" +startY + ", " + centroidX + "/" +centroidY + ", " + endX + "/" +endY);
}
void bar(String gestureName, float percentOfSimilarity, int startX, int startY, int centroidX, int centroidY, int endX, int endY){
    println("Gesture: " + gesture + ", " + startX + "/" +startY + ", " + centroidX + "/" +centroidY + ", " + endX + "/" +endY);
}

// 5. Track data:
void mouseDragged(){
    one.track(mouseX, mouseY);
}
```


## Questions?

Don't be shy and feel free to contact me on Twitter: [@darius_morawiec](https://twitter.com/darius_morawiec)


## License

The library is Open Source Software released under the [License](LICENSE.txt).