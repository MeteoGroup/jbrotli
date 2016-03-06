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

class BrotliError {

  static final byte NATIVE_ERROR = -1;
  static final byte NATIVE_GET_FIELD_ID_ERROR = -2;

  static final byte COMPRESS_GetPrimitiveArrayCritical_INBUF = -10;
  static final byte COMPRESS_GetPrimitiveArrayCritical_OUTBUF = -11;
  static final byte COMPRESS_ReleasePrimitiveArrayCritical_OUTBUF = -12;
  static final byte COMPRESS_ReleasePrimitiveArrayCritical_INBUF = -13;
  static final byte COMPRESS_BrotliCompressBuffer = -14;

  static final byte COMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF = -20;
  static final byte COMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF = -21;
  static final byte COMPRESS_ByteBuffer_BrotliCompressBuffer = -22;

  static final byte DECOMPRESS_GetPrimitiveArrayCritical_INBUF = -30;
  static final byte DECOMPRESS_GetPrimitiveArrayCritical_OUTBUF = -31;
  static final byte DECOMPRESS_BROTLI_RESULT_ERROR = -32;
  static final byte DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT = -33;
  static final byte DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT = -34;
  static final byte DECOMPRESS_ReleasePrimitiveArrayCritical_OUTBUF = -35;
  static final byte DECOMPRESS_ReleasePrimitiveArrayCritical_INBUF = -36;

  static final byte DECOMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF = -40;
  static final byte DECOMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF = -41;
  static final byte DECOMPRESS_ByteBuffer_BROTLI_RESULT_ERROR = -42;
  static final byte DECOMPRESS_ByteBuffer_BROTLI_RESULT_NEEDS_MORE_INPUT = -43;
  static final byte DECOMPRESS_ByteBuffer_BROTLI_RESULT_NEEDS_MORE_OUTPUT = -44;

}
