
Guideline how to build jbrotli project
=========================================

## Building libraries manually per platform

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

```
"c:\Program Files (x86)\Microsoft Visual Studio 14.0\VC\vcvarsall.bat" x64
cd jbrotli-native/win32-x86-amd64
build.bat
```


## Prepare JNI header files

This is only needed when native method signatures change.

```bash
mvn -pl jbrotli compile
javah -v -d jbrotli-native/src/main/cpp -classpath jbrotli/target/classes org.meteogroup.jbrotli.BrotliCompressor org.meteogroup.jbrotli.BrotliDeCompressor org.meteogroup.jbrotli.BrotliStreamCompressor org.meteogroup.jbrotli.BrotliStreamDeCompressor org.meteogroup.jbrotli.BrotliError
```


## Run benchmark

Once you build everything, you're able to run the benchmark.
Example for Linux 64bit

```bash
cd jbrotli-native/linux-x86-amd64
mvn install
cd ../..
mvn -pl jbrotli install
mvn -pl jbrotli-performance package
java -jar jbrotli-performance/target/jbrotli-performance-0.2.0.jar
```


## WiP: Running the automated build pipeline

**The pipeline is still under construction**.

jbrotli comes with it's own [Continuous Integration](https://www.thoughtworks.com/continuous-integration) pipeline
based on configuration files, which describe a virtual build infrastructure.
The tools which you need to have installed on your box are [Vagrant](https://www.vagrantup.com/)
and [Virtual Box](https://www.virtualbox.org/).
The pipeline is build by using the great [Concourse CI](http://concourse.ci/) tools and ideas behind that project.

The pipeline was tested with Concourse CI v1.2.0.

#### 1. Prepare vagrant box
```
vagrant init
vagrant up
```

#### 2. Prepare 'fly' command line tool
This [fly](http://concourse.ci/fly-cli.html) tool is required to work with the pipeline.
Please follow the Concourse CI's [Getting Started Guide](http://concourse.ci/getting-started.html)
to learn how to install this tool.

#### 3. Prepare AWS S3 bucket

The pipeline artifacts are stored on Amazon S3 buckets.
You need to provide a bucket name and access credentials on your on.
The simples way to get such (for free), is to create an developer account on AWS:
https://console.aws.amazon.com/s3/home

#### 4. Upload pipeline
```
fly set-pipeline -p jbrotli -c ./pipeline.yml --var "s3-bucket-name=<YOUR_BUCKET>" --var "s3-access-key-id=<YOUR_KEY>" --var "s3-secret-access-key=<YOUR_SECRET>" --var "s3-region-name=<REGION>"
```

#### 5. Open Web UI and run pipeline
Once the pipeline is set, you can open the Web UI via http://192.168.100.4:8080/
and start the pipeline manually.

## Update version numbers

```
mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=0.4.1-SNAPSHOT -Dartifacts=jbrotli-parent,jbrotli,jbrotli-native,jbrotli-native-win32-x86-amd64,jbrotli-native-win32-x86,jbrotli-native-linux-x86-amd64,jbrotli-native-linux-arm32-vfp-hflt,jbrotli-native-darwin-x86-amd64,jbrotli-performance,jbrotli-servlet,jbrotli-servlet-examples-dropwizard,jbrotli-servlet-examples-simple-web-app,jbrotli-servlet-examples-spring-boot,jbrotli-servlet-examples -Dproperties=version.jbrotli.native
```