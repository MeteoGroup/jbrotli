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

  /**
   * As defined in RFC draft "Brotli Compressed Data Format"
   *
   * @see <a href="http://www.ietf.org/id/draft-alakuijala-brotli-08.txt"></a>
   */
  public static final String BROTLI_HTTP_CONTENT_CODING = "br";

  private static final HttpAcceptEncodingParser acceptEncodingParser = new HttpAcceptEncodingParser();

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
    if (acceptEncodingParser.acceptBrotliEncoding((HttpServletRequest) request)) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.addHeader("Content-Encoding", BROTLI_HTTP_CONTENT_CODING);
      BrotliServletResponseWrapper brotliResponse = new BrotliServletResponseWrapper(httpResponse);
      try {
        chain.doFilter(request, brotliResponse);
      } finally {
        brotliResponse.close();
      }
    } else {
      chain.doFilter(request, response);
    }
  }

}