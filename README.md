
jBrotli
=========================================

Java bindings for [Brotli](https://github.com/google/brotli.git): a new compression algorithm for the internet.

##### License

[![License](https://img.shields.io/:license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

##### Supported operating systems and architectures

jbrotli provides platform dependant bindings for Google's brotli library.
Thus each target platform which is provided here was compiled and tested
for the following operating systems and architectures.

* Windows 7 or newer, x86 64bit
* Windows 7 or newer, x86 32bit
* Linux, x86 64bit
* Raspberry Pi (Linux), ARMv6 32bit (hardware floating point)


##### News

###### 2016-03-20

    With version 0.4.0 the Raspberry PI binaries where added as supported platform.
    For easier adoption of Brotli, there is now a 'BrotliServletFilter' available.
    Have a look at 'jbrotli-servlet-examples' on how to use it in Tomcat, Spring Boot or Dropwizard.
    This 0.4.0 release is bundled with the LATEST (un-released) version of brotli,
    because it contains a security fix CVE-2016-1968. The latest Google brotli-release 0.3.0
    is still vulnerable to this issue.


###### 2016-03-05

    The problem with the crashing JVM is solved: it revealed itself as a memory leak in the 
    OutputStream class ;-)
    Also high throughput and concurrent benchmarks are now running perfectly fine.
    Monitoring with jConsole or jVisualVM shows that e.g. the BrotliServletFilter behaves
    on par with GZIP compression in e.g. Tomcat web server.


###### 2016-01-31

    When I did high throughput and highly concurrent benchmarks with the HTTP servers (see below),
    I observed peak memory usages over 32GByte, which killed my JVM. I've searched for memory leaks,
    but couldn't found any orphan objects after comparing heap dumps.
    Unfortunately there isn't that much documentation on memory usage available. Which makes it difficult
    to understand why there are such peaks and if these are "normal".
    
    I don't recommend to use jbrotli in projects with many and highly concurrent compression tasks.
    
    
## About Brotli

Brotli is a generic-purpose lossless compression algorithm that compresses data using a combination of a modern variant of the LZ77 algorithm,
Huffman coding and 2nd order context modeling, with a compression ratio comparable to the best currently available general-purpose compression methods.
It is similar in speed with deflate but offers more dense compression.

It was developed by Google and released in September 2015 via this blog post:
[Introducing Brotli: a new compression algorithm for the internet](http://google-opensource.blogspot.de/2015/09/introducing-brotli-new-compression.html)


## Using jbrotli

##### Maven
jbrotli releases are available at [bintray](https://bintray.com/nitram509/jbrotli/jbrotli/)

In order to use, simply add these lines to your project's pom.xml:

```xml
  <dependencies>
    <dependency>
      <groupId>org.meteogroup.jbrotli</groupId>
      <artifactId>jbrotli</artifactId>
      <version>0.4.0</version>
    </dependency>
  </dependencies>

  <repositories>
    <repository>
      <id>bintray-nitram509-jbrotli</id>
      <name>bintray</name>
      <url>http://dl.bintray.com/nitram509/jbrotli</url>
    </repository>
  </repositories>
```

jbrotli's pom.xml will automatically select the native library,
depending on your platform's operating system family and arch type (Java property *os.arch*).
For a list of supported platforms, look for released ```jbrotli-native-*``` artifacts at 
[jbrotli's bintray repository](https://bintray.com/nitram509/jbrotli/jbrotli#files/com/meteogroup/jbrotli).


##### Example enabling your Java web application

In order to use BrotliServletFilter, simply add these lines to your project's **pom.xml**

```xml
  <dependencies>
    <dependency>
      <groupId>org.meteogroup.jbrotli</groupId>
      <artifactId>jbrotli-servlet</artifactId>
      <version>0.4.0</version>
    </dependency>
  </dependencies>

  <repositories>
    <repository>
      <id>bintray-nitram509-jbrotli</id>
      <name>bintray</name>
      <url>http://dl.bintray.com/nitram509/jbrotli</url>
    </repository>
  </repositories>
```

Then finally activate the filter in your **web.xml**

```xml
  <filter>
    <filter-name>BrotliFilter</filter-name>
    <filter-class>org.meteogroup.jbrotli.servlet.BrotliServletFilter</filter-class>
  </filter>

  <filter-mapping>
    <filter-name>BrotliFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```

That's it!

More examples are available:

* [jbrotli-servlet-examples/simple-web-app](jbrotli-servlet-examples/simple-web-app) - a generic web application (deployed as WAR file)
* [jbrotli-servlet-examples/spring-boot](jbrotli-servlet-examples/spring-boot)       - a simple [Spring Boot](http://projects.spring.io/spring-boot/) based microservice
* [jbrotli-servlet-examples/dropwizard](jbrotli-servlet-examples/dropwizard)         - a simple [Dropwizard](http://www.dropwizard.io/) based microservice 


##### Example of regular BrotliCompressor with custom dictionary

```java
BrotliLibraryLoader.loadBrotli();

byte[] inBuf = "Brotli: a new compression algorithm for the internet. Now available for Java!".getBytes();
byte[] compressedBuf = new byte[2048];
BrotliCompressor compressor = new BrotliCompressor();
int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuf, compressedBuf);
```


##### Example of BrotliStreamCompressor using default dictionary
 
```java
BrotliLibraryLoader.loadBrotli();

byte[] inBuf = "Brotli: a new compression algorithm for the internet. Now available for Java!".getBytes();
boolean doFlush = true;
BrotliStreamCompressor streamCompressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);
byte[] compressed = streamCompressor.compressBuffer(inBuf, doFlush);
```


## Performance benchmark results

Some results are documented in this spreadsheet
https://docs.google.com/spreadsheets/d/1y3t_NvXrD58tKCXMvNC49EtxBMQQOE_8SPQLS6c1cJo/edit?usp=sharing


## How to build

If you want to compile & test this library yourself, please follow this [guideline](HOWTO-BUILD.md). 