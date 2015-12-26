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

// Check windows
#if _WIN32 || _WIN64
   #if _WIN64
     #define ENV64BIT
  #else
    #define ENV32BIT
  #endif
#endif

// Check GCC
#if __GNUC__
  #if __x86_64__ || __ppc64__
    #define ENV64BIT
  #else
    #define ENV32BIT
  #endif
#endif

// *******************************************************

#define GetLongFieldAsPointer(env,obj,id) (jlong_to_ptr((env)->GetLongField((obj),(id))))
#define SetLongFieldFromPointer(env,obj,id,val) (env)->SetLongField((obj),(id),ptr_to_jlong(val))

#if defined(ENV64BIT)
  /* 64-bit code here. */
  #define jlong_to_ptr(a) ((void*)(a))
  #define ptr_to_jlong(a) ((jlong)(a))
#elif defined (ENV32BIT)
  /* 32-bit code here. */
  // Double casting to avoid warning messages looking for casting of
  // smaller sizes into pointers //
  #define jlong_to_ptr(a) ((void*)(int)(a))
  #define ptr_to_jlong(a) ((jlong)(int)(a))
#else
  #error "Must define either ENV32BIT or ENV64BIT"
#endif