/*
 * Copyright (c) 2015 MeteoGroup Deutschland GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.meteogroup.jbrotli;

import java.io.IOException;
import java.io.OutputStream;

public class BrotliOutputStream extends OutputStream {

  private final BrotliStreamCompressor brotliStreamCompressor;
  private OutputStream outputStream;

  public BrotliOutputStream(OutputStream outputStream) {
    this(outputStream, Brotli.DEFAULT_PARAMETER);
  }

  public BrotliOutputStream(OutputStream outputStream, Brotli.Parameter parameter) {
    this.outputStream = outputStream;
    brotliStreamCompressor = new BrotliStreamCompressor(parameter);
  }

  @Override
  public void write(int i) throws IOException {
    byte[] buf = new byte[]{(byte) (i & 0xff)};
    outputStream.write(brotliStreamCompressor.compress(buf, false));
  }

  @Override
  public void write(byte[] b) throws IOException {
    outputStream.write(brotliStreamCompressor.compress(b, false));
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    outputStream.write(brotliStreamCompressor.compress(b, off, len, false));
  }

  @Override
  public void flush() throws IOException {
    outputStream.write(brotliStreamCompressor.compress(new byte[0], true));
    outputStream.flush();
  }

  @Override
  public void close() throws IOException {
    flush();
    outputStream.close();
  }
}
