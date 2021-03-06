[![CircleCI](https://circleci.com/gh/valuelogic/vertx-web-problem/tree/master.svg?style=shield&circle-token=b1d5f5fd7022a835d2080978854d6d9ba1991c2e)](https://circleci.com/gh/valuelogic/vertx-web-problem/tree/master)

# Vertx web Problem handling

This small vertx-web project provides support for problem details - https://tools.ietf.org/html/rfc7807

It uses [Zalando Problem](https://github.com/zalando/problem) beneath.

# How to use

Add dependency in Gradle:

```groovy
dependencies {
  compile "one.valuelogic.vertx-web-problem:{version}"
}
```

or in Maven:

```xml
<dependencies>
   <dependency>
      <groupId>one.valuelogic</groupId>
      <artifactId>vertx-web-problem</artifactId>
      <version>{version}</version>
   </dependency>
</dependencies>   
```

and use it as in example: [ExampleVerticle](https://github.com/valuelogic/vertx-web-problem/blob/master/src/test/java/one/valuelogic/vertx/web/problem/ExampleVerticle.java).
 