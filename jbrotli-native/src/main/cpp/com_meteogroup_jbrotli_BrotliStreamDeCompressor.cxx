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


/* exporting methods */
#if (__GNUC__ >= 4) || (__GNUC__ == 3 && __GNUC_MINOR__ >= 4)
#  ifndef GCC_HASCLASSVISIBILITY
#    define GCC_HASCLASSVISIBILITY
#  endif
#endif

/* Deal with Apple's deprecated 'AssertMacros.h' from Carbon-framework */
#if defined(__APPLE__) && !defined(__ASSERT_MACROS_DEFINE_VERSIONS_WITHOUT_UNDERSCORES)
# define __ASSERT_MACROS_DEFINE_VERSIONS_WITHOUT_UNDERSCORES 0
#endif

/* Intel's compiler complains if a variable which was never initialised is
 * cast to void, which is a common idiom which we use to indicate that we
 * are aware a variable isn't used.  So we just silence that warning.
 * See: https://github.com/swig/swig/issues/192 for more discussion.
 */
#ifdef __INTEL_COMPILER
# pragma warning disable 592
#endif

/* Fix for jlong on some versions of gcc on Windows */
#if defined(__GNUC__) && !defined(__INTEL_COMPILER)
typedef long long __int64;
#endif

/* Fix for jlong on 64-bit x86 Solaris */
#if defined(__x86_64)
# ifdef _LP64
#   undef _LP64
# endif
#endif

#include <jni.h>
#include <stdlib.h>
#include <string.h>

#include "../../../../brotli/dec/decode.h"
#include "./type_converters.h"
#include "./com_meteogroup_jbrotli_BrotliError.h"


#ifdef __cplusplus
extern "C" {
#endif

static jfieldID brotliDeCompressorStateRefId;

/*
 * Class:     com_meteogroup_jbrotli_BrotliStreamDeCompressor
 * Method:    initJavaFieldIdCache
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_meteogroup_jbrotli_BrotliStreamDeCompressor_initJavaFieldIdCache(JNIEnv *env,
                                                                                              jclass cls) {
  brotliDeCompressorStateRefId = env->GetFieldID(cls, "brotliDeCompressorState", "J");
  if (NULL == brotliDeCompressorStateRefId) {
    return com_meteogroup_jbrotli_BrotliError_NATIVE_GET_FIELD_ID_ERROR;
  }
  return 0;
}


//  BrotliState s;
//  BrotliStateInit(&s);


/*
 * Class:     com_meteogroup_jbrotli_BrotliStreamDeCompressor
 * Method:    initBrotliDeCompressor
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_meteogroup_jbrotli_BrotliStreamDeCompressor_initBrotliDeCompressor(JNIEnv *env,
                                                                                                jobject thisObj) {
  BrotliState *brotliState = (BrotliState*) GetLongFieldAsPointer(env, thisObj, brotliDeCompressorStateRefId);
  if (NULL != brotliState) {
    BrotliStateCleanup(brotliState);
  }
  brotliState = new BrotliState;
  BrotliStateInit(brotliState);
  SetLongFieldFromPointer(env, thisObj, brotliDeCompressorStateRefId, brotliState);
  return 0;
}

/*
 * Class:     com_meteogroup_jbrotli_BrotliStreamDeCompressor
 * Method:    freeNativeResources
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_meteogroup_jbrotli_BrotliStreamDeCompressor_freeNativeResources(JNIEnv *env,
                                                                                             jobject thisObj) {
  BrotliState *brotliState = (BrotliState*) GetLongFieldAsPointer(env, thisObj, brotliDeCompressorStateRefId);
  if (NULL != brotliState) {
    BrotliStateCleanup(brotliState);
    brotliState = NULL;
    SetLongFieldFromPointer(env, thisObj, brotliDeCompressorStateRefId, brotliState);
  }
  return 0;
}

/*
 * Class:     com_meteogroup_jbrotli_BrotliStreamDeCompressor
 * Method:    deCompressBytes
 * Signature: ([BII[BII)L
 */
JNIEXPORT jlong JNICALL Java_com_meteogroup_jbrotli_BrotliStreamDeCompressor_deCompressBytes(JNIEnv *env,
                                                                                             jobject thisObj,
                                                                                             jbyteArray inByteArray,
                                                                                             jint inPosition,
                                                                                             jint inLength,
                                                                                             jbyteArray outByteArray,
                                                                                             jint outPosition,
                                                                                             jint outLength) {

  if (inPosition < 0 || inLength < 0 ) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: input array position and length must be greater than zero.");
    return NULL;
  }
  if (outPosition < 0 || outLength < 0 ) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: output array position and length must be greater than zero.");
    return NULL;
  }

  if (inLength == 0) return 0;

  BrotliState *brotliState = (BrotliState*) GetLongFieldAsPointer(env, thisObj, brotliDeCompressorStateRefId);
  if (NULL == brotliState || env->ExceptionCheck()) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamDeCompressor wasn't initialised. You need to create a new object before start decompressing.");
    return NULL;
  }

  uint8_t *inBufPtr = (uint8_t *) env->GetPrimitiveArrayCritical(inByteArray, 0);
  if (inBufPtr == NULL || env->ExceptionCheck()) return com_meteogroup_jbrotli_BrotliError_DECOMPRESS_GetPrimitiveArrayCritical_INBUF;
  uint8_t *outBufPtr = (uint8_t *) env->GetPrimitiveArrayCritical(outByteArray, 0);
  if (outBufPtr == NULL || env->ExceptionCheck()) return com_meteogroup_jbrotli_BrotliError_DECOMPRESS_GetPrimitiveArrayCritical_OUTBUF;

  size_t available_in = inLength;
  const uint8_t* inBufPtrInclPosition = inBufPtr + inPosition;
  uint8_t* outBufPtrInclPosition = outBufPtr + outPosition;
  size_t available_out = outLength;
  size_t total_out = 0;
  BrotliResult brotliResult = BrotliDecompressStream(&available_in, &inBufPtrInclPosition, &available_out, &outBufPtrInclPosition, &total_out, brotliState);

  env->ReleasePrimitiveArrayCritical(outByteArray, outBufPtr, 0);
  if (env->ExceptionCheck()) return com_meteogroup_jbrotli_BrotliError_DECOMPRESS_ReleasePrimitiveArrayCritical_OUTBUF;
  env->ReleasePrimitiveArrayCritical(inByteArray, inBufPtr, 0);
  if (env->ExceptionCheck()) return com_meteogroup_jbrotli_BrotliError_DECOMPRESS_ReleasePrimitiveArrayCritical_INBUF;

  int64_t errorCode = 0;
  switch (brotliResult) {
    case BROTLI_RESULT_ERROR:
      errorCode = com_meteogroup_jbrotli_BrotliError_DECOMPRESS_BROTLI_RESULT_ERROR;
      break;
    case BROTLI_RESULT_NEEDS_MORE_INPUT:
      errorCode = com_meteogroup_jbrotli_BrotliError_DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT;
      break;
    case BROTLI_RESULT_NEEDS_MORE_OUTPUT:
      errorCode = com_meteogroup_jbrotli_BrotliError_DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT;
      break;
  }

  jlong errorCodeOrSizeInformation = (errorCode) << 56;
  errorCodeOrSizeInformation = errorCodeOrSizeInformation | (total_out & 0x00000000ffffffffL);
  return errorCodeOrSizeInformation;
}

