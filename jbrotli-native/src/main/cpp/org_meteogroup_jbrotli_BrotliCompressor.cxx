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

#include <brotli/encode.h>

#include "./param_converter.h"
#include "./org_meteogroup_jbrotli_BrotliError.h"


#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_meteogroup_jbrotli_BrotliCompressor
 * Method:    compressBytes
 * Signature: (III[BII[BII)I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliCompressor_compressBytes(JNIEnv *env,
                                                                               jclass thisObj,
                                                                               jint mode,
                                                                               jint quality,
                                                                               jint lgwin,
                                                                               jbyteArray inByteArray,
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

  size_t computedOutLength = outLength;

  uint8_t *inBufCritArray = (uint8_t *) env->GetPrimitiveArrayCritical(inByteArray, 0);
  if (inBufCritArray == NULL || env->ExceptionCheck()) return org_meteogroup_jbrotli_BrotliError_COMPRESS_GetPrimitiveArrayCritical_INBUF;
  uint8_t *outBufCritArray = (uint8_t *) env->GetPrimitiveArrayCritical(outByteArray, 0);
  if (outBufCritArray == NULL || env->ExceptionCheck()) return org_meteogroup_jbrotli_BrotliError_COMPRESS_GetPrimitiveArrayCritical_OUTBUF;

  BROTLI_BOOL ok = BrotliEncoderCompress(
    quality,
    lgwin,
    asBrotliEncoderMode(mode),
    inLength,
    inBufCritArray + inPosition,
    &computedOutLength,
    outBufCritArray + outPosition);

  env->ReleasePrimitiveArrayCritical(outByteArray, outBufCritArray, 0);
  if (env->ExceptionCheck()) return org_meteogroup_jbrotli_BrotliError_COMPRESS_ReleasePrimitiveArrayCritical_OUTBUF;
  env->ReleasePrimitiveArrayCritical(inByteArray, inBufCritArray, 0);
  if (env->ExceptionCheck()) return org_meteogroup_jbrotli_BrotliError_COMPRESS_ReleasePrimitiveArrayCritical_INBUF;

  if (BROTLI_FALSE == ok) {
    return org_meteogroup_jbrotli_BrotliError_COMPRESS_BrotliCompressBuffer;
  }

  return computedOutLength;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliCompressor
 * Method:    compressByteBuffer
 * Signature: (IIILjava/nio/ByteBuffer;IILjava/nio/ByteBuffer;II)I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliCompressor_compressByteBuffer(JNIEnv *env,
                                                                                    jclass thisObj,
                                                                                    jint mode,
                                                                                    jint quality,
                                                                                    jint lgwin,
                                                                                    jobject inBuf,
                                                                                    jint inPosition,
                                                                                    jint inLength,
                                                                                    jobject outBuf,
                                                                                    jint outPosition,
                                                                                    jint outLength) {

  if (inPosition < 0 || inLength < 0 ) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: input ByteBuffer position and length must be greater than zero.");
    return org_meteogroup_jbrotli_BrotliError_NATIVE_ERROR;
  }
  if (outPosition < 0 || outLength < 0) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: output ByteBuffer position and length must be greater than zero.");
    return org_meteogroup_jbrotli_BrotliError_NATIVE_ERROR;
  }

  if (inLength == 0) return 0;

  uint8_t *inBufPtr = (uint8_t *) env->GetDirectBufferAddress(inBuf);
  if (inBufPtr == NULL) return org_meteogroup_jbrotli_BrotliError_COMPRESS_ByteBuffer_GetDirectBufferAddress_INBUF;

  uint8_t *outBufPtr = (uint8_t *) env->GetDirectBufferAddress(outBuf);
  if (outBufPtr == NULL) return org_meteogroup_jbrotli_BrotliError_COMPRESS_ByteBuffer_GetDirectBufferAddress_OUTBUF;

  size_t computedOutLength = outLength;
  BROTLI_BOOL ok = BrotliEncoderCompress(
    quality, lgwin, asBrotliEncoderMode(mode),
    inLength, inBufPtr + inPosition, &computedOutLength, outBufPtr + outPosition);
  if (BROTLI_FALSE == ok) {
    return org_meteogroup_jbrotli_BrotliError_COMPRESS_ByteBuffer_BrotliCompressBuffer;
  }

  return computedOutLength;
}

#ifdef __cplusplus
}
#endif

