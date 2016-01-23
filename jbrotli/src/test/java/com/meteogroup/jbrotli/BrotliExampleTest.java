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

import org.scijava.nativelib.NativeLoader;
import org.testng.annotations.Test;

public class BrotliExampleTest {

  /**
   * intended to be used in README ... to make sure it will compile ;-)
   *
   * @throws Exception
   */
  @Test
  public void compress_with_byte_array() throws Exception {

    NativeLoader.loadLibrary("brotli");

    byte[] inBuf = "Brotli: a new compression algorithm for the internet. Now available for Java!".getBytes();
    byte[] compressedBuf = new byte[2048];
    BrotliCompressor compressor = new BrotliCompressor();
    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuf, compressedBuf);

  }

  /**
   * intended to be used in README ... to make sure it will compile ;-)
   *
   * @throws Exception
   */
  @Test
  public void compress_with_stream_compressor() throws Exception {

    NativeLoader.loadLibrary("brotli");

    byte[] inBuf = "Brotli: a new compression algorithm for the internet. Now available for Java!".getBytes();
    boolean doFlush = true;
    BrotliStreamCompressor streamCompressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);
    byte[] compressed = streamCompressor.compressBuffer(inBuf, doFlush);
  }

}