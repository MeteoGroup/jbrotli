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

import java.nio.ByteBuffer;

import static com.meteogroup.jbrotli.BrotliErrorChecker.assertBrotliOk;

public final class BrotliCompressor {

  /**
   * @param parameter parameter
   * @param in        input
   * @param out       output
   * @return output buffer length
   * @throws BrotliException in case of something in native code went wrong
   */
  public final int compress(Brotli.Parameter parameter, byte[] in, byte[] out) throws BrotliException {
    return compress(parameter, in, 0, in.length, out, 0, out.length);
  }

  /**
   * @param parameter   parameter
   * @param in          input
   * @param inPosition  input position
   * @param inLength    input length
   * @param out         output
   * @param outPosition output position
   * @param outLength   output length
   * @return output buffer length
   * @throws BrotliException in case of something in native code went wrong
   */
  public final int compress(Brotli.Parameter parameter, byte[] in, int inPosition, int inLength, byte[] out, int outPosition, int outLength) throws BrotliException {
    if (inPosition + inLength > in.length) {
      throw new IllegalArgumentException("The input array position and length must me smaller then the source byte array's length.");
    }
    if (outPosition + outLength > out.length) {
      throw new IllegalArgumentException("The output position and length must me smaller then the source byte array's length.");
    }
    return assertBrotliOk(compressBytes(parameter.getMode().mode, parameter.getQuality(), parameter.getLgwin(), parameter.getLgblock(), in, inPosition, inLength, out, outPosition, outLength));
  }

  /**
   * One may use {@link ByteBuffer#position(int)} and {@link ByteBuffer#limit(int)} to adjust
   * how the buffers are used for reading and writing.
   * <p>&nbsp;</p>
   * There are two types of ByteBuffer: a) direct ones and b) byte array wrapped ones.
   * This implementation supports both. Both (input and output) buffer has to be of the same type.
   *
   * @param parameter parameter
   * @param in        input
   * @param out       output
   * @return output buffer length
   * @throws BrotliException in case of something in native code went wrong
   */
  public final int compress(Brotli.Parameter parameter, ByteBuffer in, ByteBuffer out) throws BrotliException {
    int inPosition = in.position();
    int inLimit = in.limit();
    int inRemain = inLimit - inPosition;
    if (inRemain <= 0)
      throw new IllegalArgumentException("The source (in) position must me smaller then the source ByteBuffer's limit.");

    int outPosition = out.position();
    int outLimit = out.limit();
    int outRemain = outLimit - outPosition;
    if (outRemain <= 0)
      throw new IllegalArgumentException("The destination (out) position must me smaller then the source ByteBuffer's limit.");

    int computedOutLength;
    if (in.isDirect() && out.isDirect()) {
      computedOutLength = assertBrotliOk(compressByteBuffer(parameter.getMode().mode, parameter.getQuality(), parameter.getLgwin(), parameter.getLgblock(), in, inPosition, inRemain, out, outPosition, outRemain));
    } else if (in.hasArray() && out.hasArray()) {
      computedOutLength = assertBrotliOk(compressBytes(parameter.getMode().mode, parameter.getQuality(), parameter.getLgwin(), parameter.getLgblock(), in.array(), inPosition + in.arrayOffset(), inRemain, out.array(), outPosition + out.arrayOffset(), outRemain));
    } else {
      throw new UnsupportedOperationException("Not supported ByteBuffer implementation. Both (input and output) buffer has to be of the same type. Use either direct BB or wrapped byte arrays. You may raise an issue on GitHub too ;-)");
    }
    in.position(inLimit);
    out.position(outPosition);
    out.limit(outPosition + computedOutLength);
    return computedOutLength;
  }

  private native static int compressBytes(int mode, int quality, int lgwin, int lgblock, byte[] inArray, int inPosition, int inLength, byte[] outArray, int outPosition, int outLength);

  private native static int compressByteBuffer(int mode, int quality, int lgwin, int lgblock, ByteBuffer inByteBuffer, int inPosition, int inLength, ByteBuffer outByteBuffer, int outPosition, int outLength);

}
