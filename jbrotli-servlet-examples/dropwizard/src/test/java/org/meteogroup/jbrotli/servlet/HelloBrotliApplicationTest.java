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

import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static io.dropwizard.testing.ConfigOverride.config;
import static org.assertj.core.api.Assertions.assertThat;

public class HelloBrotliApplicationTest {

  public static final DropwizardTestSupport<HelloBrotliConfiguration> SUPPORT =
      new DropwizardTestSupport<>(HelloBrotliApplication.class, ResourceHelpers.resourceFilePath("hello_brotli.yml"), config("server.applicationConnectors[0].port", "0"));

  @BeforeClass
  public void beforeClass() {
    SUPPORT.before();
  }

  @AfterClass
  public void afterClass() {
    SUPPORT.after();
  }

  @Test
  public void brotli_content_encoding_is_set() {
    Client client = new JerseyClientBuilder().build();

    Response response = client.target(
        String.format("http://localhost:%d/canterbury-corpus/plrabn12.txt", SUPPORT.getLocalPort()))
        .request()
        .acceptEncoding("br")
        .get();

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getHeaderString("Content-Encoding")).isEqualTo("br");
  }
}