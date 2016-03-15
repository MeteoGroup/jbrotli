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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES_COMPRESSED;
import static org.meteogroup.jbrotli.BufferTestHelper.concat;

public class BrotliStreamDeCompressorByteArrayTest {
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
  public void decompress_with_byte_array() throws Exception {
    // setup
    byte[] out = new byte[A_BYTES.length];

    // when
    int length = decompressor.deCompress(A_BYTES_COMPRESSED, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(out).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_byte_array_with_larger_output_buffer() throws Exception {
    // setup
    byte[] out = new byte[100];

    // when
    int length = decompressor.deCompress(A_BYTES_COMPRESSED, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(out).startsWith(A_BYTES);
  }

  @Test
  public void decompress_with_byte_array_using_position_and_length_on_input() throws Exception {
    // setup
    byte[] in = BufferTestHelper.createFilledByteArray(100, 'x');
    byte[] out = new byte[100];

    // when
    int testPosition = 23;
    int testLength = A_BYTES_COMPRESSED.length;
    System.arraycopy(A_BYTES_COMPRESSED, 0, in, testPosition, testLength);

    int outLen = decompressor.deCompress(in, testPosition, testLength, out, 0, out.length);

    assertThat(outLen).isEqualTo(A_BYTES.length);
    assertThat(out).startsWith(A_BYTES);
  }

  @Test
  public void decompress_with_byte_array_using_position_and_length_on_output() throws Exception {
    // setup
    byte[] out = new byte[100];

    // when
    int testPosition = 23;
    int outLen = decompressor.deCompress(A_BYTES_COMPRESSED, 0, A_BYTES_COMPRESSED.length, out, testPosition, A_BYTES.length);

    assertThat(outLen).isEqualTo(A_BYTES.length);
    byte[] outCopiedRange = Arrays.copyOfRange(out, testPosition, testPosition + outLen);
    assertThat(outCopiedRange).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_using_multiple_input_data_fragments() throws Exception {
    // setup
    byte[] out = new byte[A_BYTES.length];
    int lengthPart1 = A_BYTES_COMPRESSED.length / 2;

    // when
    int length1 = decompressor.deCompress(A_BYTES_COMPRESSED, 0, lengthPart1, out, 0, out.length);
    assertThat(decompressor.needsMoreInput()).isTrue();

    // when
    int length2 = decompressor.deCompress(A_BYTES_COMPRESSED, lengthPart1, A_BYTES_COMPRESSED.length - lengthPart1, out, length1, out.length - length1);
    assertThat(decompressor.needsMoreInput()).isFalse();

    // then
    assertThat(length1 + length2).isEqualTo(A_BYTES.length);
    assertThat(out).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_using_multiple_output_data_fragments() throws Exception {
    // setup
    byte[] out1 = new byte[A_BYTES.length / 2];
    byte[] out2 = new byte[A_BYTES.length / 2 + A_BYTES.length % 2];

    // when
    int length1 = decompressor.deCompress(A_BYTES_COMPRESSED, 0, A_BYTES_COMPRESSED.length, out1, 0, out1.length);
    assertThat(decompressor.needsMoreOutput()).isTrue();

    // when
    int length2 = decompressor.deCompress(A_BYTES_COMPRESSED, 0, 0, out2, 0, out2.length);
    assertThat(decompressor.needsMoreOutput()).isFalse();

    // then
    assertThat(length1 + length2).isEqualTo(A_BYTES.length);
    assertThat(concat(out1, out2)).isEqualTo(A_BYTES);
  }

}