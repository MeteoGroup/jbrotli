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
#include "../../../../brotli/enc/encode.h"
#include "./param_converter.h"

brotli::BrotliParams mapToBrotliParams(JNIEnv *env, jint mode, jint quality, jint lgwin, jint lgblock) {
  brotli::BrotliParams params;
  switch (mode) {
    case 0:
      params.mode = brotli::BrotliParams::MODE_GENERIC;
      break;
    case 1:
      params.mode = brotli::BrotliParams::MODE_TEXT;
      break;
    case 2:
      params.mode = brotli::BrotliParams::MODE_FONT;
      break;
    default:
      params.mode = brotli::BrotliParams::MODE_GENERIC;
  }
  params.quality = quality;
  params.lgwin = lgwin;
  params.lgblock = lgblock;
  return params;
}
