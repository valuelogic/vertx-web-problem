[![CircleCI](https://circleci.com/gh/valuelogic/vertx-web-problem/tree/master.svg?style=shield)](https://circleci.com/gh/valuelogic/vertx-web-problem/tree/master)

# Vertx web Problem handling

This small vertx-web project provides support for problem details - https://tools.ietf.org/html/rfc7807

It uses https://github.com/zalando/problem beneath.

# How to use

Add dependency in Gradle:

```groovy
dependencies {
  compile "one.valuelogic.vertx-web-problem:0.1"
}
```

or in Maven:

```xml
<dependencies>
   <dependency>
      <groupId>one.valuelogic</groupId>
      <artifactId>vertx-web-problem</artifactId>
      <version>0.1</version>
   </dependency>
</dependencies>   
```

and use it as in example: [ExampleVerticle](https://github.com/valuelogic/vertx-web-problem/blob/master/src/test/java/one/valuelogic/vertx/web/problem/ExampleVerticle.java).
 