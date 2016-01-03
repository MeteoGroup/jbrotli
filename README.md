
jBrotli
=========================================

Java bindings for [Brotli](https://github.com/google/brotli.git): a new compression algorithm for the internet.

##### License

[![License](https://img.shields.io/:license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

##### Status of this project

🚨 Experimental 🚀

##### Example usage - Tomcat

There's a second experiment to let a [patched Tomcat server](https://github.com/nitram509/tomcat80) make use of jbrotli.


## About Brotli

Brotli is a generic-purpose lossless compression algorithm that compresses data using a combination of a modern variant of the LZ77 algorithm,
Huffman coding and 2nd order context modeling, with a compression ratio comparable to the best currently available general-purpose compression methods.
It is similar in speed with deflate but offers more dense compression.

It was developed by Google and released in September 2015 via this blog post:
[Introducing Brotli: a new compression algorithm for the internet](http://google-opensource.blogspot.de/2015/09/introducing-brotli-new-compression.html)


## Example compression code snippet

##### Example of regular BrotliCompressor with custom dictionary

```java
NativeLoader.loadLibrary("brotli");

byte[] inBuf = "Brotli: a new compression algorithm for the internet. Now available for Java!".getBytes();
byte[] compressedBuf = new byte[2048];
BrotliCompressor compressor = new BrotliCompressor();
int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuf, compressedBuf);
```

##### Example of BrotliStreamCompressor using default dictionary
 
```java
NativeLoader.loadLibrary("brotli");

byte[] inBuf = "Brotli: a new compression algorithm for the internet. Now available for Java!".getBytes();
boolean doFlush = true;
BrotliStreamCompressor streamCompressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);
byte[] compressed = streamCompressor.compress(inBuf, doFlush);
```


## Building this library

### Requirements

* [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [CMake v3.0+](https://cmake.org/)
* C++ compiler tool chain (see below)


#### Supported platforms and architecture

As this project is under development.
The goal is to provide native libraries for Linux, Windows and OSX in 32bit and 64bit. 
Currently the following platforms and architectures are tested:

* Windows 10, 64bit
   * compiler tool chains
      * [ok] NMake 32bit+64bit
      * [ok] MS Visual Studio 2010 32bit+64bit
      * [ok] MS Visual Studio 2013 32bit+64bit
      * [ok] MS Visual Studio 2015 32bit+64bit
      * [fail] mingw 64bit
* OSX El Capitan v10.11.1
   * compiler tool chains:
      * [ok] Xcode, AppleClang 7.0.2
* Linux, Ubuntu 14.x, 64bit
   * compiler tool chains:
      * [ok] GNU CC 4.9.2


### Build native libs

The jbrotli-native Maven modules are configured to automatically be activated on your platform.
E.g. on Windows with a 64bit JDK the module 'win32-x86-amd64' will be picked up.
If you want to build the 32bit version on Windows, you also need the 32bit JDK installed
and to setup different ENV variables for your Windows SDK (or Visual Studio).
See build.bat files for more details.


#### automatically for your platform

```bash
cd jbrotli-native
mvn package
```


#### manual

Each native module contains a small build script.
E.g. for Windows 64bit, you may use this ...

```bash
cd jbrotli-native/win32-x86-amd64
build.bat
```


### Prepare JNI header files

This is only needed when native method signatures change.

```bash
mvn -pl jbrotli compile
javah -v -d jbrotli-native/src/main/cpp -classpath jbrotli/target/classes com.meteogroup.jbrotli.BrotliCompressor com.meteogroup.jbrotli.BrotliDeCompressor com.meteogroup.jbrotli.BrotliStreamCompressor com.meteogroup.jbrotli.BrotliStreamDeCompressor com.meteogroup.jbrotli.BrotliError
```

### Run benchmark

Example for Linux 64bit
```bash
cd jbrotli-native/linux-x86-amd64
mvn install
cd ../..
mvn -pl jbrotli-native install
mvn -pl jbrotli install
mvn -pl jbrotli-performance package
java -jar jbrotli-performance/target/jbrotli-performnace-0.2.0-SNAPSHOT.jar
```

## Performance benchmark results

Some results are documented in this spreadsheet
https://docs.google.com/spreadsheets/d/1y3t_NvXrD58tKCXMvNC49EtxBMQQOE_8SPQLS6c1cJo/edit?usp=sharing
