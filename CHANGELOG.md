
CHANGELOG jbrotli
=================

## v0.3.1 (2016-??-??)

* NEW: provide BrotliServletFilter, so you may add to your Tomcat, Jetty, etc.
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