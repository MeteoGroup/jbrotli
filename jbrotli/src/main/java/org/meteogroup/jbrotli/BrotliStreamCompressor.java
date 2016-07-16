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

import java.io.Closeable;
import java.nio.ByteBuffer;

import static java.lang.Math.min;
import static org.meteogroup.jbrotli.BrotliErrorChecker.assertBrotliOk;

public final class BrotliStreamCompressor implements Closeable {

  static {
    assertBrotliOk(initJavaFieldIdCache());
  }

  // will be used from native code to store native compressor object
  private final long brotliCompressorInstanceRef = 0;

  /**
   * Uses {@link Brotli#DEFAULT_PARAMETER}
   */
  public BrotliStreamCompressor() {
    this(Brotli.DEFAULT_PARAMETER);
  }

  /**
   * @param parameter parameter to use for this compressor
   * @throws BrotliException in case of something in native code went wrong
   */
  public BrotliStreamCompressor(Brotli.Parameter parameter) throws BrotliException {
    assertBrotliOk(initBrotliCompressor(parameter.getMode().mode, parameter.getQuality(), parameter.getLgwin(), parameter.getLgblock()));
  }

  /**
   * Compress a chunk of bytes. The size of this array has to be less or equal than {@link #getMaxInputBufferSize()}.
   * @param in      input byte array
   * @param doFlush do flush
   * @return compressed byte array, never NULL, but maybe length=0
   * @throws IllegalArgumentException if e.g. wrong length was provided
   * @throws IllegalStateException    if there was an error in native code
   */
  public final byte[] compressArray(byte[] in, boolean doFlush) {
    return compressArray(in, 0, in.length, doFlush);
  }

  /**
   * Compress a chunk of bytes. The length to compress has to be less or equal than {@link #getMaxInputBufferSize()}.
   * @param in         input byte array
   * @param inPosition position to start compress from
   * @param inLength   length in byte to compress
   * @param doFlush    do flush
   * @return compressed byte array, never NULL, but maybe length=0
   * @throws IllegalArgumentException if e.g. wrong length was provided
   * @throws IllegalStateException    if there was an error in native code
   */
  public final byte[] compressArray(byte[] in, int inPosition, int inLength, boolean doFlush) {
    if (inPosition + inLength > in.length) {
      throw new IllegalArgumentException("The source position + length must me smaller then the source byte array's length.");
    }
    return compressBytes(in, inPosition, inLength, doFlush);
  }

  /**
   * Compressing larger {@link ByteBuffer}s is easier than arrays, because this method
   * automatically compresses the maximum partial buffer. For example, if you have a 10mb buffer and the
   * {@link #getInputBlockSize()} is 2mb, you can call this method
   * 5 times in total. The input {@link ByteBuffer#position(int)} will be set accordingly.
   * You may use {@link ByteBuffer#position(int)} and {@link ByteBuffer#limit(int)} to adjust
   * how the buffers are used for reading.
   *
   * @param in      input buffer
   * @param doFlush do flush
   * @return a direct baked {@link ByteBuffer} containing the compressed output
   */
  public final ByteBuffer compressNext(ByteBuffer in, boolean doFlush) {
    int inPosition = in.position();
    int inLimit = min(in.limit(), inPosition + getMaxInputBufferSize());
    int inRemain = inLimit - inPosition;
    if (inRemain < 0)
      throw new IllegalArgumentException("The source (in) position must me smaller then the source ByteBuffer's limit.");

    ByteBuffer out;
    if (in.isDirect()) {
      out = compressByteBuffer(in, inPosition, inRemain, doFlush);
    } else if (in.hasArray()) {
      out = ByteBuffer.wrap(compressBytes(in.array(), inPosition + in.arrayOffset(), inRemain, doFlush));
    } else {
      throw new UnsupportedOperationException("Not supported ByteBuffer implementation. Use either direct BB or wrapped byte arrays. You may raise an issue on GitHub too ;-)");
    }
    in.position(inLimit);
    return out;
  }

  /**
   * @return the size of the internal compression buffer window in bytes
   * @throws BrotliException
   */
  public final int getMaxInputBufferSize() throws BrotliException {
    return assertBrotliOk(getInputBlockSize());
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    close();
  }

  @Override
  public void close() throws BrotliException {
    assertBrotliOk(freeNativeResources());
  }

  private native static int initJavaFieldIdCache();

  private native int getInputBlockSize();

  private native int initBrotliCompressor(int mode, int quality, int lgwin, int lgblock);

  private native int freeNativeResources();

  private native byte[] compressBytes(byte[] inArray, int inPosition, int inLength, boolean doFlush);

  private native ByteBuffer compressByteBuffer(ByteBuffer inByteBuffer, int inPosition, int inLength, boolean doFlush);

}
