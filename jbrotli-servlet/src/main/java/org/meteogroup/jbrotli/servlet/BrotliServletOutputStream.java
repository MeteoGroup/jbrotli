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
import org.meteogroup.jbrotli.io.BrotliOutputStream;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BrotliServletOutputStream extends ServletOutputStream {

  private final BrotliOutputStream brotliOutputStream;

  /**
   * uses brotli default compression parameter {@link org.meteogroup.jbrotli.Brotli#DEFAULT_PARAMETER}
   *
   * @param outputStream outputStream
   */
  public BrotliServletOutputStream(OutputStream outputStream) {
    this(outputStream, Brotli.DEFAULT_PARAMETER);
  }

  /**
   * @param outputStream outputStream
   * @param parameter    brotli compression parameter
   */
  public BrotliServletOutputStream(OutputStream outputStream, Brotli.Parameter parameter) {
    brotliOutputStream = new BrotliOutputStream(outputStream, parameter);
  }

  @Override
  public void write(int i) throws IOException {
    brotliOutputStream.write(i);
  }

  @Override
  public void write(byte[] buffer) throws IOException {
    brotliOutputStream.write(buffer);
  }

  @Override
  public void write(byte[] buffer, int offset, int len) throws IOException {
    brotliOutputStream.write(buffer, offset, len);
  }

  @Override
  public void flush() throws IOException {
    brotliOutputStream.flush();
  }

  @Override
  public void close() throws IOException {
    brotliOutputStream.close();
  }
}