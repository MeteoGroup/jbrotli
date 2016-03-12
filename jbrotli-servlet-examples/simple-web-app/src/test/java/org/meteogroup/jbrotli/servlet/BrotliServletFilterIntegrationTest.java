/*
 * Copyright (c) 2016 MeteoGroup Deutschland GmbH
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

package org.meteogroup.jbrotli.servlet;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;

public class BrotliServletFilterIntegrationTest extends AbstractWebAppIntegrationTest {

  private String root_url;

  @BeforeMethod
  public void setUp() throws Exception {
    root_url = "http://" + getServerIpAddress() + ":" + getServerPort();
  }

  @Test
  public void brotli_content_encoding_is_set() throws Exception {
    // given
    URL textFileUrl = new URL(root_url + "/canterbury-corpus/alice29.txt");

    // when
    HttpURLConnection httpCon = (HttpURLConnection) textFileUrl.openConnection();
    httpCon.addRequestProperty("Accept-Encoding", "br");
    httpCon.connect();
    String contentEncoding = httpCon.getHeaderField("Content-Encoding");
    httpCon.disconnect();

    // then
    assertThat(contentEncoding).isEqualTo("br");
  }

  @Test
  public void content_length_is_NOT_set__OR_is_zero() throws Exception {
    // given
    URL textFileUrl = new URL(root_url + "/canterbury-corpus/alice29.txt");

    // when
    HttpURLConnection httpCon = (HttpURLConnection) textFileUrl.openConnection();
    httpCon.addRequestProperty("Accept-Encoding", "br");
    httpCon.connect();
    String contentEncoding = httpCon.getHeaderField("Content-Length");

    // then
    if (contentEncoding != null) {
      assertThat(parseInt(contentEncoding)).isEqualTo(0);
    }
    // contentEncoding==null is OK
  }

}
