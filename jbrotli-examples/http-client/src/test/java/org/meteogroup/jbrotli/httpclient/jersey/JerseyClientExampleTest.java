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

package org.meteogroup.jbrotli.httpclient.jersey;

import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;
import org.apache.http.HttpException;
import org.meteogroup.jbrotli.httpserver.TestServerApplication;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.dropwizard.testing.ConfigOverride.config;
import static org.assertj.core.api.Assertions.assertThat;

public class JerseyClientExampleTest {

  protected static final DropwizardTestSupport<TestServerApplication.TestServerConfiguration> SUPPORT =
      new DropwizardTestSupport<>(TestServerApplication.class, ResourceHelpers.resourceFilePath("testserver.yml"), config("server.applicationConnectors[0].port", "0"));

  protected String indexHtmlUrl;

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
  public void brotli_downloads_can_be_decompressed_via_apache_jerseyclient() throws IOException, HttpException {
    JerseyClientExample jerseyClient = new JerseyClientExample();

    String text = jerseyClient.downloadFileAsString(indexHtmlUrl);

    assertThat(text).contains("Info-ZIP; Zip, UnZip, gzip and zlib co-author; PNG group");
    assertThat(text).contains("<i><b>Audio compression</b></i>");
  }

}