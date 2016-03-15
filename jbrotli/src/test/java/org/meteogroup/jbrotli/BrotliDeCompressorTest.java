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

import static org.assertj.core.api.Assertions.assertThat;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES_COMPRESSED;

public class BrotliDeCompressorTest {

  private BrotliDeCompressor decompressor;

  @BeforeClass
  public void loadLibrary() throws Exception {
    BrotliLibraryLoader.loadBrotli();
  }

  @BeforeMethod
  public void setUp() throws Exception {
    decompressor = new BrotliDeCompressor();
  }

  @Test
  public void decompress_with_byte_array() throws Exception {
    byte[] in = A_BYTES_COMPRESSED;
    byte[] out = new byte[100];

    int outLen = decompressor.deCompress(in, out);

    assertThat(outLen).isEqualTo(A_BYTES.length);
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

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void using_negative_position_on_input_throws_IllegalArgumentException() throws Exception {
    byte[] in = A_BYTES_COMPRESSED;
    byte[] out = new byte[2048];

    decompressor.deCompress(in, -1, in.length, out, 0, out.length);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void using_negative_position_on_output_throws_IllegalArgumentException() throws Exception {
    byte[] in = A_BYTES_COMPRESSED;
    byte[] out = new byte[2048];

    decompressor.deCompress(in, 0, in.length, out, -1, out.length);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void using_negative_length_on_input_throws_IllegalArgumentException() throws Exception {
    byte[] in = A_BYTES_COMPRESSED;
    byte[] out = new byte[2048];

    decompressor.deCompress(in, 0, -1, out, 0, out.length);

    // expect exception
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void using_negative_length_on_output_throws_IllegalArgumentException() throws Exception {
    byte[] in = A_BYTES_COMPRESSED;
    byte[] out = new byte[2048];

    decompressor.deCompress(in, 0, in.length, out, 0, -1);

    // expect exception
  }

  //
  // *** Direct ByteBuffer ********

  @Test
  public void decompress_with_direct_ByteBuffer() throws Exception {

    ByteBuffer inBuf = ByteBuffer.allocateDirect(A_BYTES_COMPRESSED.length);
    inBuf.put(A_BYTES_COMPRESSED);
    inBuf.position(0);

    ByteBuffer outBuf = ByteBuffer.allocateDirect(100);

    // when
    int outLen = decompressor.deCompress(inBuf, outBuf);

    // then
    assertThat(outLen).isEqualTo(A_BYTES.length);
    // then
    byte[] out = new byte[A_BYTES.length];
    outBuf.get(out);
    assertThat(out).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_direct_ByteBuffer_using_position_and_length_on_input() throws Exception {
    // setup
    ByteBuffer inBuffer = ByteBuffer.allocateDirect(100);
    ByteBuffer outBuffer = ByteBuffer.allocateDirect(100);
    inBuffer.put(BufferTestHelper.createFilledByteArray(100, 'x'));

    // given
    int testPosition = 23;
    inBuffer.position(testPosition);
    inBuffer.put(A_BYTES_COMPRESSED);
    inBuffer.position(testPosition);
    inBuffer.limit(testPosition + A_BYTES_COMPRESSED.length);

    // when
    int outLen = decompressor.deCompress(inBuffer, outBuffer);

    // then
    assertThat(outLen).isEqualTo(A_BYTES.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    assertThat(outBuffer.limit()).isEqualTo(outLen);
    assertThat(inBuffer.position()).isEqualTo(testPosition + A_BYTES_COMPRESSED.length);
    // then
    byte[] buf = new byte[A_BYTES.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES);
  }

  @Test
  public void decompress_with_direct_ByteBuffer_using_position_and_length_on_output() throws Exception {
    // setup
    ByteBuffer inBuffer = ByteBuffer.allocateDirect(100);
    ByteBuffer outBuffer = ByteBuffer.allocateDirect(100);
    inBuffer.put(A_BYTES_COMPRESSED);
    inBuffer.limit(A_BYTES_COMPRESSED.length);
    inBuffer.position(0);

    // given
    int testPosition = 23;
    outBuffer.position(testPosition);
    outBuffer.limit(testPosition + A_BYTES.length);

    // when
    int outLen = decompressor.deCompress(inBuffer, outBuffer);

    // then
    assertThat(outLen).isEqualTo(A_BYTES.length);
    assertThat(outBuffer.position()).isEqualTo(testPosition);
    assertThat(outBuffer.limit()).isEqualTo(testPosition + outLen);
    assertThat(inBuffer.position()).isEqualTo(A_BYTES_COMPRESSED.length);
    // then
    byte[] buf = new byte[A_BYTES.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES);
  }

  //
  // *** byte[] wrapped ByteBuffer ********

  @Test
  public void decompress_with_byte_array_wrapped_ByteBuffer() throws Exception {

    ByteBuffer inBuf = ByteBuffer.wrap(A_BYTES_COMPRESSED);

    ByteBuffer outBuf = ByteBuffer.allocate(100);

    // when
    int outLen = decompressor.deCompress(inBuf, outBuf);

    // then
    assertThat(outLen).isEqualTo(A_BYTES.length);
    // then
    byte[] out = new byte[A_BYTES.length];
    outBuf.get(out);
    assertThat(out).isEqualTo(A_BYTES);
  }

  @Test
  public void decompress_with_byte_array_wrapped_ByteBuffer_using_position_and_length_on_input() throws Exception {
    // setup
    ByteBuffer inBuffer = ByteBuffer.wrap(BufferTestHelper.createFilledByteArray(100, 'x'));
    ByteBuffer outBuffer = ByteBuffer.allocate(100);

    // given
    int testPosition = 23;
    inBuffer.position(testPosition);
    inBuffer.put(A_BYTES_COMPRESSED);
    inBuffer.position(testPosition);
    inBuffer.limit(testPosition + A_BYTES_COMPRESSED.length);

    // when
    int outLen = decompressor.deCompress(inBuffer, outBuffer);

    // then
    assertThat(outLen).isEqualTo(A_BYTES.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    assertThat(outBuffer.limit()).isEqualTo(outLen);
    assertThat(inBuffer.position()).isEqualTo(testPosition + A_BYTES_COMPRESSED.length);
    // then
    byte[] buf = new byte[A_BYTES.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES);
  }

  @Test
  public void decompress_with_byte_array_wrapped_ByteBuffer_using_position_and_length_on_output() throws Exception {
    // setup
    ByteBuffer inBuffer = ByteBuffer.allocate(100);
    ByteBuffer outBuffer = ByteBuffer.allocate(100);
    inBuffer.put(A_BYTES_COMPRESSED);
    inBuffer.limit(A_BYTES_COMPRESSED.length);
    inBuffer.position(0);

    // given
    int testPosition = 23;
    outBuffer.position(testPosition);
    outBuffer.limit(testPosition + A_BYTES.length);

    // when
    int outLen = decompressor.deCompress(inBuffer, outBuffer);

    // then
    assertThat(outLen).isEqualTo(A_BYTES.length);
    assertThat(outBuffer.position()).isEqualTo(testPosition);
    assertThat(outBuffer.limit()).isEqualTo(testPosition + outLen);
    assertThat(inBuffer.position()).isEqualTo(A_BYTES_COMPRESSED.length);
    // then
    byte[] buf = new byte[A_BYTES.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES);
  }

  @Test
  public void decompress_with_byte_array_wrapped_ByteBuffer_using_arrayOffset_and_length_on_input() throws Exception {
    // setup
    ByteBuffer inBuffer = ByteBuffer.wrap(BufferTestHelper.createFilledByteArray(100, 'x'));
    ByteBuffer outBuffer = ByteBuffer.allocate(100);

    // given
    int testPosition = 23;
    inBuffer.position(testPosition);
    inBuffer.put(A_BYTES_COMPRESSED);
    inBuffer.position(testPosition);
    inBuffer = inBuffer.slice();
    inBuffer.limit(A_BYTES_COMPRESSED.length);

    // when
    int outLen = decompressor.deCompress(inBuffer, outBuffer);

    // then
    assertThat(outLen).isEqualTo(A_BYTES.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    assertThat(outBuffer.limit()).isEqualTo(outLen);
    assertThat(inBuffer.position()).isEqualTo(A_BYTES_COMPRESSED.length);
    // then
    byte[] buf = new byte[A_BYTES.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES);
  }

  @Test
  public void decompress_with_byte_array_wrapped_ByteBuffer_using_arrayOffset_and_length_on_output() throws Exception {
    // setup
    ByteBuffer inBuffer = ByteBuffer.allocate(100);
    ByteBuffer outBuffer = ByteBuffer.allocate(100);
    inBuffer.put(A_BYTES_COMPRESSED);
    inBuffer.limit(A_BYTES_COMPRESSED.length);
    inBuffer.position(0);

    // given
    int testPosition = 23;
    outBuffer.position(testPosition);
    outBuffer = outBuffer.slice();
    outBuffer.limit(A_BYTES.length);

    // when
    int outLen = decompressor.deCompress(inBuffer, outBuffer);

    // then
    assertThat(outLen).isEqualTo(A_BYTES.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    assertThat(outBuffer.limit()).isEqualTo(outLen);
    assertThat(inBuffer.position()).isEqualTo(A_BYTES_COMPRESSED.length);
    // then
    byte[] buf = new byte[A_BYTES.length];
    outBuffer.get(buf);
    assertThat(buf).startsWith(A_BYTES);
  }
}