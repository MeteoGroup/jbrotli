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

public class BrotliStreamDeCompressorInitAndCloseTest {
  private BrotliStreamDeCompressor decompressor;

  @BeforeClass
  public void loadLibrary() throws Exception {
    BrotliLibraryLoader.loadBrotli();
  }

  @BeforeMethod
  public void setUp() throws Exception {
    decompressor = new BrotliStreamDeCompressor();
  }

  @AfterMethod
  public void tearDown() throws Exception {
    decompressor.close();
  }

  @Test
  public void happy_path_decompress_a_buffer_completly() throws Exception {
    byte out[] = new byte[BrotliCompressorTest.A_BYTES.length];

    DeCompressorResult result = decompressor.deCompress(BrotliCompressorTest.A_BYTES_COMPRESSED, out);

    assertThat(result.bytesProduced).isEqualTo(BrotliCompressorTest.A_BYTES.length);
    assertThat(out).isEqualTo(BrotliCompressorTest.A_BYTES);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: input array position and length must be greater than zero.")
  public void using_negative_position_on_input_throws_IllegalArgumentException() throws Exception {

    decompressor.deCompress(BrotliCompressorTest.A_BYTES_COMPRESSED, -1, 0, new byte[100], 0, 100);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: output array position and length must be greater than zero.")
  public void using_negative_position_on_output_throws_IllegalArgumentException() throws Exception {

    decompressor.deCompress(BrotliCompressorTest.A_BYTES_COMPRESSED, 0, 10, new byte[100], -1, 100);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: input array position and length must be greater than zero.")
  public void using_negative_length_on_input_throws_IllegalArgumentException() throws Exception {

    decompressor.deCompress(BrotliCompressorTest.A_BYTES_COMPRESSED, 0, -1, new byte[100], 0, 100);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: output array position and length must be greater than zero.")
  public void using_negative_length_on_output_throws_IllegalArgumentException() throws Exception {

    decompressor.deCompress(BrotliCompressorTest.A_BYTES_COMPRESSED, 0, 10, new byte[100], 0, -1);

    // expect exception
  }

  @Test(expectedExceptions = IllegalStateException.class,
      expectedExceptionsMessageRegExp = "^BrotliStreamDeCompressor wasn't initialised.*")
  public void auto_close_frees_resources() throws Exception {
    // given
    BrotliStreamDeCompressor brotliStreamDeCompressor = new BrotliStreamDeCompressor();

    // when
    brotliStreamDeCompressor.close();

    // then exception
    brotliStreamDeCompressor.deCompress(BrotliCompressorTest.A_BYTES_COMPRESSED, new byte[100]);
  }

  @Test
  public void close_is_idempotent() throws Exception {
    // given
    BrotliStreamDeCompressor brotliStreamDeCompressor = new BrotliStreamDeCompressor();

    // when
    brotliStreamDeCompressor.close();
    brotliStreamDeCompressor.close();
    brotliStreamDeCompressor.close();

    // no exception ;-)
  }
}
