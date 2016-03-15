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
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrotliServletResponseWrapperTest {

  private BrotliServletResponseWrapper responseWrapper;

  @BeforeClass
  public void loadLibrary() throws Exception {
    BrotliLibraryLoader.loadBrotli();
  }

  @BeforeMethod
  public void setUp() throws Exception {
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    when(httpServletResponse.getCharacterEncoding()).thenReturn("UTF-8");
    responseWrapper = new BrotliServletResponseWrapper(httpServletResponse, Brotli.DEFAULT_PARAMETER);
  }

  @Test(expectedExceptions = IllegalStateException.class,
      expectedExceptionsMessageRegExp = "PrintWriter obtained already - cannot get OutputStream")
  public void when_there_is_a_printwriter__outputstream_cant_be_created() throws Exception {
    // when
    PrintWriter writer = responseWrapper.getWriter();
    assertThat(writer).isNotNull();

    responseWrapper.getOutputStream();
    // expected IllegalStateException
  }

  @Test(expectedExceptions = IllegalStateException.class,
      expectedExceptionsMessageRegExp = "OutputStream obtained already - cannot get PrintWriter")
  public void when_there_is_a_outputstream__printwriter_cant_be_created() throws Exception {
    // when
    OutputStream outputStream = responseWrapper.getOutputStream();
    assertThat(outputStream).isNotNull();

    responseWrapper.getWriter();
    // expected IllegalStateException
  }
}