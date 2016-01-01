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

public final class BrotliDeCompressor {

  /**
   * @param in  compressed input
   * @param out output buffer
   * @return output buffer length
   * @throws BrotliException
   */
  public final int deCompress(byte[] in, byte[] out) throws BrotliException {
    return deCompress(in, 0, in.length, out, 0, out.length);
  }

  /**
   * @param in          compressed input
   * @param inPosition  input position
   * @param inLength    input length
   * @param out         output buffer
   * @param outPosition output position
   * @param outLength   output length
   * @return output buffer length
   * @throws BrotliException
   */
  public final int deCompress(byte[] in, int inPosition, int inLength, byte[] out, int outPosition, int outLength) throws BrotliException {
    if (inPosition + inLength > in.length) {
      throw new IllegalArgumentException("The input array position and length must me smaller then the source byte array's length.");
    }
    if (outPosition + outLength > out.length) {
      throw new IllegalArgumentException("The output array position and length must me smaller then the source byte array's length.");
    }
    return assertBrotliOk(deCompressBytes(in, inPosition, inLength, out, outPosition, outLength));
  }

  /**
   * One may use {@link ByteBuffer#position(int)} and {@link ByteBuffer#limit(int)} to adjust
   * how the buffers are used for reading and writing.
   *
   * @param in  compressed input
   * @param out output buffer
   * @return output buffer length
   * @throws BrotliException
   */
  public final int deCompress(ByteBuffer in, ByteBuffer out) throws BrotliException {
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

    int outLength;
    if (in.isDirect() && out.isDirect()) {
      outLength = assertBrotliOk(deCompressByteBuffer(in, inPosition, inRemain, out, outPosition, outRemain));
    } else if (in.hasArray() && out.hasArray()) {
      outLength = assertBrotliOk(deCompressBytes(in.array(), inPosition + in.arrayOffset(), inRemain, out.array(), outPosition + out.arrayOffset(), outRemain));
    } else {
      throw new UnsupportedOperationException("Not supported ByteBuffer implementation. Both (input and output) buffer has to be of the same type. Use either direct BB or wrapped byte arrays. You may raise an issue on GitHub too ;-)");
    }
    in.position(inLimit);
    out.limit(outPosition + outLength);
    return outLength;
  }

  private native static int deCompressBytes(byte[] in, int inPosition, int inLength, byte[] out, int outPosition, int outLength);

  private native static int deCompressByteBuffer(ByteBuffer inBuf, int inPosition, int inLength, ByteBuffer outBuf, int outPosition, int outLength);

}
