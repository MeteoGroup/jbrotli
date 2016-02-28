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
package org.meteogroup.jbrotli.servlet;


import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliDeCompressor;
import org.meteogroup.jbrotli.io.BrotliOutputStream;
import org.scijava.nativelib.NativeLoader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BrotliServletOutputStreamTest {
  private byte[] testBytes;
  private BrotliServletOutputStream brotliOutputStream;
  private ByteArrayOutputStream baos;

  @BeforeClass
  public void loadLibrary() throws Exception {
    NativeLoader.loadLibrary("brotli");
  }

  @BeforeMethod
  public void setUp() throws Exception {
    baos = new ByteArrayOutputStream();
    brotliOutputStream = new BrotliServletOutputStream(baos);
    createTestBytes();
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
      brotliOutputStream = new BrotliServletOutputStream(baos, Brotli.DEFAULT_PARAMETER.setQuality(11));
      brotliOutputStream.close();
    }
  }

  private byte[] decompress(byte[] compressed, int upackedLength) {
    BrotliDeCompressor brotliDeCompressor = new BrotliDeCompressor();
    byte[] decompressed = new byte[upackedLength];
    brotliDeCompressor.deCompress(compressed, decompressed);
    return decompressed;
  }

  private void createTestBytes() {
    testBytes = new byte[256];
    int idx = 0;
    for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
      testBytes[idx++] = b;
    }
  }
}