
CHANGELOG jbrotli
=================

## v0.5.0 (2016-06-13)

* NEW: add example how to use jbrotli with [Apache HttpComponents](https://hc.apache.org/)
* CHG: adopt pipeline to Concourse CI 1.2.0
* CHG: using gcc v4.8 for linux build to enhance compatibility (https://github.com/MeteoGroup/jbrotli/issues/4)
* FIX: the case where Brotli doesn't consume all input (https://github.com/MeteoGroup/jbrotli/pull/3)
* FIX: fix ARM detection and library path name for Raspberry Pi binaries

## v0.4.0 (2016-03-20)

* NEW: new platform support for Raspberry Pi
* NEW: lowered the entry-barrier to Java 1.7 (also as preparation for upcoming Android support) 
* NEW: provide BrotliServletFilter, so you may add to your Tomcat, Jetty, Spring Boot, Dropwizard, etc.
* NEW: provide simple web app example for servlet filter 
* NEW: provide [Spring Boot](http://projects.spring.io/spring-boot/) web app example for servlet filter 
* NEW: provide [Dropwizard](http://dropwizard.io) web app example for servlet filter 
* NEW: Brotli.Parameter has fluent setter-methods, for convenience
* NEW: BrotliOutputStream can handle arbitrary large input buffers and will automatically take care of Brotli's input buffer window size    
* FIX: memory leak in BrotliOutputStream and BrotliInputStream
* CHG: rename BrotliStreamCompressor array methods to 'compressArray()'
* CHG: BrotliOutputStream and BrotliInputStream don't auto-flush, because this violates the AutoCloseable contract

## v0.3.0 (2016-01-27)

* NEW: update using brotli release 0.3.0
* NEW: StreamCompressor automatically handles input buffer size
* NEW: Added build pipeline, based on [Concourse CI](http://concourse.ci/)
* CHG: switch to use 'org.meteogroup' package, aligns with MeteoGroup's Open Source guidelines
* CHG: simplified project structure, no more parent poms, make deployment easier


## v0.2.0 (2016-01-03)

* initial public version, using brotli release 0.2.0