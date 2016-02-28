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

import org.scijava.nativelib.NativeLoader;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BrotliServletFilter implements Filter {

  private static final String BROTLI_MIME_TYPE = "br";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    try {
      NativeLoader.loadLibrary("brotli");
    } catch (IOException e) {
      throw new ServletException(e);
    }
  }

  @Override
  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    if (acceptsGZipEncoding(httpRequest)) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.addHeader("Content-Encoding", BROTLI_MIME_TYPE);
      BrotliServletResponseWrapper brotliResponse = new BrotliServletResponseWrapper(httpResponse);
      chain.doFilter(request, brotliResponse);
      brotliResponse.close();
    } else {
      chain.doFilter(request, response);
    }
  }

  private boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
    String acceptEncoding = httpRequest.getHeader("Accept-Encoding");
    return acceptEncoding != null && acceptEncoding.contains(BROTLI_MIME_TYPE);
  }
}