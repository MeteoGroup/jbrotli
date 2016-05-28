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

import java.io.IOException;

public class HttpClientExample {

  public static void main(String[] args) throws Exception {
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {
      @Override
      public void process(HttpRequest request, HttpContext httpContext) throws HttpException, IOException {
        if (!request.containsHeader("Accept-Encoding")) {
          request.addHeader("Accept-Encoding", "br");
        }
      }
    });

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

    try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
      HttpGet httpget = new HttpGet("http://localhost:8080/canterbury-corpus/alice29.txt");

      // Execute HTTP request
      System.out.println("executing request " + httpget.getURI());
      HttpResponse response = httpClient.execute(httpget);

      System.out.println("----------------------------------------");
      System.out.println(response.getStatusLine());
      System.out.println(response.getLastHeader("Content-Encoding"));
      System.out.println(response.getLastHeader("Content-Length"));
      System.out.println("----------------------------------------");

      HttpEntity entity = response.getEntity();

      if (entity != null) {
        String content = EntityUtils.toString(entity);
        System.out.println(content);
        System.out.println("----------------------------------------");
        System.out.println("Uncompressed size: " + content.length());
      }
    }
  }

  private static boolean usesBrotliContentEncoding(Header contentEncoding) {
    if (contentEncoding != null) {
      for (HeaderElement codec : contentEncoding.getElements()) {
        if ("br".equalsIgnoreCase(codec.getName())) {
          return true;
        }
      }
    }
    return false;
  }
}


