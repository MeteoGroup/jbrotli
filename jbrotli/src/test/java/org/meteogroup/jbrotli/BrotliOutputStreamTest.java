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

package org.meteogroup.jbrotli;

import org.scijava.nativelib.NativeLoader;
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
    NativeLoader.loadLibrary("brotli");
  }

  @BeforeMethod
  public void setUp() throws Exception {
    baos = new ByteArrayOutputStream();
    brotliOutputStream = new BrotliOutputStream(baos);
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
      assertThat(decompressed[i-10]).describedAs("Byte at offset=" + i).isEqualTo(testBytes[i]);
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