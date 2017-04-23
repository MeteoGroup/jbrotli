/*
 * Copyright (c) 2017 MeteoGroup Deutschland GmbH
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

package org.meteogroup.jbrotli;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.Math.min;
import static org.meteogroup.jbrotli.BrotliErrorChecker.assertBrotliOk;

public final class BrotliStreamEncoder implements Closeable, AutoCloseable {

  static {
    assertBrotliOk(initJavaFieldIdCache());
  }

  // will be used from native code to store native encoder state object
  private final long brotliEncoderStateInstanceRefID = 0;

  private final ByteBuffer outBuffer = ByteBuffer.allocateDirect(32 * 1024);

  /**
   * Uses {@link Brotli#DEFAULT_PARAMETER}
   */
  public BrotliStreamEncoder() {
    this(Brotli.DEFAULT_PARAMETER);
  }

  /**
   * @param parameter parameter to use for this compressor
   * @throws BrotliException in case of something in native code went wrong
   */
  public BrotliStreamEncoder(Brotli.Parameter parameter) throws BrotliException {
    assertBrotliOk(initNativeEncoder(parameter.getMode().mode, parameter.getQuality(), parameter.getLgwin(), parameter.getLgblock()));
  }

  /**
   * @param in input buffer
   * @return a direct baked {@link ByteBuffer} containing the compressed output
   */
  public final ByteBuffer process(ByteBuffer in) {
    int inPosition = in.position();
    int inLimit = in.limit();
    int inRemain = inLimit - inPosition;
    if (inRemain < 0)
      throw new IllegalArgumentException("The source (in) position must me smaller then the source ByteBuffer's limit.");

    ByteBuffer out;
    if (in.isDirect()) {
      out = processNative(in, inPosition, inRemain);
    } else if (in.hasArray()) {
      throw new UnsupportedOperationException("out = ByteBuffer.wrap(compressBytes(in.array(), inPosition + in.arrayOffset(), inRemain, doFlush, false));");
    } else {
      throw new UnsupportedOperationException("Not supported ByteBuffer implementation. Use either direct BB or wrapped byte arrays. You may raise an issue on GitHub too ;-)");
    }
    in.position(inLimit);
    return out;
  }

  public final ByteBuffer flush() {
    return flushNative();
  }

  /**
   * Every stream must be finished, to create byte byte meta data according to the specification.
   * If a stream is NOT finished, de-compressors are unable to parse a stream (find the end),
   * which results in an error.
   * This method also flushes the stream.
   *
   * @return the last bytes, to close the stream.
   */
  public final ByteBuffer finishStream() {
    return finishNative();
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

  private native int initNativeEncoder(int mode, int quality, int lgwin, int lgblock);

  private native int freeNativeResources();

  private native ByteBuffer processNative(ByteBuffer inByteBuffer, int inPosition, int inLength);

  private native ByteBuffer flushNative();

  private native ByteBuffer finishNative();

  private native ByteBuffer BrotliEncoderTakeOutput();

  private native boolean brotliEncoderIsFinished();

  private native boolean brotliEncoderHasMoreOutput();
}
