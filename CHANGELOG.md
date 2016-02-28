
CHANGELOG jbrotli
=================

## v0.3.1 (2016-??-??)

* NEW: provide BrotliServletFilter, so you may add to your Tomcat, Jetty, etc.
* NEW: Brotli.Parameter has fluent setter-methods, for convenience
* CHG: rename BrotliStreamCompressor array methods to 'compressArray()'

## v0.3.0 (2016-01-27)

* NEW: update using brotli release 0.3.0
* NEW: StreamCompressor automatically handles input buffer size
* NEW: Added build pipeline, based on [Concourse CI](http://concourse.ci/)
* CHG: switch to use 'org.meteogroup' package, aligns with MeteoGroup's Open Source guidelines
* CHG: simplified project structure, no more parent poms, make deployment easier


## v0.2.0 (2016-01-03)

* initial public version, using brotli release 0.2.0