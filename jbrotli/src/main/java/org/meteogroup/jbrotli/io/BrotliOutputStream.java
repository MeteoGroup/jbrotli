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

package org.meteogroup.jbrotli.io;

import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;

import java.io.IOException;
import java.io.OutputStream;

import static java.lang.Math.min;

public class BrotliOutputStream extends OutputStream {

  private final BrotliStreamCompressor brotliStreamCompressor;
  private final OutputStream outputStream;

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
    byte[] compressedBuf = brotliStreamCompressor.compressArray(buf, false);
    if (compressedBuf.length > 0) {
      outputStream.write(compressedBuf);
    }
  }

  @Override
  public void write(byte[] buffer) throws IOException {
    this.write(buffer, 0, buffer.length);
  }

  @Override
  public void write(byte[] buffer, int offset, int len) throws IOException {
    final int maxInputBufferSize = brotliStreamCompressor.getMaxInputBufferSize();
    while (len > 0) {
      final int stepSize = min(maxInputBufferSize, len);
      final boolean doFlush = len <= maxInputBufferSize;
      final byte[] compressedBuf = brotliStreamCompressor.compressArray(buffer, offset, stepSize, doFlush);
      if (compressedBuf.length > 0) {
        outputStream.write(compressedBuf);
      }
      offset += stepSize;
      len -= stepSize;
    }
  }

  @Override
  public void flush() throws IOException {
    outputStream.write(brotliStreamCompressor.compressArray(new byte[0], true));
    outputStream.flush();
  }

  @Override
  public void close() throws IOException {
    brotliStreamCompressor.close();
    outputStream.close();
  }
}
