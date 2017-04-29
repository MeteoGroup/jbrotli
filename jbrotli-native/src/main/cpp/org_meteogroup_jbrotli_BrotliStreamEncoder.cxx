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
static jfieldID outBufferInstanceRefID;


// http://normanmaurer.me/blog/2014/01/07/JNI-Performance-Welcome-to-the-dark-side/
// // Is automatically called once the native code is loaded via System.loadLibary(...);
// jint JNI_OnLoad(JavaVM* vm, void* reserved) {
//     JNIEnv* env;
//     if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6) != JNI_OK) {
//         return JNI_ERR;
//     } else {
//         jclass cls = (*env)->FindClass("java/nio/Buffer");
//         // Get the id of the Buffer.limit() method.
//         limitMethodId = (*env)->GetMethodID(env, cls, "limit", "()I");

//         // Get int limit field of Buffer
//         limitFieldId = (*env)->GetFieldID(env, cls, "limit", "I");
//     }
// }

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamEncoder
 * Method:    initJavaFieldIdCache
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliStreamEncoder_initJavaFieldIdCache(JNIEnv *env,
                                                                                            jclass cls) {
  brotliEncoderStateInstanceRefID = env->GetFieldID(cls, "brotliEncoderStateInstanceRefID", "J");
  if (NULL == brotliEncoderStateInstanceRefID) {
    return org_meteogroup_jbrotli_BrotliError_NATIVE_GET_FIELD_ID_ERROR;
  }
  outBufferInstanceRefID = env->GetFieldID(cls, "outBuffer", "Ljava/nio/ByteBuffer;");
  if (NULL == outBufferInstanceRefID) {
    return org_meteogroup_jbrotli_BrotliError_NATIVE_GET_FIELD_ID_ERROR;
  }
  return 0;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamEncoder
 * Method:    initNativeEncoder
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliStreamEncoder_initNativeEncoder(JNIEnv *env,
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
 * Class:     org_meteogroup_jbrotli_BrotliStreamEncoder
 * Method:    freeNativeResources
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliStreamEncoder_freeNativeResources(JNIEnv *env,
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
 * Class:     org_meteogroup_jbrotli_BrotliStreamEncoder
 * Method:    processNative
 * Signature: (Ljava/nio/ByteBuffer;II)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliStreamEncoder_processNative(JNIEnv *env,
                                                                                     jobject thisObj,
                                                                                     jobject inBuf,
                                                                                     jint inPosition,
                                                                                     jint inLength){
  if (inPosition < 0 || inLength < 0) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"), "Brotli: input array position and length must be greater than zero.");
    return NULL;
  }

  BrotliEncoderState* encoderState = (BrotliEncoderState*) GetLongFieldAsPointer(env, thisObj, brotliEncoderStateInstanceRefID);
  if (NULL == encoderState) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor was already closed (encoderState=null). You need to create a new object before start compressing.");
    return NULL;
  }

  jobject outByteBuffer = (jobject) ((env)->GetObjectField(thisObj, outBufferInstanceRefID));
  if (NULL == outByteBuffer) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor was already closed (outByteBuffer=null). You need to create a new object before start compressing.");
    return NULL;
  }

  uint8_t *outBufPtr = (uint8_t *) env->GetDirectBufferAddress(outByteBuffer);
  if (NULL == outBufPtr) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor couldn't get direct address of output ByteBuffer.");
    return NULL;
  }

  uint8_t *inBufPtr = (uint8_t *) env->GetDirectBufferAddress(inBuf);
  if (NULL == inBufPtr) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor couldn't get direct address of input ByteBuffer.");
    return NULL;
  }

  size_t available_out = 32*1024;
  size_t total_out = 0;

  if (inLength > 0) {
    size_t available_in = as_size_t(inLength);
    const uint8_t *next_in = inBufPtr + inPosition;
    BROTLI_BOOL ok = BrotliEncoderCompressStream(encoderState, BROTLI_OPERATION_PROCESS, &available_in, &next_in, &available_out, &outBufPtr, &total_out);
    if (BROTLI_FALSE == ok) {
      env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "Error in native code BrotliCompressor::WriteBrotliData().");
      return NULL;
    }
  }

  return jlong_to_jint(size_to_jlong(total_out));
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamEncoder
 * Method:    flushNative
 * Signature: ()Ljava/nio/ByteBuffer;
 */
