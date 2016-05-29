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

package org.meteogroup.jbrotli.httpclient.apache;

import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.meteogroup.jbrotli.httpserver.TestServerApplication;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.dropwizard.testing.ConfigOverride.config;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;

public class HttpClientExampleTest {

  private static final DropwizardTestSupport<TestServerApplication.TestServerConfiguration> SUPPORT =
      new DropwizardTestSupport<>(TestServerApplication.class, ResourceHelpers.resourceFilePath("testserver.yml"), config("server.applicationConnectors[0].port", "0"));
  private String indexHtmlUrl;

  @BeforeClass
  public void beforeClass() {
    SUPPORT.before();
  }

  @AfterClass
  public void afterClass() {
    SUPPORT.after();
  }

  @BeforeMethod
  public void setUp() throws Exception {
    indexHtmlUrl = String.format("http://localhost:%d/index.html", SUPPORT.getLocalPort());
  }

  @Test
  public void brotli_downloads_can_be_decompressed_via_apache_httpclient() throws IOException, HttpException {
    HttpClientExample clientExample = new HttpClientExample();
    clientExample.httpResponseInterceptor = spy(clientExample.httpResponseInterceptor);
    ArgumentCaptor<HttpResponse> httpResponseCaptor = ArgumentCaptor.forClass(HttpResponse.class);
    doCallRealMethod().when(clientExample.httpResponseInterceptor).process(httpResponseCaptor.capture(), any(HttpContext.class));

    String text = clientExample.downloadFileAsString(indexHtmlUrl);

    assertThat(text).contains("Info-ZIP; Zip, UnZip, gzip and zlib co-author; PNG group");
    assertThat(text).contains("<i><b>Audio compression</b></i>");
    assertBrotliDecompressionWasUsed(httpResponseCaptor);
  }

  private void assertBrotliDecompressionWasUsed(ArgumentCaptor<HttpResponse> httpResponseCaptor) {
    assertThat(httpResponseCaptor.getValue().getEntity()).isOfAnyClassIn(BrotliDecompressingEntity.class);
  }

}