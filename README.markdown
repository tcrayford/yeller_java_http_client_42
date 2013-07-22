This is the java notifier library for integrating your app with the Yeller
exception notifier.

When an uncaught exception occurs, this library will record the exception in
Yeller, letting you diagnose bugs in your running app.

# Integration

Note that yeller-java is a lowlevel client. If you happen to use clojure's
ring, we have a higher level wrapper that builds on this library for your use:

## Adding to your project

yeller-java is distributed via maven. Assuming your build system is maven
compatible, you can add the following xml snippet to your pom file to import
it:

```pom.xml snippet here```

Alternatively, if you just want a jar file, you can grab it from here:
http:://yeller-java-file.com.

## Integrating

Once you've got the jar installed, you'll need to get running with a Yeller
client. This means you'll need the api key from your Yeller project (which you
can find on your project's setting screen). Once you have the api key,
you instantiate a client with it thusly:

```java
YellerClient yellerClient = new YellerClient('YOUR_API_KEY_HERE');
```

There are some more configuration options, which you can read about at http://www.yellerapp.com/docs/java-client#configuration

To report an exception, simply pass it into ```java yellerClient.report```. That's all there is to it, we report the exception to
the server. You'll be fixing your bugs in no time.

### Report your own exception

```java
try {
  // Your code here
} catch (Throwable t) {
  yellerClient.report(t);
  throw t;
}
```