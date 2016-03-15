/*
 * Copyright (c) 2016 MeteoGroup Deutschland GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.meteogroup.jbrotli.io;

import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliDeCompressor;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class BrotliOutputStreamTest {

  private byte[] testBytes;
  private BrotliOutputStream brotliOutputStream;
  private ByteArrayOutputStream baos;

  @BeforeClass
  public void loadLibrary() throws Exception {
    BrotliLibraryLoader.loadBrotli();
  }

  @BeforeMethod
  public void setUp() throws Exception {
    baos = new ByteArrayOutputStream();
    brotliOutputStream = new BrotliOutputStream(baos);
    createSmallTestBytes();
  }

  @AfterMethod
  public void tearDown() throws Exception {
    brotliOutputStream.close();
  }

  @Test
  public void byte_wise_compression_works() throws Exception {

    // when
    for (int i : testBytes) {
      brotliOutputStream.write(i);
    }
    brotliOutputStream.flush();

    // then
    assertThat(decompress(baos.toByteArray(), testBytes.length)).isEqualTo(testBytes);
  }

  @Test
  public void byte_array_wise_compression_works() throws Exception {

    // when
    brotliOutputStream.write(testBytes);
    brotliOutputStream.flush();

    // then
    assertThat(decompress(baos.toByteArray(), testBytes.length)).isEqualTo(testBytes);
  }

  @Test
  public void big_byte_array_gets_compressed_when_underlying_brotli_compressor_is_smaller() throws Exception {
    Brotli.Parameter parameter = Brotli.DEFAULT_PARAMETER.setQuality(3);
    brotliOutputStream = new BrotliOutputStream(baos, parameter);
    createBigTestBytes();
    assertCompressorHasSmallerBuffer(parameter);

    // when
    brotliOutputStream.write(testBytes);
    brotliOutputStream.flush();

    // then
    assertThat(decompress(baos.toByteArray(), testBytes.length)).isEqualTo(testBytes);
  }

  @Test
  public void byte_array_length_and_offset_wise_compression_works() throws Exception {

    // when
    brotliOutputStream.write(testBytes, 10, 100);
    brotliOutputStream.flush();

    // then
    byte[] decompressed = decompress(baos.toByteArray(), testBytes.length);
    for (int i = 10; i < 100; i++)
      assertThat(decompressed[i - 10]).describedAs("Byte at offset=" + i).isEqualTo(testBytes[i]);
  }

  @Test
  public void naive_test_to_make_sure_there_is_no_memory_leak_on_close_method() throws Exception {
    // constructing a new native object with highest quality allocs lots of megabytes.
    // assuming there's a mem-leak, looping will alloc so much mem, that the jvm crashes
    for (int i = 0; i < 65536; i++) {
      brotliOutputStream = new BrotliOutputStream(baos, Brotli.DEFAULT_PARAMETER.setQuality(11));
      brotliOutputStream.close();
    }
  }

  private void assertCompressorHasSmallerBuffer(Brotli.Parameter parameter) {
    try (BrotliStreamCompressor aCompressor = new BrotliStreamCompressor(parameter)) {
      assertThat(aCompressor.getMaxInputBufferSize()).isLessThan(testBytes.length);
    }
  }

  private byte[] decompress(byte[] compressed, int upackedLength) {
    BrotliDeCompressor brotliDeCompressor = new BrotliDeCompressor();
    byte[] decompressed = new byte[upackedLength];
    brotliDeCompressor.deCompress(compressed, decompressed);
    return decompressed;
  }

  private void createSmallTestBytes() {
    testBytes = new byte[256];
    int idx = 0;
    for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
      testBytes[idx++] = b;
    }
  }

  private void createBigTestBytes() {
    testBytes = new byte[65535];
    int idx = 0;
    for (short b = Short.MIN_VALUE; b < Short.MAX_VALUE; b++) {
      testBytes[idx++] = (byte) b;
    }
  }

}