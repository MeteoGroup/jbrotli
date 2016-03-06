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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BrotliServletFilterTest {

  private BrotliServletFilter servletFilter;

  @BeforeMethod
  public void setUp() throws Exception {
    servletFilter = new BrotliServletFilter();
  }

  @Test
  public void testName() throws Exception {
    Map<String, String> filterConfig = new HashMap<>();
    filterConfig.put("brotli.compression.parameter.mode", "font");
    filterConfig.put("brotli.compression.parameter.quality", "11");
    filterConfig.put("brotli.compression.parameter.lgwin", "5");
    filterConfig.put("brotli.compression.parameter.lgblock", "7");

    servletFilter.init(new MockFilterConfig(filterConfig));

    assertThat(servletFilter.brotliCompressionParameter.getMode()).isEqualTo(Brotli.Mode.FONT);
    assertThat(servletFilter.brotliCompressionParameter.getQuality()).isEqualTo(11);
    assertThat(servletFilter.brotliCompressionParameter.getLgwin()).isEqualTo(5);
    assertThat(servletFilter.brotliCompressionParameter.getLgblock()).isEqualTo(7);
  }

  private static class MockFilterConfig implements FilterConfig {
    private Map<String, String> filterConfig;

    public MockFilterConfig(Map<String, String> filterConfig) {
      this.filterConfig = filterConfig;
    }

    @Override
    public String getFilterName() {
      throw new UnsupportedOperationException("Not implemented in this mock.");
    }

    @Override
    public ServletContext getServletContext() {
      throw new UnsupportedOperationException("Not implemented in this mock.");
    }

    @Override
    public String getInitParameter(String s) {
      return filterConfig.get(s);
    }

    @Override
    public Enumeration getInitParameterNames() {
      throw new UnsupportedOperationException("Not implemented in this mock.");
    }
  }
}