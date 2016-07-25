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

import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES_COMPRESSED;

public class BrotliStreamCompressorByteArrayTest {

  private BrotliStreamCompressor compressor;

  @BeforeClass
  public void loadLibrary() throws Exception {
    BrotliLibraryLoader.loadBrotli();
  }

  @BeforeMethod
  public void setUp() throws Exception {
    compressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    compressor.close();
  }

  @Test
  public void compress_with_byte_array_and_flushing() throws Exception {
    byte[] out = compressor.compressArray(A_BYTES, true);
//    out = concat(out, compressor.finishStream());

    assertThat(out).hasSize(10);
    assertThat(out).isEqualTo(A_BYTES_COMPRESSED);
  }

  private byte[] concat(byte[] bytes1, byte[] bytes2) {
    byte[] result = new byte[bytes1.length + bytes2.length];
    System.arraycopy(bytes1, 0, result, 0, bytes1.length);
    System.arraycopy(bytes2, 0, result, bytes1.length, bytes2.length);
    return result;
  }

  @Test
  public void compress_with_byte_array_without_flushing() throws Exception {

    // when
    byte[] out = compressor.compressArray(A_BYTES, false);

    // then
    assertThat(out).hasSize(0);

    // when
    out = compressor.compressArray(new byte[0], true);

    // then
    assertThat(out).isEqualTo(A_BYTES_COMPRESSED);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: input array position and length must be greater than zero.")
  public void using_negative_position_throws_IllegalArgumentException() throws Exception {

    compressor.compressArray(A_BYTES, -1, 0, true);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: input array position and length must be greater than zero.")
  public void using_negative_length_throws_IllegalArgumentException() throws Exception {

    compressor.compressArray(A_BYTES, 0, -1, true);

    // expect exception
  }

  @Test
  public void compress_with_byte_array_using_position_and_length() throws Exception {
    // setup
    byte[] in = BufferTestHelper.createFilledByteArray(100, 'x');

    // given
    int testPosition = 23;
    int testLength = A_BYTES.length;
    System.arraycopy(A_BYTES, 0, in, testPosition, testLength);

    // when
    byte[] out = compressor.compressArray(in, testPosition, testLength, true);

    // then
    assertThat(out).isEqualTo(A_BYTES_COMPRESSED);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "BrotliStreamCompressor, input byte array length is larger than allowed input block size. Slice the input into smaller chunks.")
  public void using_larger_input_buffer_throws_exception() throws Exception {
    // given
    byte[] tmpBuffer = new byte[compressor.getMaxInputBufferSize() + 1];

    // when
    compressor.compressArray(tmpBuffer, true);

    // expected exception
  }
}