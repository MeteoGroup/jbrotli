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

public class BrotliError {

  public static final int NATIVE_ERROR = -1;
  public static final int NATIVE_GET_FIELD_ID_ERROR = -2;

  public static final int COMPRESS_GetPrimitiveArrayCritical_INBUF = -10;
  public static final int COMPRESS_GetPrimitiveArrayCritical_OUTBUF = -11;
  public static final int COMPRESS_ReleasePrimitiveArrayCritical_OUTBUF = -12;
  public static final int COMPRESS_ReleasePrimitiveArrayCritical_INBUF = -13;
  public static final int COMPRESS_BrotliCompressBuffer = -14;

  public static final int COMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF = -20;
  public static final int COMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF = -21;
  public static final int COMPRESS_ByteBuffer_BrotliCompressBuffer = -22;

  public static final int DECOMPRESS_GetPrimitiveArrayCritical_INBUF = -30;
  public static final int DECOMPRESS_GetPrimitiveArrayCritical_OUTBUF = -31;
  public static final int DECOMPRESS_BROTLI_RESULT_ERROR = -32;
  public static final int DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT = -33;
  public static final int DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT = -34;
  public static final int DECOMPRESS_ReleasePrimitiveArrayCritical_OUTBUF = -35;
  public static final int DECOMPRESS_ReleasePrimitiveArrayCritical_INBUF = -36;

  public static final int DECOMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF = -40;
  public static final int DECOMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF = -41;
  public static final int DECOMPRESS_ByteBuffer_BROTLI_RESULT_ERROR = -42;
  public static final int DECOMPRESS_ByteBuffer_BROTLI_RESULT_NEEDS_MORE_INPUT = -43;
  public static final int DECOMPRESS_ByteBuffer_BROTLI_RESULT_NEEDS_MORE_OUTPUT = -44;

  public static final int STREAM_COMPRESS_INIT_BrotliCompressor = -50;

  public static final int STREAM_COMPRESS_GetPrimitiveArrayCritical_INBUF = -60;
  public static final int STREAM_COMPRESS_GetPrimitiveArrayCritical_OUTBUF = -61;
  public static final int STREAM_COMPRESS_ReleasePrimitiveArrayCritical_OUTBUF = -63;
  public static final int STREAM_COMPRESS_ReleasePrimitiveArrayCritical_INBUF = -64;
  public static final int STREAM_COMPRESS_WriteBrotliData = -62;

  public static final int STREAM_COMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF = -70;
  public static final int STREAM_COMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF = -71;
  public static final int STREAM_COMPRESS_ByteBuffer_WriteBrotliData = -72;

}
