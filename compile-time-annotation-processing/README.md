This is an addition to:

http://stackoverflow.com/questions/6967514/maven-example-of-annotation-preprocessing-and-generation-of-classes-in-same-comp

It demonstrates how to do compile-time annotation processing using an annotation processor in source code form without packaging the annotation processor first, ie. to compile the processor and use it to generate source code which is compiled in the same maven run.

The trick is simply to define multiple maven compiler executions.
