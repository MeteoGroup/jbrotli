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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;

import static com.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES;
import static com.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES_COMPRESSED;
import static com.meteogroup.jbrotli.BufferTestHelper.*;
import static java.nio.ByteBuffer.wrap;
import static org.assertj.core.api.Assertions.assertThat;

public class BrotliStreamCompressorByteBufferTest {

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


  //
  // *** direct ByteBuffer **********

  @Test
  public void compress_with_direct_ByteBuffer_and_flushing() throws Exception {
    ByteBuffer inBuffer = ByteBuffer.allocateDirect(A_BYTES.length);
    inBuffer.put(A_BYTES);
    inBuffer.position(0);

    // when
    ByteBuffer outBuffer = compressor.compress(inBuffer, true);

    assertThat(outBuffer.capacity()).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(getByteArray(outBuffer)).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_direct_ByteBuffer_without_flushing() throws Exception {
    ByteBuffer inBuffer = ByteBuffer.allocateDirect(A_BYTES.length);
    inBuffer.put(A_BYTES);
    inBuffer.position(0);

    // when
    ByteBuffer outBuffer = compressor.compress(inBuffer, false);
    // then
    assertThat(outBuffer.capacity()).isEqualTo(0);

    // when
    outBuffer = compressor.compress(ByteBuffer.allocateDirect(0), true);
    // then
    assertThat(outBuffer.capacity()).isEqualTo(A_BYTES_COMPRESSED.length);

    // then
    assertThat(getByteArray(outBuffer)).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_direct_ByteBuffer_using_position_and_length() throws Exception {
    // setup
    ByteBuffer inBuffer = ByteBuffer.allocateDirect(100);
    inBuffer.put(createFilledByteArray(100, 'x'));

    // given
    int testPosition = 23;
    inBuffer.position(testPosition);
    inBuffer.put(A_BYTES);
    inBuffer.position(testPosition);
    inBuffer.limit(testPosition + A_BYTES.length);

    // when
    ByteBuffer outBuffer = compressor.compress(inBuffer, true);

    // then
    assertThat(outBuffer.capacity()).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.limit()).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    // then
    assertThat(getByteArray(outBuffer)).startsWith(A_BYTES_COMPRESSED);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "BrotliStreamCompressor, input ByteBuffer size is larger than allowed input block size. Slice the input into smaller chunks.")
  public void compress_with_array_direct_ByteBuffer_using_larger_input_buffer_throws_exception() throws Exception {
    // given
    ByteBuffer tmpBuffer = wrapDirect(new byte[compressor.getMaxInputBufferSize() + 1]);

    // when
    compressor.compress(tmpBuffer, true);

    // expected exception
  }

  //
  // *** array wrapped ByteBuffer **********

  @Test
  public void compress_with_array_wrapped_ByteBuffer_and_flushing() throws Exception {
    ByteBuffer inBuffer = wrap(A_BYTES);

    // when
    ByteBuffer outBuffer = compressor.compress(inBuffer, true);

    assertThat(outBuffer.capacity()).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(getByteArray(outBuffer)).isEqualTo(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_array_wrapped_ByteBuffer_without_flushing() throws Exception {
    ByteBuffer inBuffer = wrap(A_BYTES);

    // when
    ByteBuffer outBuffer = compressor.compress(inBuffer, false);
    // then
    assertThat(outBuffer.capacity()).isEqualTo(0);

    // when
    outBuffer = compressor.compress(wrap(new byte[0]), true);
    // then
    assertThat(outBuffer.capacity()).isEqualTo(A_BYTES_COMPRESSED.length);

    // then
    assertThat(getByteArray(outBuffer)).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_array_wrapped_ByteBuffer_using_position_and_length() throws Exception {
    // setup
    ByteBuffer inBuffer = wrap(createFilledByteArray(100, 'x'));

    // given
    int testPosition = 23;
    inBuffer.position(testPosition);
    inBuffer.put(A_BYTES);
    inBuffer.position(testPosition);
    inBuffer.limit(testPosition + A_BYTES.length);

    // when
    ByteBuffer outBuffer = compressor.compress(inBuffer, true);

    // then
    assertThat(outBuffer.capacity()).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.limit()).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    // then
    assertThat(getByteArray(outBuffer)).startsWith(A_BYTES_COMPRESSED);
  }

  @Test
  public void compress_with_array_wrapped_ByteBuffer_using_arrayOffset_and_length() throws Exception {
    // setup
    ByteBuffer inBuffer = wrap(createFilledByteArray(100, 'x'));

    // given
    int testPosition = 23;
    inBuffer.position(testPosition);
    inBuffer.put(A_BYTES);
    inBuffer.position(testPosition);
    inBuffer = inBuffer.slice();
    inBuffer.limit(A_BYTES.length);

    // when
    ByteBuffer outBuffer = compressor.compress(inBuffer, true);

    // then
    assertThat(outBuffer.capacity()).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.limit()).isEqualTo(A_BYTES_COMPRESSED.length);
    assertThat(outBuffer.position()).isEqualTo(0);
    // then
    assertThat(getByteArray(outBuffer)).startsWith(A_BYTES_COMPRESSED);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "BrotliStreamCompressor, input byte array length is larger than allowed input block size. Slice the input into smaller chunks.")
  public void compress_with_array_wrapped_ByteBuffer_using_larger_input_buffer_throws_exception() throws Exception {
    // given
    ByteBuffer tmpBuffer = wrap(new byte[compressor.getMaxInputBufferSize() + 1]);

    // when
    compressor.compress(tmpBuffer, true);

    // expected exception
  }

}