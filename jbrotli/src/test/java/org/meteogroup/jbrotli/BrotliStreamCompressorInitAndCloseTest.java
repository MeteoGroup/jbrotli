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

import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES;
import static org.assertj.core.api.Assertions.assertThat;

public class BrotliStreamCompressorInitAndCloseTest {

  private BrotliStreamCompressor compressor;

  @BeforeClass
  public void loadLibrary() throws Exception {
    NativeLoader.loadLibrary("brotli");
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
  public void max_input_buffer_size_can_be_retrieved() throws Exception {
    Brotli.Parameter parameter = Brotli.DEFAULT_PARAMETER;
    parameter.setLgblock(16);

    compressor = new BrotliStreamCompressor(parameter);

    int maxInputBufferSize = compressor.getMaxInputBufferSize();

    int computed_input_block_size_as_in_botli_encode_header_file = 1 << 16;
    assertThat(maxInputBufferSize).isEqualTo(computed_input_block_size_as_in_botli_encode_header_file);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: input array position and length must be greater than zero.")
  public void using_negative_position_on_input_throws_IllegalArgumentException() throws Exception {

    compressor.compressArray(A_BYTES, -1, 0, true);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: input array position and length must be greater than zero.")
  public void using_negative_length_on_input_throws_IllegalArgumentException() throws Exception {

    compressor.compressArray(A_BYTES, 0, -1, true);

    // expect exception
  }

  @Test(expectedExceptions = IllegalStateException.class,
      expectedExceptionsMessageRegExp = "^BrotliStreamCompressor was already closed.*")
  public void auto_close_frees_resources() throws Exception {
    // given
    BrotliStreamCompressor brotliStreamCompressor = new BrotliStreamCompressor();

    // when
    brotliStreamCompressor.close();

    // then exception
    brotliStreamCompressor.getMaxInputBufferSize();
  }

  @Test
  public void close_is_idempotent() throws Exception {
    // given
    BrotliStreamCompressor brotliStreamCompressor = new BrotliStreamCompressor();

    // when
    brotliStreamCompressor.close();
    brotliStreamCompressor.close();
    brotliStreamCompressor.close();

    // no exception ;-)
  }
}