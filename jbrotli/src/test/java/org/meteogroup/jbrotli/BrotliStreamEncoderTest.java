/*
 * Copyright (c) 2017 MeteoGroup Deutschland GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.meteogroup.jbrotli;

import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meteogroup.jbrotli.BufferTestHelper.createFilledByteArray;
import static org.meteogroup.jbrotli.BufferTestHelper.wrapDirect;

public class BrotliStreamEncoderTest {

  private BrotliStreamEncoder encoder;

  @BeforeClass
  public void loadLibrary() throws Exception {
    BrotliLibraryLoader.loadBrotli();
  }

  @BeforeMethod
  public void setUp() throws Exception {
    encoder = new BrotliStreamEncoder(Brotli.DEFAULT_PARAMETER);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    encoder.close();
  }

  @Test
  public void compress_sets_position_to_the_next_compressed_block__until_hits_limit() throws Exception {
    // given
    ByteBuffer in = wrapDirect(createFilledByteArray(1024, 'x'));

    // when
    ByteBuffer out;
    out = encoder.process(in);
    out = encoder.flush();

    // then
    assertThat(in.position()).isEqualTo(1024);
    assertThat(out.capacity()).isGreaterThanOrEqualTo(10);
  }

}