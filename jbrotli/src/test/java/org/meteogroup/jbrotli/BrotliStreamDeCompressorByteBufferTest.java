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

import java.nio.ByteBuffer;

import static java.nio.ByteBuffer.*;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES_COMPRESSED;
import static org.meteogroup.jbrotli.BufferTestHelper.*;

public class BrotliStreamDeCompressorByteBufferTest {
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

  //
  // *** direct ByteBuffer **********

  @Test
  public void decompress_with_direct_ByteBuffer() throws Exception {
    // given
    ByteBuffer in = wrapDirect(A_BYTES_COMPRESSED);
    ByteBuffer out = allocateDirect(A_BYTES.length);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_direct_ByteBuffer_with_larger_output_buffer() throws Exception {
    // given
    ByteBuffer in = wrapDirect(A_BYTES_COMPRESSED);
    ByteBuffer out = allocateDirect(100);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_direct_ByteBuffer_using_position_and_length_on_input() throws Exception {
    // setup
    ByteBuffer in = allocateDirect(100);
    ByteBuffer out = allocateDirect(A_BYTES.length);

    // given
    int testPosition = 23;
    in.put(createFilledByteArray(100, 'x'));
    in.position(testPosition);
    in.put(A_BYTES_COMPRESSED);
    in.position(testPosition);
    in.limit(testPosition + A_BYTES_COMPRESSED.length);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }


  @Test
  public void decompress_with_direct_ByteBuffer_using_position_and_length_on_output() throws Exception {
    // setup
    ByteBuffer in = wrapDirect(A_BYTES_COMPRESSED);
    ByteBuffer out = allocateDirect(100);

    // given
    int testPosition = 23;
    out.put(createFilledByteArray(100, 'x'));
    out.position(testPosition);
    out.limit(testPosition + A_BYTES.length);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_direct_ByteBuffer_using_multiple_input_data_fragments() throws Exception {
    // setup
    ByteBuffer out = allocateDirect(A_BYTES.length);
    int lengthPart1 = A_BYTES_COMPRESSED.length / 2;
    ByteBuffer inPart1 = wrapDirect(copyOf(A_BYTES_COMPRESSED, lengthPart1));
    ByteBuffer inPart2 = wrapDirect(copyOfRange(A_BYTES_COMPRESSED, lengthPart1, A_BYTES_COMPRESSED.length));

    // when
    int length1 = decompressor.deCompress(inPart1, out);
    assertThat(decompressor.needsMoreInput()).isTrue();

    // when
    int length2 = decompressor.deCompress(inPart2, out);
    assertThat(decompressor.needsMoreInput()).isFalse();

    // then
    assertThat(length1 + length2).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_direct_ByteBuffer_using_multiple_output_data_fragments() throws Exception {
    // setup
    ByteBuffer in = wrapDirect(A_BYTES_COMPRESSED);
    ByteBuffer outPart1 = allocateDirect(A_BYTES.length / 2);
    ByteBuffer outPart2 = allocateDirect(A_BYTES.length);

    // when
    int length1 = decompressor.deCompress(in, outPart1);
    assertThat(decompressor.needsMoreOutput()).isTrue();

    // when
    int length2 = decompressor.deCompress(in, outPart2);
    assertThat(decompressor.needsMoreOutput()).isFalse();

    // then
    assertThat(length1 + length2).isEqualTo(A_BYTES.length);
    assertThat(concat(outPart1, outPart2)).isEqualTo(A_BYTES);
  }

  //
  // *** wrapped byte array ByteBuffer **********

  @Test
  public void decompress_with_wrapped_byte_array_ByteBuffer() throws Exception {
    // given
    ByteBuffer in = wrap(A_BYTES_COMPRESSED);
    ByteBuffer out = allocate(A_BYTES.length);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_wrapped_byte_array_ByteBuffer_with_larger_output_buffer() throws Exception {
    // given
    ByteBuffer in = wrap(A_BYTES_COMPRESSED);
    ByteBuffer out = allocate(100);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_wrapped_byte_array_ByteBuffer_using_position_and_length_on_input() throws Exception {
    // setup
    ByteBuffer in = allocate(100);
    ByteBuffer out = allocate(A_BYTES.length);

    // given
    int testPosition = 23;
    in.put(createFilledByteArray(100, 'x'));
    in.position(testPosition);
    in.put(A_BYTES_COMPRESSED);
    in.position(testPosition);
    in.limit(testPosition + A_BYTES_COMPRESSED.length);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_wrapped_byte_array_ByteBuffer_using_arrayOffset_and_length_on_input() throws Exception {
    // setup
    ByteBuffer in = allocate(100);
    ByteBuffer out = allocate(A_BYTES.length);

    // given
    int testPosition = 23;
    in.put(createFilledByteArray(100, 'x'));
    in.position(testPosition);
    in.put(A_BYTES_COMPRESSED);
    in.position(testPosition);
    in = in.slice();
    in.limit(A_BYTES_COMPRESSED.length);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_wrapped_byte_array_ByteBuffer_using_position_and_length_on_output() throws Exception {
    // setup
    ByteBuffer in = wrap(A_BYTES_COMPRESSED);
    ByteBuffer out = allocate(100);

    // given
    int testPosition = 23;
    out.put(createFilledByteArray(100, 'x'));
    out.position(testPosition);
    out.limit(testPosition + A_BYTES.length);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_wrapped_byte_array_ByteBuffer_using_arrayOffset_and_length_on_output() throws Exception {
    // setup
    ByteBuffer in = wrap(A_BYTES_COMPRESSED);
    ByteBuffer out = allocate(100);

    // given
    int testPosition = 23;
    out.put(createFilledByteArray(100, 'x'));
    out.position(testPosition);
    out = out.slice();
    out.limit(A_BYTES.length);

    // when
    int length = decompressor.deCompress(in, out);

    // then
    assertThat(length).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_wrapped_byte_array_ByteBuffer_using_multiple_input_data_fragments() throws Exception {
    // setup
    ByteBuffer out = allocate(A_BYTES.length);
    int lengthPart1 = A_BYTES_COMPRESSED.length / 2;
    ByteBuffer inPart1 = wrap(copyOf(A_BYTES_COMPRESSED, lengthPart1));
    ByteBuffer inPart2 = wrap(copyOfRange(A_BYTES_COMPRESSED, lengthPart1, A_BYTES_COMPRESSED.length));

    // when
    int length1 = decompressor.deCompress(inPart1, out);
    assertThat(decompressor.needsMoreInput()).isTrue();

    // when
    int length2 = decompressor.deCompress(inPart2, out);
    assertThat(decompressor.needsMoreInput()).isFalse();

    // then
    assertThat(length1 + length2).isEqualTo(A_BYTES.length);
    assertThat(getByteArray(out)).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_wrapped_byte_array_ByteBuffer_using_multiple_output_data_fragments() throws Exception {
    // setup
    ByteBuffer in = wrap(A_BYTES_COMPRESSED);
    ByteBuffer outPart1 = allocate(A_BYTES.length / 2);
    ByteBuffer outPart2 = allocate(A_BYTES.length);

    // when
    int length1 = decompressor.deCompress(in, outPart1);
    assertThat(decompressor.needsMoreOutput()).isTrue();

    // when
    int length2 = decompressor.deCompress(in, outPart2);
    assertThat(decompressor.needsMoreOutput()).isFalse();

    // then
    assertThat(length1 + length2).isEqualTo(A_BYTES.length);
    assertThat(concat(outPart1, outPart2)).isEqualTo(A_BYTES);
  }

}