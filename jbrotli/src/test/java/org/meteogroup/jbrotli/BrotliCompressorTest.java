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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.nio.ByteBuffer.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.meteogroup.jbrotli.BufferTestHelper.*;

public class BrotliCompressorTest {

  public static final byte[] A_BYTES = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes();
  public static final byte[] A_BYTES_COMPRESSED = new byte[]{27, 54, 0, 0, 36, -126, -30, -103, 64, 0};

  private BrotliCompressor compressor;

  @BeforeClass
  public void loadLibrary() throws Exception {
    BrotliLibraryLoader.loadBrotli();
  }

  @BeforeMethod
  public void setUp() throws Exception {
    compressor = new BrotliCompressor();
  }

  @Test
  public void compress_with_byte_array() throws Exception {
    byte[] out = new byte[2048];
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, A_BYTES, 0, A_BYTES.length, out, 0, out.length);

    assertThat(outLength).isEqualTo(10);
    assertThat(out).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_byte_array_using_position_and_length_on_input_array() throws Exception {
    // setup
    byte[] in = createFilledByteArray(100, 'x');
    byte[] out = new byte[2048];

    // given
    int testPosition = 23;
    int testLength = A_BYTES.length;
    System.arraycopy(A_BYTES, 0, in, testPosition, testLength);

    // when
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, in, testPosition, testLength, out, 0, out.length);

    // then
    assertThat(outLength).isEqualTo(10);
    assertThat(out).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_byte_array_using_position_and_length_on_output_array() throws Exception {
    byte[] out = new byte[2048];
    int testPosition = 23;

    // when
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, A_BYTES, 0, A_BYTES.length, out, testPosition, A_BYTES_COMPRESSED.length);

