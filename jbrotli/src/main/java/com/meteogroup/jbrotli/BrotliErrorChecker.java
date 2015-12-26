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

public class BrotliErrorChecker {

  /**
   * @param errorCode errorCode
   */
  public static boolean isBrotliOk(int errorCode) {
    return errorCode >= 0;
  }

  /**
   * @param errorCodeOrSizeInformation error code or size information
   * @throws BrotliException in case of errors
   */
  public static int assertBrotliOk(int errorCodeOrSizeInformation) throws BrotliException {
    if (!isBrotliOk(errorCodeOrSizeInformation))
      throw new BrotliException(resolveErrorCode2Message(errorCodeOrSizeInformation));
    return errorCodeOrSizeInformation;
  }

  /**
   * @param errorCode errorCode
   * @return message or null if there was no error
   */
  public static String resolveErrorCode2Message(int errorCode) {
    if (isBrotliOk(errorCode)) return null;
    String msg = " (Error code: " + errorCode + ")";
    switch (errorCode) {
      case BrotliError.NATIVE_ERROR:
        return "An error happened inside JNI function call. Maybe OOME or other issues." + msg;
      case BrotliError.DECOMPRESS_BROTLI_RESULT_ERROR:
      case BrotliError.DECOMPRESS_ByteBuffer_BROTLI_RESULT_ERROR:
        return "Decoding error, e.g. corrupt input or no memory left." + msg;
      case BrotliError.DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT:
      case BrotliError.DECOMPRESS_ByteBuffer_BROTLI_RESULT_NEEDS_MORE_INPUT:
        return "Decompression partially done, but must be invoked again with more input." + msg;
      case BrotliError.DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT:
      case BrotliError.DECOMPRESS_ByteBuffer_BROTLI_RESULT_NEEDS_MORE_OUTPUT:
        return "Decompression partially done, but must be invoked again with more output." + msg;
      case BrotliError.STREAM_COMPRESS_WriteBrotliData:
        return "WriteBrotliData returns false because the size of the input data is larger than " +
            "input_block_size() or if there was an error during writing the output." + msg;
      case BrotliError.STREAM_COMPRESS_INIT_BrotliCompressor:
        return "Error while initializing new BrotliCompressor (native) object." + msg;
      case BrotliError.COMPRESS_ByteBuffer_BrotliCompressBuffer:
        return "Error in native Brotli library 'COMPRESS_ByteBuffer_BrotliCompressBuffer'." + msg;
      case BrotliError.COMPRESS_BrotliCompressBuffer:
        return "Error in native Brotli library 'COMPRESS_BrotliCompressBuffer'. Most likely, your compress buffer (output) is too small, please make it larger. " + msg;
      case BrotliError.COMPRESS_GetPrimitiveArrayCritical_INBUF:
        return "Error in native Brotli library 'COMPRESS_GetPrimitiveArrayCritical_INBUF'." + msg;
      case BrotliError.COMPRESS_GetPrimitiveArrayCritical_OUTBUF:
        return "Error in native Brotli library 'COMPRESS_GetPrimitiveArrayCritical_OUTBUF'." + msg;
      case BrotliError.COMPRESS_ReleasePrimitiveArrayCritical_OUTBUF:
        return "Error in native Brotli library 'COMPRESS_ReleasePrimitiveArrayCritical_OUTBUF'." + msg;
      case BrotliError.COMPRESS_ReleasePrimitiveArrayCritical_INBUF:
        return "Error in native Brotli library 'COMPRESS_ReleasePrimitiveArrayCritical_INBUF'." + msg;
      case BrotliError.COMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF:
        return "Error in native Brotli library 'COMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF'." + msg;
      case BrotliError.COMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF:
        return "Error in native Brotli library 'COMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF'." + msg;
      case BrotliError.DECOMPRESS_GetPrimitiveArrayCritical_INBUF:
        return "Error in native Brotli library 'DECOMPRESS_GetPrimitiveArrayCritical_INBUF'." + msg;
      case BrotliError.DECOMPRESS_GetPrimitiveArrayCritical_OUTBUF:
        return "Error in native Brotli library 'DECOMPRESS_GetPrimitiveArrayCritical_OUTBUF'." + msg;
      case BrotliError.DECOMPRESS_ReleasePrimitiveArrayCritical_OUTBUF:
        return "Error in native Brotli library 'DECOMPRESS_ReleasePrimitiveArrayCritical_OUTBUF'." + msg;
      case BrotliError.DECOMPRESS_ReleasePrimitiveArrayCritical_INBUF:
        return "Error in native Brotli library 'DECOMPRESS_ReleasePrimitiveArrayCritical_INBUF'." + msg;
      case BrotliError.DECOMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF:
        return "Error in native Brotli library 'DECOMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF'." + msg;
      case BrotliError.DECOMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF:
        return "Error in native Brotli library 'DECOMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF'." + msg;
      case BrotliError.STREAM_COMPRESS_GetPrimitiveArrayCritical_INBUF:
        return "Error in native Brotli library 'STREAM_COMPRESS_GetPrimitiveArrayCritical_INBUF'." + msg;
      case BrotliError.STREAM_COMPRESS_GetPrimitiveArrayCritical_OUTBUF:
        return "Error in native Brotli library 'STREAM_COMPRESS_GetPrimitiveArrayCritical_OUTBUF'." + msg;
      case BrotliError.STREAM_COMPRESS_ReleasePrimitiveArrayCritical_OUTBUF:
        return "Error in native Brotli library 'STREAM_COMPRESS_ReleasePrimitiveArrayCritical_OUTBUF'." + msg;
      case BrotliError.STREAM_COMPRESS_ReleasePrimitiveArrayCritical_INBUF:
        return "Error in native Brotli library 'STREAM_COMPRESS_ReleasePrimitiveArrayCritical_INBUF'." + msg;
      case BrotliError.STREAM_COMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF:
        return "Error in native Brotli library 'STREAM_COMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF'." + msg;
      case BrotliError.STREAM_COMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF:
        return "Error in native Brotli library 'STREAM_COMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF'." + msg;
      case BrotliError.STREAM_COMPRESS_ByteBuffer_WriteBrotliData:
        return "Error in native Brotli library 'STREAM_COMPRESS_ByteBuffer_WriteBrotliData'." + msg;
      case BrotliError.NATIVE_GET_FIELD_ID_ERROR:
        return "Error in native Brotli library 'NATIVE_GET_FIELD_ID_ERROR'." + msg;
      default:
        return "Error in native Brotli library." + msg;
    }
  }

}
