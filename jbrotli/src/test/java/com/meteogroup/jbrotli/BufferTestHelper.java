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
import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

class BufferTestHelper {

  static byte[] concat(byte[] bytes1, byte[] bytes2) {
    byte[] result = new byte[bytes1.length + bytes2.length];
    System.arraycopy(bytes1, 0, result, 0, bytes1.length);
    System.arraycopy(bytes2, 0, result, bytes1.length, bytes2.length);
    return result;
  }

  static byte[] concat(ByteBuffer outPart1, ByteBuffer outPart2) {
    return concat(getByteArray(outPart1), getByteArray(outPart2));
  }

  static byte[] getByteArray(ByteBuffer byteBuffer) {
    if (byteBuffer.hasArray()) {
      return copyOfRange(byteBuffer.array(), byteBuffer.position() + byteBuffer.arrayOffset(), byteBuffer.limit() + byteBuffer.arrayOffset());
    }
    byte[] result = new byte[byteBuffer.limit() - byteBuffer.position()];
    byteBuffer.get(result);
    return result;
  }

  static ByteBuffer wrapDirect(byte[] bytes) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bytes.length);
    byteBuffer.put(bytes);
    byteBuffer.position(0);
    return byteBuffer;
  }

  static byte[] createFilledByteArray(int len, char fillChar) {
    byte[] tmpXXX = new byte[len];
    Arrays.fill(tmpXXX, (byte) fillChar);
    return tmpXXX;
  }
}