    // then
    assertThat(outLength).isEqualTo(10);
    byte[] outCopiedRange = Arrays.copyOfRange(out, testPosition, testPosition + A_BYTES_COMPRESSED.length);
    assertThat(outCopiedRange).isEqualTo(A_BYTES_COMPRESSED);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void compress_with_input_byte_array_using_negative_position_throws_IllegalArgumentException() throws Exception {
    byte[] out = new byte[2048];

    compressor.compress(Brotli.DEFAULT_PARAMETER, A_BYTES, -1, A_BYTES.length, out, 0, out.length);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void compress_with_output_byte_array_using_negative_position_throws_IllegalArgumentException() throws Exception {
    byte[] out = new byte[2048];

    compressor.compress(Brotli.DEFAULT_PARAMETER, A_BYTES, 0, A_BYTES.length, out, -1, out.length);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: input array position and length must be greater than zero.")
  public void compress_with_input_byte_array_using_negative_length_throws_IllegalArgumentException() throws Exception {
    byte[] out = new byte[2048];

    compressor.compress(Brotli.DEFAULT_PARAMETER, A_BYTES, 0, -1, out, 0, out.length);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Brotli: output array position and length must be greater than zero.")
  public void compress_with_output__byte_array_using_negative_length_throws_IllegalArgumentException() throws Exception {
    byte[] out = new byte[2048];

    compressor.compress(Brotli.DEFAULT_PARAMETER, A_BYTES, 0, A_BYTES.length, out, 0, -1);

    // expect exception
  }

  //
  // *** Direct ByteBuffer ********

  @Test
  public void compress_with_direct_ByteBuffer() throws Exception {
    ByteBuffer inBuffer = wrapDirect(A_BYTES);
    ByteBuffer outBuffer = allocateDirect(10);
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuffer, outBuffer);

    // then
    assertThat(outLength).isEqualTo(10);
    assertThat(getByteArray(outBuffer)).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_direct_ByteBuffer_using_position_and_limit_on_input_buffer() throws Exception {
    // setup
    ByteBuffer inBuffer = allocateDirect(100);
    ByteBuffer outBuffer = allocateDirect(A_BYTES_COMPRESSED.length);
    inBuffer.put(createFilledByteArray(100, 'x'));

    // given
    int testPosition = 23;
    inBuffer.position(testPosition);
    inBuffer.put(A_BYTES);
    inBuffer.limit(testPosition + A_BYTES.length);
    inBuffer.position(testPosition);

    // when
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuffer, outBuffer);

    // then
    assertThat(outLength).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    assertThat(outBuffer.limit()).isEqualTo(outLength);
    assertThat(inBuffer.position()).isEqualTo(testPosition + A_BYTES.length);
    // then
    byte[] buf = new byte[A_BYTES_COMPRESSED.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_direct_ByteBuffer_using_position_and_limit_on_output_buffer() throws Exception {
    // setup
    ByteBuffer inBuffer = allocateDirect(A_BYTES.length);
    ByteBuffer outBuffer = allocateDirect(100);
    inBuffer.put(A_BYTES);
    inBuffer.position(0);

    // given
    int testPosition = 23;
    outBuffer.position(testPosition);
    outBuffer.limit(testPosition + A_BYTES_COMPRESSED.length);

    // when
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuffer, outBuffer);

    // then
    assertThat(outLength).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.position()).isEqualTo(testPosition);
    assertThat(outBuffer.limit()).isEqualTo(testPosition + outLength);
    assertThat(inBuffer.position()).isEqualTo(A_BYTES.length);
    // then
    byte[] buf = new byte[A_BYTES_COMPRESSED.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES_COMPRESSED);
  }

  //
  // *** byte[] wrapped ByteBuffer ********

  @Test
  public void compress_with_byte_array_wrapped_ByteBuffer() throws Exception {
    ByteBuffer inBuffer = wrap(A_BYTES);
    ByteBuffer outBuffer = allocate(10);
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuffer, outBuffer);

    // then
    assertThat(outLength).isEqualTo(10);
    // then
    byte[] buf = new byte[10];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_byte_array_wrapped_ByteBuffer_using_position_and_limit_on_input_buffer() throws Exception {
    // setup
    ByteBuffer inBuffer = wrap(createFilledByteArray(100, 'x'));
    ByteBuffer outBuffer = allocate(A_BYTES_COMPRESSED.length);

    // given
    int testPosition = 23;
    inBuffer.position(testPosition);
    inBuffer.put(A_BYTES);
    inBuffer.limit(testPosition + A_BYTES.length);
    inBuffer.position(testPosition);

    // when
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuffer, outBuffer);

    // then
    assertThat(outLength).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    assertThat(outBuffer.limit()).isEqualTo(outLength);
    assertThat(inBuffer.position()).isEqualTo(testPosition + A_BYTES.length);
    // then
    byte[] buf = new byte[A_BYTES_COMPRESSED.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_byte_array_wrapped_ByteBuffer_using_arrayOffset_and_limit_on_input_buffer() throws Exception {
    // setup
    ByteBuffer inBuffer = wrap(createFilledByteArray(100, 'x'));
    ByteBuffer outBuffer = allocate(A_BYTES_COMPRESSED.length);

    // given
    int testPosition = 23;
    inBuffer.position(testPosition);
    inBuffer.put(A_BYTES);
    inBuffer.position(testPosition);
    inBuffer = inBuffer.slice();
    inBuffer.limit(A_BYTES.length);

    // when
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuffer, outBuffer);

    // then
    assertThat(outLength).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    assertThat(outBuffer.limit()).isEqualTo(outLength);
    assertThat(inBuffer.position()).isEqualTo(A_BYTES.length);
    // then
    byte[] buf = new byte[A_BYTES_COMPRESSED.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_byte_array_wrapped_ByteBuffer_using_position_and_limit_on_output_buffer() throws Exception {
    // setup
    ByteBuffer inBuffer = wrap(A_BYTES);
    ByteBuffer outBuffer = allocate(100);

    // given
    int testPosition = 23;
    outBuffer.position(testPosition);
    outBuffer.limit(testPosition + A_BYTES_COMPRESSED.length);

    // when
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuffer, outBuffer);

    // then
    assertThat(outLength).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.position()).isEqualTo(testPosition);
    assertThat(outBuffer.limit()).isEqualTo(testPosition + outLength);
    assertThat(inBuffer.position()).isEqualTo(A_BYTES.length);
    // then
    assertThat(getByteArray(outBuffer)).isEqualTo(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_byte_array_wrapped_ByteBuffer_using_arrayOffset_and_limit_on_output_buffer() throws Exception {
    // setup
    ByteBuffer inBuffer = wrap(A_BYTES);
    ByteBuffer outBuffer = allocate(100);

    // given
    int testPosition = 23;
    outBuffer.position(testPosition);
    outBuffer = outBuffer.slice();
    outBuffer.limit(A_BYTES_COMPRESSED.length);

    // when
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuffer, outBuffer);

    // then
    assertThat(outLength).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    assertThat(outBuffer.limit()).isEqualTo(outLength);
    assertThat(inBuffer.position()).isEqualTo(A_BYTES.length);
    // then
    assertThat(getByteArray(outBuffer)).isEqualTo(A_BYTES_COMPRESSED);
  }

}
