/*
 * Copyright (c) 2016 MeteoGroup Deutschland GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.meteogroup.jbrotli.io;

import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliCompressor;
import org.meteogroup.jbrotli.BufferTestHelper;
import org.scijava.nativelib.NativeLoader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meteogroup.jbrotli.Brotli.DEFAULT_PARAMETER;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES;
import static org.meteogroup.jbrotli.BrotliCompressorTest.A_BYTES_COMPRESSED;
import static org.meteogroup.jbrotli.io.BrotliInputStream.INTERNAL_UNCOMPRESSED_BUFFER_SIZE;

public class BrotliInputStreamTest {

  private BrotliInputStream brotliInputStream;
  private ByteArrayInputStream bais;

  @BeforeClass
  public void loadLibrary() throws Exception {
    NativeLoader.loadLibrary("brotli");
  }

  @BeforeMethod
  public void setUp() throws Exception {
    bais = new ByteArrayInputStream(A_BYTES_COMPRESSED);
    brotliInputStream = new BrotliInputStream(bais);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    brotliInputStream.close();
  }

  @Test
  public void byte_wise_decompression_works() throws Exception {

    byte[] uncompressedBuffer = new byte[A_BYTES.length];

    // when
    for (int i = 0; i < uncompressedBuffer.length; i++) {
      uncompressedBuffer[i] = (byte) brotliInputStream.read();
    }

    // then
    assertThat(uncompressedBuffer).isEqualTo(A_BYTES);
  }

  @Test
  public void byte_wise_decompression_signals_end_of_stream() throws Exception {
    // given
    readBrotliStreamFully();

    // when
    int read = brotliInputStream.read();

    // then
    assertThat(read).isEqualTo(-1);
  }

  @Test
  public void an_inputstream_which_wasnt_read_has_bytes_available() throws Exception {

    // when
    int available = brotliInputStream.available();

    // then
    assertThat(available).isGreaterThanOrEqualTo(1);
  }

  @Test
  public void when_end_of_inputstream_then_zero_bytes_available() throws Exception {
    // given
    readBrotliStreamFully();
    assertThat(brotliInputStream.read()).isEqualTo(-1);

    // when
    int available = brotliInputStream.available();

    // then
    assertThat(available).isEqualTo(0);
  }

  @Test
  public void read_byte_array_fully() throws Exception {
    byte[] outBuf = new byte[A_BYTES.length];
    int read = brotliInputStream.read(outBuf);

    assertThat(read).isEqualTo(A_BYTES.length);
    assertThat(outBuf).isEqualTo(A_BYTES);
  }

  @Test
  public void read_byte_array_fully_with_larger_buffer() throws Exception {
    // given
    byte[] outBuf = new byte[A_BYTES.length + 100];

    // when
    int read = brotliInputStream.read(outBuf);

    // then
    assertThat(read).isEqualTo(A_BYTES.length);
    assertThat(outBuf).startsWith(A_BYTES);

    // then
    read = brotliInputStream.read(outBuf);
    assertThat(read).isLessThanOrEqualTo(0);
  }

  @Test
  public void read_huge_byte_array_which_exceeds_internal_buffer_size() throws Exception {
    // setup
    byte[] a_bytes = BufferTestHelper.createFilledByteArray(INTERNAL_UNCOMPRESSED_BUFFER_SIZE * 2, 'a');
    byte[] a_bytes_compressed = brotliCompress(a_bytes);
    byte[] readBuffer;

    // given
    brotliInputStream = new BrotliInputStream(new ByteArrayInputStream(a_bytes_compressed));

    // when
    readBuffer = new byte[INTERNAL_UNCOMPRESSED_BUFFER_SIZE + 1];
    int read1 = brotliInputStream.read(readBuffer);

    // then
    assertThat(read1).isEqualTo(readBuffer.length);
    assertBufferContainsOnlySingleCharacter(readBuffer, 0, read1, 'a');

    // when
    readBuffer = new byte[INTERNAL_UNCOMPRESSED_BUFFER_SIZE + 1];
    int read2 = brotliInputStream.read(readBuffer);

    // then
    assertThat(read2).isEqualTo(readBuffer.length - 2);
    assertBufferContainsOnlySingleCharacter(readBuffer, 0, read2, 'a');
  }

  private void assertBufferContainsOnlySingleCharacter(byte[] buffer, int position, int length, char a) {
    for (int i = position; i < length; i++) {
      byte a_byte = buffer[i];
      String msg = String.format("Buffer position:%d should be '%s' but actually is '%s'", i, a, (char) a_byte);
      assertThat(a_byte).describedAs(msg).isEqualTo((byte) a);
    }
  }

  private byte[] brotliCompress(byte[] a_bytes) {
    BrotliCompressor brotliCompressor = new BrotliCompressor();
    byte[] a_bytes_compressed = new byte[a_bytes.length];
    int compressedLength = brotliCompressor.compress(DEFAULT_PARAMETER, a_bytes, a_bytes_compressed);
    return Arrays.copyOfRange(a_bytes_compressed, 0, compressedLength);
  }

  private void readBrotliStreamFully() throws IOException {
    for (int i = 0; i < A_BYTES.length; i++) {
      brotliInputStream.read();
    }
  }
}