JNIEXPORT jint JNICALL Java_org_meteogroup_jbrotli_BrotliStreamEncoder_flushNative(JNIEnv *env,
                                                                                      jobject thisObj) {
  BrotliEncoderState* encoderState = (BrotliEncoderState*) GetLongFieldAsPointer(env, thisObj, brotliEncoderStateInstanceRefID);
  if (NULL == encoderState) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor was already closed (encoderState=null). You need to create a new object before start compressing.");
    return NULL;
  }

  jobject outByteBuffer = (jobject) ((env)->GetObjectField(thisObj, outBufferInstanceRefID));
  if (NULL == outByteBuffer) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor was already closed (outByteBuffer=null). You need to create a new object before start compressing.");
    return NULL;
  }

  uint8_t *outBufPtr = (uint8_t *) env->GetDirectBufferAddress(outByteBuffer);
  if (NULL == outBufPtr) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor couldn't get direct address of output ByteBuffer.");
    return NULL;
  }

  size_t available_out = 32*1024;
  size_t total_out = 0;
  size_t available_in = 0;
  uint8_t *inBufPtr = NULL;
  const uint8_t *next_in = NULL;
  BROTLI_BOOL ok = BrotliEncoderCompressStream(encoderState, BROTLI_OPERATION_FLUSH, &available_in, &next_in, &available_out, &outBufPtr, &total_out);
  if (BROTLI_FALSE == ok) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "Error in native code BrotliCompressor::WriteBrotliData().");
    return NULL;
  }

  return jlong_to_jint(size_to_jlong(total_out));
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamEncoder
 * Method:    finishNative
 * Signature: ()Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_org_meteogroup_jbrotli_BrotliStreamEncoder_finishNative(JNIEnv *env,
                                                                                       jobject thisObj) {
  return NULL;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamEncoder
 * Method:    BrotliEncoderTakeOutput
 * Signature: ()Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_org_meteogroup_jbrotli_BrotliStreamEncoder_BrotliEncoderTakeOutput(JNIEnv *env,
                                                                                                  jobject thisObj) {
  return NULL;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamEncoder
 * Method:    brotliEncoderIsFinished
 * Signature: ()Z;
 */
JNIEXPORT jboolean JNICALL Java_org_meteogroup_jbrotli_BrotliStreamEncoder_brotliEncoderIsFinished(JNIEnv *env,
                                                                                                   jobject thisObj) {
  BrotliEncoderState* encoderState = (BrotliEncoderState*) GetLongFieldAsPointer(env, thisObj, brotliEncoderStateInstanceRefID);
  if (NULL == encoderState) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor was already closed. You need to create a new object before start compressing.");
    return NULL;
  }
  return BROTLI_TRUE == BrotliEncoderIsFinished(encoderState) ? JNI_TRUE : JNI_FALSE;
}

/*
 * Class:     org_meteogroup_jbrotli_BrotliStreamEncoder
 * Method:    brotliEncoderHasMoreOutput
 * Signature: ()Z;
 */
JNIEXPORT jboolean JNICALL Java_org_meteogroup_jbrotli_BrotliStreamEncoder_brotliEncoderHasMoreOutput(JNIEnv *env,
                                                                                                      jobject thisObj) {
  BrotliEncoderState* encoderState = (BrotliEncoderState*) GetLongFieldAsPointer(env, thisObj, brotliEncoderStateInstanceRefID);
  if (NULL == encoderState) {
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), "BrotliStreamCompressor was already closed. You need to create a new object before start compressing.");
    return NULL;
  }
  return BROTLI_TRUE == BrotliEncoderHasMoreOutput(encoderState) ? JNI_TRUE : JNI_FALSE;
}

#ifdef __cplusplus
}
#endif

