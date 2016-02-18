/*
 * Copyright (c) 2015 MeteoGroup Deutschland GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.meteogroup.jbrotli.performance;

import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliCompressor;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.scijava.nativelib.NativeLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

@State(Scope.Benchmark)
public class Brotli_vs_Gzip_Benchmark {

  private BrotliCompressor brotliCompressor;
  private byte[] cpHtmlData;
  private byte[] out;
  private ByteArrayOutputStream arrayOutputStream;
  private Brotli.Parameter brotliParameter;
  private ByteBuffer outByteBuffer;
  private ByteBuffer cpHtmlDataByteBuffer;
  private BrotliStreamCompressor brotliStreamCompressor;

  @Setup
  public void init() throws IOException {
    NativeLoader.loadLibrary("brotli");

    brotliParameter = new Brotli.Parameter(Brotli.Mode.GENERIC, 5, Brotli.DEFAULT_LGWIN, Brotli.DEFAULT_LGBLOCK);

    brotliCompressor = new BrotliCompressor();
    brotliStreamCompressor = new BrotliStreamCompressor(brotliParameter);

    out = new byte[24603];
    outByteBuffer = ByteBuffer.allocateDirect(24603);
    arrayOutputStream = new ByteArrayOutputStream(24603);

    cpHtmlData = loadCanterburyCorpusHtmlFile();
    cpHtmlDataByteBuffer = ByteBuffer.allocateDirect(cpHtmlData.length);
    cpHtmlDataByteBuffer.put(cpHtmlData);
  }

  private byte[] loadCanterburyCorpusHtmlFile() throws IOException {
    InputStream inputStream = ClassLoader.getSystemResourceAsStream("cp.html");
    byte[] bytes = new byte[24603];
    inputStream.read(bytes);
    return bytes;
  }

  @Benchmark
  public void brotli_compression_with_BrotliCompressor_using_byte_array() {
    int compress = brotliCompressor.compress(brotliParameter, cpHtmlData, 0, cpHtmlData.length, out, 0, out.length);
    if (!(compress > 0)) throw new AssertionError("epic fail, err: " + compress);
  }

  @Benchmark
  public void brotli_compression_with_BrotliCompressor_using_ByteBuffer() {
    cpHtmlDataByteBuffer.position(0);
    int compress = brotliCompressor.compress(brotliParameter, cpHtmlDataByteBuffer, outByteBuffer);
    if (!(compress > 0)) throw new AssertionError("epic fail, err: " + compress);
  }

  @Benchmark
  public void brotli_compression_with_BrotliStreamCompressor_using_byte_array() {
    out = brotliStreamCompressor.compressBuffer(cpHtmlData, 0, cpHtmlData.length, true);
    if (out == null) throw new AssertionError("epic fail");
  }

  @Benchmark
  public void brotli_compression_with_BrotliStreamCompressor_using_ByteBuffer() {
    cpHtmlDataByteBuffer.position(0);
    outByteBuffer = brotliStreamCompressor.compressNext(cpHtmlDataByteBuffer, true);
    if (outByteBuffer.capacity() == 0) throw new AssertionError("epic fail");
  }

  @Benchmark
  public void gzip_compression() throws IOException {
    arrayOutputStream.reset();
    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(arrayOutputStream);
    gzipOutputStream.write(cpHtmlData, 0, cpHtmlData.length);
    gzipOutputStream.close();
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(Brotli_vs_Gzip_Benchmark.class.getSimpleName())
        .forks(1)
        .warmupIterations(3)
        .measurementIterations(5)
        .build();

    new Runner(opt).run();
  }
}
