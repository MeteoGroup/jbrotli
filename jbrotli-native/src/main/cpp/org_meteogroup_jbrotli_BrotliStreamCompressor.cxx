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
#include <stdio.h>

#include <brotli/encode.h>
#include "./type_converters.h"
#include "./param_converter.h"
#include "./org_meteogroup_jbrotli_BrotliError.h"


#ifdef __cplusplus
extern "C" {
#endif

static jfieldID brotliEncoderStateInstanceRefID;

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamCompressor
 * Method:    initJavaFieldIdCache
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliStreamCompressor_initJavaFieldIdCache(JNIEnv *env,
                                                                                               jclass cls) {
  brotliEncoderStateInstanceRefID = env->GetFieldID(cls, "brotliEncoderStateInstanceRefID", "J");
  if (NULL == brotliEncoderStateInstanceRefID) {
    return org_meteogroup_jbrotli_BrotliError_NATIVE_GET_FIELD_ID_ERROR;
  }
  return 0;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamCompressor
 * Method:    initBrotliCompressor
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliStreamCompressor_initBrotliCompressor(JNIEnv *env,
                                                                                               jobject thisObj,
                                                                                               jint mode,
                                                                                               jint quality,
                                                                                               jint lgwin,
                                                                                               jint lgblock) {

  BrotliEncoderState* encoderState = (BrotliEncoderState*) GetLongFieldAsPointer(env, thisObj, brotliEncoderStateInstanceRefID);
  if (NULL != encoderState) {
    BrotliEncoderDestroyInstance(encoderState);
  }
  encoderState = BrotliEncoderCreateInstance(NULL, NULL, NULL);
  BrotliEncoderSetParameter(encoderState, BROTLI_PARAM_MODE, asBrotliEncoderMode(mode));
  BrotliEncoderSetParameter(encoderState, BROTLI_PARAM_QUALITY, (uint32_t) quality);
  BrotliEncoderSetParameter(encoderState, BROTLI_PARAM_LGWIN, (uint32_t) lgwin);
  BrotliEncoderSetParameter(encoderState, BROTLI_PARAM_LGBLOCK, (uint32_t) lgblock);
  SetLongFieldFromPointer(env, thisObj, brotliEncoderStateInstanceRefID, encoderState);
  return 0;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamCompressor
 * Method:    freeNativeResources
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliStreamCompressor_freeNativeResources(JNIEnv *env,
                                                                                              jobject thisObj) {
  BrotliEncoderState* encoderState = (BrotliEncoderState*) GetLongFieldAsPointer(env, thisObj, brotliEncoderStateInstanceRefID);
  if (NULL != encoderState) {
    BrotliEncoderDestroyInstance(encoderState);
    brotliEncoderStateInstanceRefID = NULL;
    SetLongFieldFromPointer(env, thisObj, brotliEncoderStateInstanceRefID, encoderState);
  }
  return 0;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamCompressor
 * Method:    getInputBlockSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliStreamCompressor_getInputBlockSize(JNIEnv *env,
                                                                                            jobject thisObj) {
  // brotli::BrotliCompressor *compressor = (brotli::BrotliCompressor*) GetLongFieldAsPointer(env, thisObj, brotliCompressorInstanceRefID);
  // if (NULL == compressor) {
  //   env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor was already closed. You need to create a new object before getInputBlockSize.");
  //   return org_meteogroup_jbrotli_BrotliError_NATIVE_ERROR;
  // }
  // return compressor->input_block_size();
  return 4096;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamCompressor
 * Method:    compressBytes
 * Signature: ([BIIZZ)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_meteogroup_jbrotli_BrotliStreamCompressor_compressBytes(JNIEnv *env,
                                                                                              jobject thisObj,
                                                                                              jbyteArray inByteArray,
                                                                                              jint inPosition,
                                                                                              jint inLength,
                                                                                              jboolean doFlush,
                                                                                              jboolean isLast) {

  if (inPosition < 0 || inLength < 0) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: input array position and length must be greater than zero.");
    return NULL;
  }

  BrotliEncoderState* encoderState = (BrotliEncoderState*) GetLongFieldAsPointer(env, thisObj, brotliEncoderStateInstanceRefID);
  if (NULL == encoderState) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor was already closed. You need to create a new object before start compressing.");
    return NULL;
  }

  // if ((signed)compressor->input_block_size() < inLength) {
  //   env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "BrotliStreamCompressor, input byte array length is larger than allowed input block size. Slice the input into smaller chunks.");
  //   return NULL;
  // }

  uint8_t buffer[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
  uint8_t *brotliOutBufferPtr = (uint8_t*)&buffer;
  size_t available_out = sizeof(buffer);

  printf("GO\n");
  printf("PROCESS - inLength: %d\n", inLength);

  if (inLength > 0) {
    size_t total_out = 0;
    size_t available_in = as_size_t(inLength);
    printf("PROCESS before - available_in: %d\n", available_in);
    printf("PROCESS after - &brotliOutBufferPtr: %d\n", &brotliOutBufferPtr);

    uint8_t *inBufCritArray = (uint8_t *) env->GetPrimitiveArrayCritical(inByteArray, 0);
    if (inBufCritArray == NULL || env->ExceptionCheck()) return NULL;
    const uint8_t *next_in = inBufCritArray + inPosition;
    // printf("PROCESS before - next_in: %d\n", next_in);
    // compressor->CopyInputToRingBuffer(inLength, inBufCritArray + inPosition);
    BROTLI_BOOL ok = BrotliEncoderCompressStream(encoderState, BROTLI_OPERATION_PROCESS, &available_in, &next_in, &available_out, &brotliOutBufferPtr, &total_out);
    if (BROTLI_FALSE == ok) {
      env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "Error in native code BrotliCompressor::WriteBrotliData().");
      return NULL;
    }

    printf("PROCESS after - available_in: %d\n", available_in);
    // printf("PROCESS after - next_in: %d\n", next_in);
    printf("PROCESS after - available_out: %d\n", available_out);
    printf("PROCESS after - &brotliOutBufferPtr: %d\n", &brotliOutBufferPtr);
    printf("PROCESS after - total_out: %d\n", total_out);
    printf("PROCESS after - BrotliEncoderIsFinished: %d\n", BrotliEncoderIsFinished(encoderState));

    ok = BrotliEncoderCompressStream(encoderState, BROTLI_OPERATION_FLUSH, &available_in, &next_in, &available_out, &brotliOutBufferPtr, &total_out);
    if (BROTLI_FALSE == ok) {
      env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "Error in native code BrotliCompressor::WriteBrotliData().");
      return NULL;
    }

    printf("FLUSH after - available_in: %d\n", available_in);
    printf("FLUSH after - available_out: %d\n", available_out);
    printf("FLUSH after - total_out: %d\n", total_out);

    env->ReleasePrimitiveArrayCritical(inByteArray, inBufCritArray, 0);
    if (env->ExceptionCheck()) return NULL;
  }

  jbyteArray outByteArray = env->NewByteArray(available_out);
  if (available_out > 0) {
    uint8_t *outBufCritArray = (uint8_t *) env->GetPrimitiveArrayCritical(outByteArray, 0);
    if (outBufCritArray == NULL || env->ExceptionCheck()) return NULL;
    memcpy(outBufCritArray, brotliOutBufferPtr, available_out);
    env->ReleasePrimitiveArrayCritical(outByteArray, outBufCritArray, 0);
    if (env->ExceptionCheck()) return NULL;
  }

  return outByteArray;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamCompressor
 * Method:    compressByteBuffer
 * Signature: (Ljava/nio/ByteBuffer;IIZ)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_org_meteogroup_jbrotli_BrotliStreamCompressor_compressByteBuffer(JNIEnv *env,
                                                                                                jobject thisObj,
                                                                                                jobject inBuf,
                                                                                                jint inPosition,
                                                                                                jint inLength,
                                                                                                jboolean doFlush) {

  // if (inPosition < 0 || inLength < 0 ) {
  //   env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: input ByteBuffer position and length must be greater than zero.");
  //   return NULL;
  // }

  // brotli::BrotliCompressor *compressor = (brotli::BrotliCompressor *) GetLongFieldAsPointer(env, thisObj, brotliCompressorInstanceRefID);
  // if (NULL == compressor) {
  //   env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor was already closed. You need to create a new object before start compressing.");
  //   return NULL;
  // }

  // if ((signed)compressor->input_block_size() < inLength) {
  //   env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "BrotliStreamCompressor, input ByteBuffer size is larger than allowed input block size. Slice the input into smaller chunks.");
  //   return NULL;
  // }

  // uint8_t *inBufPtr = (uint8_t *) env->GetDirectBufferAddress(inBuf);
  // if (NULL == inBufPtr) {
  //   env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor couldn't get direct address of input buffer.");
  //   return NULL;
  // }

  // if (inLength > 0) {
  //   compressor->CopyInputToRingBuffer(inLength, inBufPtr + inPosition);
  // }

  // size_t computedOutLength = 0;
  // uint8_t *brotliOutBufferPtr;
  // bool writeResult = compressor->WriteBrotliData(false, doFlush, &computedOutLength, &brotliOutBufferPtr);
  // if (!writeResult)  {
  //   env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor got an error while calling WriteBrotliData() method.");
  //   return NULL;
  // }

  // return env->NewDirectByteBuffer(brotliOutBufferPtr, computedOutLength);

  return NULL;
}

#ifdef __cplusplus
}
#endif

