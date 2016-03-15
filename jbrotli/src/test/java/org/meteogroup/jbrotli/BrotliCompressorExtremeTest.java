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

package org.meteogroup.jbrotli;

import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BrotliCompressorExtremeTest {

  private BrotliCompressor compressor;

  @BeforeClass
  public void loadLibrary() throws Exception {
    BrotliLibraryLoader.loadBrotli();
  }

  @BeforeMethod
  public void setUp() throws Exception {
    compressor = new BrotliCompressor();
  }

  @Test(expectedExceptions = BrotliException.class,
      expectedExceptionsMessageRegExp = ".*\\(Error code: -14\\)")
  public void compress_huge_buffer() throws Exception {
    byte[] inBuf = createArrayWithNumbers(100000);
    assertThat(inBuf.length).isEqualTo(488890);

    byte[] compressedBuf = new byte[2048];

    int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER, inBuf, compressedBuf);

    // expected Exception, because output buffer too small
  }

  private byte[] createArrayWithNumbers(int numberCount) {
    StringBuilder bigData = new StringBuilder();
    for (int i = 0; i < numberCount; i++) {
      bigData.append(i);
    }
    return bigData.toString().getBytes();
  }

}
