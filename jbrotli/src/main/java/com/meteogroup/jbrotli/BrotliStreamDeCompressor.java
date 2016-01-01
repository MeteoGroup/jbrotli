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

import java.io.Closeable;
import java.nio.ByteBuffer;

import static com.meteogroup.jbrotli.BrotliError.DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT;
import static com.meteogroup.jbrotli.BrotliError.DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT;
import static com.meteogroup.jbrotli.BrotliErrorChecker.assertBrotliOk;

public final class BrotliStreamDeCompressor implements Closeable {

  static {
    assertBrotliOk(initJavaFieldIdCache());
  }

  // will be used from native code to store native decompressor state object
  private final long brotliDeCompressorState = 0;

  private int lastErrorCode = 0;

  /**
   * @throws BrotliException
   */
  public BrotliStreamDeCompressor() throws BrotliException {
    assertBrotliOk(initBrotliDeCompressor());
  }

  /**
   * @param in  input byte array
   * @param out output byte array
   */
  public final int deCompress(byte[] in, byte[] out) {
    return deCompress(in, 0, in.length, out, 0, out.length);
  }

  /**
   * @param in         input byte array
   * @param inPosition position to start decompress from
   * @param inLength   length in byte to decompress
   * @return decompressed byte array
   */
  public final int deCompress(byte[] in, int inPosition, int inLength, byte[] out, int outPosition, int outLength) throws BrotliException {
    if (inPosition + inLength > in.length) {
      throw new IllegalArgumentException("The source position + length must me smaller then the source byte array's length.");
    }
    long errorCodeOrSizeInformation = deCompressBytes(in, inPosition, inLength, out, outPosition, outLength);

    lastErrorCode = extractErrorCode(errorCodeOrSizeInformation);
    if (lastErrorCode == DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT || lastErrorCode == DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT) {
      return extractSizeInformation(errorCodeOrSizeInformation);
    }
    return assertBrotliOk(extractSizeInformation(errorCodeOrSizeInformation));
  }

  /**
   * @return true, if decompressor needs more input bytes to decompress
   */
  public boolean needsMoreInput() {
    return lastErrorCode == DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT;
  }

  /**
   * @return true, if decompressor needs more output buffer to store decompressed bytes
   */
  public boolean needsMoreOutput() {
    return lastErrorCode == DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT;
  }

  /**
   * One may use {@link ByteBuffer#position(int)} and {@link ByteBuffer#limit(int)} to adjust
   * how the buffers are used for reading and writing.
   *
   * @param in  input buffer
   * @param out output buffer
   */
  public final int deCompress(ByteBuffer in, ByteBuffer out) throws BrotliException {
    int inPosition = in.position();
    int inLimit = in.limit();
    int inRemain = inLimit - inPosition;
    if (inRemain < 0)
      throw new IllegalArgumentException("The source (in) position must me smaller then the source ByteBuffer's limit.");

    int outPosition = out.position();
    int outRemain = out.limit() - outPosition;
    if (outRemain < 0)
      throw new IllegalArgumentException("The destination (out) position must me smaller then the destination ByteBuffer's limit.");

    long errorCodeOrSizeInformation = 0;
    if (in.isDirect() && out.isDirect()) {
      errorCodeOrSizeInformation = deCompressByteBuffer(in, inPosition, inRemain, out, outPosition, outRemain);
    } else if (in.hasArray() && out.hasArray()) {
      errorCodeOrSizeInformation = deCompressBytes(in.array(), inPosition + in.arrayOffset(), inRemain, out.array(), outPosition + out.arrayOffset(), outRemain);
    } else {
      throw new UnsupportedOperationException("Not supported ByteBuffer implementation. Both (input and output) buffer has to be of the same type. Use either direct BB or wrapped byte arrays. You may raise an issue on GitHub too ;-)");
    }

    lastErrorCode = extractErrorCode(errorCodeOrSizeInformation);
    switch (lastErrorCode) {
      case DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT:
        break;
      case DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT:
        out.limit(outPosition + extractSizeInformation(errorCodeOrSizeInformation));
        break;
      default:
        out.limit(outPosition + extractSizeInformation(errorCodeOrSizeInformation));
        assertBrotliOk(lastErrorCode);
        break;
    }
    in.position(inLimit);
    return extractSizeInformation(errorCodeOrSizeInformation);
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    close();
  }

  @Override
  public void close() throws BrotliException {
    lastErrorCode = 0;
    assertBrotliOk(freeNativeResources());
  }

  private static byte extractErrorCode(long errorCodeOrSizeInformation) {
    return (byte) ((errorCodeOrSizeInformation & 0xff00000000000000L) >> 56);
  }

  private static int extractSizeInformation(long errorCodeOrSizeInformation) {
    return ((int) (errorCodeOrSizeInformation & 0x00000000ffffffffL));
  }

  private native static int initJavaFieldIdCache();

  private native int initBrotliDeCompressor();

  private native int freeNativeResources();

  private native long deCompressBytes(byte[] inArray, int inPosition, int inLength, byte[] outArray, int outPosition, int outLength);

  private native long deCompressByteBuffer(ByteBuffer inByteBuffer, int inPosition, int inLength, ByteBuffer outByteBuffer, int outPosition, int outLength);
}
