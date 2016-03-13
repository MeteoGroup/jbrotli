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

package org.meteogrou.jbrotli.servlet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.meteogroup.jbrotli.servlet.BrotliServletFilter.BROTLI_HTTP_CONTENT_CODING;

@SpringApplicationConfiguration(classes = HelloBrotliApplication.class)
@WebIntegrationTest({"server.port=0", "management.port=0"})
public class HelloBrotliHttpControllerTest extends AbstractTestNGSpringContextTests {

  @Value("${local.server.port}")
  protected int localServerPort = 0;
  private String root_url;
  TestRestTemplate restTemplate;

  @BeforeMethod
  public void setUp() throws Exception {
    this.root_url = "http://127.0.0.1:" + localServerPort;
    restTemplate = new TestRestTemplate();
  }

  @Test
  public void brotli_content_encoding_is_set() throws Exception {
    // given
    String textFileUrl = root_url + "/canterbury-corpus/asyoulik.txt";

    // when
    restTemplate.setInterceptors(createAcceptBrotliEncodingInterceptor());
    HttpHeaders headers = restTemplate.getForEntity(textFileUrl, String.class).getHeaders();

    // then
    assertThat(headers.get("Content-Encoding")).containsOnly(BROTLI_HTTP_CONTENT_CODING);
  }

  @Test
  public void content_length_is_NOT_set__OR_is_zero() throws Exception {
    // given
    String textFileUrl = root_url + "/canterbury-corpus/asyoulik.txt";

    // when
    restTemplate.setInterceptors(createAcceptBrotliEncodingInterceptor());
    HttpHeaders headers = restTemplate.getForEntity(textFileUrl, String.class).getHeaders();

    // then
    assertThat(headers.getContentLength()).describedAs("Content length should be unknown").isEqualTo(-1L);
  }

  @Test
  public void hello_brotli_controller_response_OK() throws Exception {
    // given
    String textFileUrl = root_url + "/hello";

    // when
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(textFileUrl, String.class);

    // then
    assertThat(responseEntity.getBody()).isEqualTo("Hello Brotli!");
  }

  private static List<ClientHttpRequestInterceptor> createAcceptBrotliEncodingInterceptor() {
    return singletonList((ClientHttpRequestInterceptor) new ClientHttpRequestInterceptor() {
      @Override
      public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequest wrapper = new HttpRequestWrapper(request);
        wrapper.getHeaders().set("Accept-Encoding", BROTLI_HTTP_CONTENT_CODING);
        return execution.execute(wrapper, body);
      }
    });
  }
}