/*
 * Class:     com_meteogroup_jbrotli_BrotliStreamDeCompressor
 * Method:    deCompressByteBuffer
 * Signature: (Ljava/nio/ByteBuffer;IILjava/nio/ByteBuffer;II)I
 */
JNIEXPORT jint JNICALL Java_com_meteogroup_jbrotli_BrotliStreamDeCompressor_deCompressByteBuffer(JNIEnv *env,
                                                                                              jobject thisObj,
                                                                                              jobject inBuf,
                                                                                              jint inPosition,
                                                                                              jint inLength,
                                                                                              jobject outBuf,
                                                                                              jint outPosition,
                                                                                              jint outLength) {

  if (inPosition < 0 || inLength < 0 ) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: input ByteBuffer position and length must be greater than zero.");
    return NULL;
  }
  if (outPosition < 0 || outLength < 0 ) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: output ByteBuffer position and length must be greater than zero.");
    return NULL;
  }

  if (inLength == 0) return 0;

  BrotliState *brotliState = (BrotliState*) GetLongFieldAsPointer(env, thisObj, brotliDeCompressorStateRefId);
  if (NULL == brotliState || env->ExceptionCheck()) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamDeCompressor wasn't initialised. You need to create a new object before start decompressing.");
    return NULL;
  }

  const uint8_t *inBufPtr = (uint8_t *) env->GetDirectBufferAddress(inBuf);
  if (NULL == inBufPtr) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamDeCompressor couldn't get direct address of input buffer.");
    return NULL;
  }
  uint8_t *outBufPtr = (uint8_t *) env->GetDirectBufferAddress(outBuf);
  if (NULL == outBufPtr) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamDeCompressor couldn't get direct address of output buffer.");
    return NULL;
  }

  size_t available_in = inLength;
  inBufPtr += inPosition;
  size_t available_out = outLength;
  outBufPtr += outPosition;
  size_t total_out = 0;
  BrotliResult brotliResult = BrotliDecompressStream(&available_in, &inBufPtr, &available_out, &outBufPtr, &total_out, brotliState);

  if (brotliResult == BROTLI_RESULT_ERROR) return com_meteogroup_jbrotli_BrotliError_DECOMPRESS_BROTLI_RESULT_ERROR;
  if (brotliResult == BROTLI_RESULT_NEEDS_MORE_INPUT) return com_meteogroup_jbrotli_BrotliError_DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_INPUT;
  if (brotliResult == BROTLI_RESULT_NEEDS_MORE_OUTPUT) return com_meteogroup_jbrotli_BrotliError_DECOMPRESS_BROTLI_RESULT_NEEDS_MORE_OUTPUT;

  return total_out;
}

#ifdef __cplusplus
}
#endif

