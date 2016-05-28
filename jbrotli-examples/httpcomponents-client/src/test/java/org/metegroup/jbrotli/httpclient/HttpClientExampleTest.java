/*
 * Copyright (c) 2016. MeteoGroup Deutschland GmbH
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

package org.metegroup.jbrotli.httpclient;

import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpClientExampleTest {

  @Test
  public void test() throws IOException {
    HttpClientExample clientExample = new HttpClientExample();

    String text = clientExample.downloadFileAsString("http://localhost:8080/canterbury-corpus/alice29.txt");

    assertThat(text).contains("Alice was beginning to get very tired of sitting by her sister");
    assertThat(text).contains("So she was considering in her own mind (as well as she could,");
  }

}