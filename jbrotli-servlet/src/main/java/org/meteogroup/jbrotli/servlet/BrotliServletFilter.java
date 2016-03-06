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

import org.meteogroup.jbrotli.Brotli;
import org.scijava.nativelib.NativeLoader;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * BrotliServletFilter
 * This filter accepts init parameters and uses the following defaults:
 * <pre>
 *   brotli.compression.parameter.mode=generic       [generic, text, font]
 *   brotli.compression.parameter.quality=5          [0..11]
 *   brotli.compression.parameter.lgwin=22           [10..24]
 *   brotli.compression.parameter.lgblock=0          [16..24]
 * </pre>
 */
public class BrotliServletFilter implements Filter {

  /**
   * As defined in RFC draft "Brotli Compressed Data Format"
   *
   * @see <a href="http://www.ietf.org/id/draft-alakuijala-brotli-08.txt"></a>
   */
  public static final String BROTLI_HTTP_CONTENT_CODING = "br";

  private static final HttpAcceptEncodingParser acceptEncodingParser = new HttpAcceptEncodingParser();
  private static final int DEFAULT_BROTLI_SERVLET_COMPRESSION_QUALITY = 5;

  protected Brotli.Parameter brotliCompressionParameter = Brotli.DEFAULT_PARAMETER;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    try {
      NativeLoader.loadLibrary("brotli");
    } catch (IOException e) {
      throw new ServletException(e);
    }
    applyFilterConfig(filterConfig);
  }

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (acceptEncodingParser.acceptBrotliEncoding((HttpServletRequest) request)) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.addHeader("Content-Encoding", BROTLI_HTTP_CONTENT_CODING);
      BrotliServletResponseWrapper brotliResponse = new BrotliServletResponseWrapper(httpResponse, brotliCompressionParameter);
      try {
        chain.doFilter(request, brotliResponse);
      } finally {
        brotliResponse.close();
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  private void applyFilterConfig(FilterConfig filterConfig) {
    brotliCompressionParameter = brotliCompressionParameter
        .setMode(getInitParameterAsBrotliMode(filterConfig, "brotli.compression.parameter.mode", Brotli.Mode.GENERIC))
        .setQuality(getInitParameterAsInteger(filterConfig, "brotli.compression.parameter.quality", DEFAULT_BROTLI_SERVLET_COMPRESSION_QUALITY))
        .setLgwin(getInitParameterAsInteger(filterConfig, "brotli.compression.parameter.lgwin", Brotli.DEFAULT_LGWIN))
        .setLgblock(getInitParameterAsInteger(filterConfig, "brotli.compression.parameter.lgblock", Brotli.DEFAULT_LGBLOCK));
  }

  private Brotli.Mode getInitParameterAsBrotliMode(FilterConfig filterConfig, String parameterName, Brotli.Mode defaultValue) {
    String initParameter = filterConfig.getInitParameter(parameterName);
    if (null != initParameter && !initParameter.trim().isEmpty()) {
      return Brotli.Mode.valueOf(initParameter.toUpperCase());
    }
    return defaultValue;
  }

  private int getInitParameterAsInteger(FilterConfig filterConfig, String parameterName, int defaultValue) {
    String initParameter = filterConfig.getInitParameter(parameterName);
    if (null != initParameter && !initParameter.trim().isEmpty()) {
      return Integer.parseInt(initParameter);
    }
    return defaultValue;
  }

}