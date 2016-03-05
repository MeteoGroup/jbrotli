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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class BrotliServletResponseWrapper extends HttpServletResponseWrapper {

  private static final Brotli.Parameter BROTLI_COMRESSION_PARAMETER = new Brotli.Parameter().setQuality(5);

  private BrotliServletOutputStream brotliServletOutputStream = null;
  private PrintWriter printWriter = null;

  public BrotliServletResponseWrapper(HttpServletResponse response) throws IOException {
    super(response);
  }

  void close() throws IOException {
    if (this.printWriter != null) {
      this.printWriter.close();
    }
    if (this.brotliServletOutputStream != null) {
      this.brotliServletOutputStream.close();
    }
  }

  @Override
  public void flushBuffer() throws IOException {
    if (this.printWriter != null) {
      this.printWriter.flush();
    }
    try {
      if (this.brotliServletOutputStream != null) {
        this.brotliServletOutputStream.flush();
      }
    } finally {
      super.flushBuffer();
    }
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (this.printWriter != null) {
      throw new IllegalStateException("PrintWriter obtained already - cannot get OutputStream");
    }
    if (this.brotliServletOutputStream == null) {
      this.brotliServletOutputStream = new BrotliServletOutputStream(getResponse().getOutputStream(), BROTLI_COMRESSION_PARAMETER);
    }
    return this.brotliServletOutputStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (this.printWriter == null && this.brotliServletOutputStream != null) {
      throw new IllegalStateException("OutputStream obtained already - cannot get PrintWriter");
    }
    if (this.printWriter == null) {
      this.brotliServletOutputStream = new BrotliServletOutputStream(getResponse().getOutputStream(), BROTLI_COMRESSION_PARAMETER);
      this.printWriter = new PrintWriter(new OutputStreamWriter(this.brotliServletOutputStream, getResponse().getCharacterEncoding()));
    }
    return this.printWriter;
  }

  @Override
  public void setContentLength(int len) {
    //ignore, since content length of zipped content
    //does not match content length of unzipped content.
  }

}