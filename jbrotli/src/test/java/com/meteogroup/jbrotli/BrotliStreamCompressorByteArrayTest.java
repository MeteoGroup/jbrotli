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

package com.meteogroup.jbrotli;

import org.scijava.nativelib.NativeLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.meteogroup.jbrotli.BrotliCompressorTest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class BrotliStreamCompressorByteArrayTest {

  private BrotliStreamCompressor compressor;

  @BeforeClass
  public void loadLibrary() throws Exception {
    NativeLoader.loadLibrary("brotli");
  }

  @BeforeMethod
  public void setUp() throws Exception {
    compressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);
  }

  @Test
  public void compress_with_byte_array_and_flushing() throws Exception {
    byte[] out = compressor.compress(A_BYTES, true);

    assertThat(out).hasSize(10);
    assertThat(out).isEqualTo(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_byte_array_without_flushing() throws Exception {

    // when
    byte[] out = compressor.compress(A_BYTES, false);

    // then
    assertThat(out).hasSize(0);

    // when
    out = compressor.compress(new byte[0], true);

    // then
    assertThat(out).isEqualTo(A_BYTES_COMPRESSED);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: input array position and length must be greater than zero.")
  public void using_negative_position_throws_IllegalArgumentException() throws Exception {

    compressor.compress(A_BYTES, -1, 0, true);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: input array position and length must be greater than zero.")
  public void using_negative_length_throws_IllegalArgumentException() throws Exception {

    compressor.compress(A_BYTES, 0, -1, true);

    // expect exception
  }

  @Test
  public void compress_with_byte_array_using_position_and_length() throws Exception {
    // setup
    byte[] in = createFilledByteArray(100, 'x');

    // given
    int testPosition = 23;
    int testLength = A_BYTES.length;
    System.arraycopy(A_BYTES, 0, in, testPosition, testLength);

    // when
    byte[] out = compressor.compress(in, testPosition, testLength, true);

    // then
    assertThat(out).isEqualTo(A_BYTES_COMPRESSED);
  }

}