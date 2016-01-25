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

#include <jni.h>
#include <stdlib.h>
#include <string.h>

#include "../../../../brotli/dec/decode.h"
#include "./org_meteogroup_jbrotli_BrotliDeCompressor.h"
#include "./org_meteogroup_jbrotli_BrotliError.h"


#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_meteogroup_jbrotli_BrotliDeCompressor
 * Method:    deCompressBytes
 * Signature: ([BII[BII)I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliDeCompressor_deCompressBytes(JNIEnv *env,
                                                                                   jclass thisObj,
                                                                                   jbyteArray encodedByteArray,
                                                                                   jint inPosition,
                                                                                   jint inLength,
                                                                                   jbyteArray outByteArray,
                                                                                   jint outPosition,
                                                                                   jint outLength) {

  if (inPosition < 0 || inLength < 0) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: input array position and length must be greater than zero.");
    return org_meteogroup_jbrotli_BrotliError_NATIVE_ERROR;
  }
  if (outPosition < 0 || outLength < 0) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: output array position and length must be greater than zero.");
    return org_meteogroup_jbrotli_BrotliError_NATIVE_ERROR;
  }

  if (inLength == 0) return 0;

  uint8_t *encodedBuffer = (uint8_t *) env->GetPrimitiveArrayCritical(encodedByteArray, 0);
  if (encodedBuffer == NULL || env->ExceptionCheck()) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_GetPrimitiveArrayCritical_INBUF;
  uint8_t *outBuffer = (uint8_t *) env->GetPrimitiveArrayCritical(outByteArray, 0);
  if (outBuffer == NULL || env->ExceptionCheck()) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_GetPrimitiveArrayCritical_OUTBUF;

  size_t computedOutLength = outLength;
  BrotliResult brotliResult = BrotliDecompressBuffer(inLength, encodedBuffer + inPosition, &computedOutLength, outBuffer + outPosition);

  env->ReleasePrimitiveArrayCritical(outByteArray, outBuffer, 0);
  if (env->ExceptionCheck()) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_ReleasePrimitiveArrayCritical_OUTBUF;
  env->ReleasePrimitiveArrayCritical(encodedByteArray, encodedBuffer, 0);
  if (env->ExceptionCheck()) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_ReleasePrimitiveArrayCritical_INBUF;

  if (brotliResult == BROTLI_RESULT_ERROR) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_BROTLI_RESULT_ERROR;
  if (brotliResult == BROTLI_RESULT_NEEDS_MORE_INPUT) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT;
  if (brotliResult == BROTLI_RESULT_NEEDS_MORE_OUTPUT) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT;

  return computedOutLength;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliDeCompressor
 * Method:    deCompressByteBuffer
 * Signature: (Ljava/nio/ByteBuffer;IILjava/nio/ByteBuffer;II)I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliDeCompressor_deCompressByteBuffer(JNIEnv *env,
                                                                                        jclass thisObj,
                                                                                        jobject inBuf,
                                                                                        jint inPosition,
                                                                                        jint inLength,
                                                                                        jobject outBuf,
                                                                                        jint outPosition,
                                                                                        jint outLength) {

  if (inPosition < 0 || inLength < 0) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: input ByteBuffer position and length must be greater than zero.");
    return org_meteogroup_jbrotli_BrotliError_NATIVE_ERROR;
  }
  if (outPosition < 0 || outLength < 0) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: output ByteBuffer position and length must be greater than zero.");
    return org_meteogroup_jbrotli_BrotliError_NATIVE_ERROR;
  }

  if (inLength == 0) return 0;

  uint8_t *inBufPtr = (uint8_t *)env->GetDirectBufferAddress(inBuf);
  if (inBufPtr==NULL) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF;

  uint8_t *outBufPtr = (uint8_t *)env->GetDirectBufferAddress(outBuf);
  if (outBufPtr==NULL) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF;

  size_t computedOutLength = outLength;
  BrotliResult brotliResult = BrotliDecompressBuffer(inLength, inBufPtr + inPosition, &computedOutLength, outBufPtr + outPosition);

  if (brotliResult == BROTLI_RESULT_ERROR) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_ByteBuffer_BROTLI_RESULT_ERROR;
  if (brotliResult == BROTLI_RESULT_NEEDS_MORE_INPUT) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_ByteBuffer_BROTLI_RESULT_NEEDS_MORE_INPUT;
  if (brotliResult == BROTLI_RESULT_NEEDS_MORE_OUTPUT) return org_meteogroup_jbrotli_BrotliError_DECOMPRESS_ByteBuffer_BROTLI_RESULT_NEEDS_MORE_OUTPUT;

  return computedOutLength;
}

#ifdef __cplusplus
}
#endif

