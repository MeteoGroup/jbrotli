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

package org.metegroup.jbrotli.httpclient;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.IOException;

public class HttpClientExample {

  public static final String BROTLI_MIME_TYPE = "br";

  public String downloadFileAsString(String url) throws IOException {
    BrotliLibraryLoader.loadBrotli();
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    prepareAcceptHeaderForBrotli(httpClientBuilder);
    prepareResponseContentFilter(httpClientBuilder);
    try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
      String entity = downloadFileAsString(httpClient, url);
      if (entity != null) return entity;
    }
    return null;
  }

  private String downloadFileAsString(CloseableHttpClient httpClient, String url) throws IOException {
    HttpGet httpget = new HttpGet(url);
    HttpResponse response = httpClient.execute(httpget);
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      return EntityUtils.toString(entity);
    }
    return null;
  }

  private void prepareAcceptHeaderForBrotli(HttpClientBuilder httpClientBuilder) {
    httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {
      @Override
      public void process(HttpRequest request, HttpContext httpContext) throws HttpException, IOException {
        if (!request.containsHeader("Accept-Encoding")) {
          request.addHeader("Accept-Encoding", BROTLI_MIME_TYPE);
        }
      }
    });
  }

  private void prepareResponseContentFilter(HttpClientBuilder httpClientBuilder) {
    httpClientBuilder.addInterceptorFirst(new HttpResponseInterceptor() {
      @Override
      public void process(HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          Header contentEncoding = entity.getContentEncoding();
          if (usesBrotliContentEncoding(contentEncoding)) {
            response.setEntity(new BrotliDecompressingEntity(response.getEntity()));
          }
        }
      }
    });
  }

  private boolean usesBrotliContentEncoding(Header contentEncoding) {
    if (contentEncoding != null) {
      for (HeaderElement codec : contentEncoding.getElements()) {
        if (BROTLI_MIME_TYPE.equalsIgnoreCase(codec.getName())) {
          return true;
        }
      }
    }
    return false;
  }
}


