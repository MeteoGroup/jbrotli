
jBrotli
=========================================

Java bindings for [Brotli](https://github.com/google/brotli.git): a new compression algorithm for the internet.

##### License

[![License](https://img.shields.io/:license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

##### Supported operating system and architectures

jbrotli provides platform dependant bindings for Google's brotli library.
Thus each target platform which is provided here was compiled and tested.

* Windows 7 or newer, x86 64bit
* Windows 7 or newer, x86 32bit
* Linux, x86 64bit
* Raspberry Pi (Linux), ARMv6 32bit (hardware floating point)


##### News

###### 2016-03-20

    With version 0.4.0 the Raspberry PI binaries where added as supported platform.
    For easier adoption of Brotli, there is now a 'BrotliServletFilter' available.
    Have a look at 'jbrotli-servlet-examples' on how to use it in Tomcat, Spring Boot or Dropwizard.


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
    
    
##### Example patches in HTTP servers

* There's a [patched Tomcat server](https://github.com/nitram509/tomcat80) which makes use of jbrotli.
* There's a [patched Undertow server](https://github.com/nitram509/undertow) which makes use of jbrotli.


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
      <version>0.3.0</version>
    </dependency>
  </dependencies>

  <repositories>
    <repository>
      <id>bintray-nitram509-jbrotli</id>
      <url>https://api.bintray.com/maven/nitram509/jbrotli/jbrotli</url>
    </repository>
  </repositories>
```

jbrotli's pom.xml will automatically select the native library,
depending on your platform's operating system family and arch type (Java property *os.arch*).
For a list of supported platforms, look for released ```jbrotli-native-*``` artifacts at 
[jbrotli's bintray repository](https://bintray.com/nitram509/jbrotli/jbrotli#files/com/meteogroup/jbrotli).


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