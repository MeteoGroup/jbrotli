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

package org.meteogroup.jbrotli;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BrotliInputStream extends InputStream {

  static final int INTERNAL_UNCOMPRESSED_BUFFER_SIZE = 64 * 1024;

  private final BrotliStreamDeCompressor brotliStreamDeCompressor;
  private final InputStream inputStream;

  private InputStream uncompressedInputStreamDelegate = new ByteArrayInputStream(new byte[0]);
  private boolean isEndOfInputStream = false;

  public BrotliInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
    this.brotliStreamDeCompressor = new BrotliStreamDeCompressor();
  }

  @Override
  public int read() throws IOException {
    if (uncompressedInputStreamDelegate.available() == 0) {
      readChunkFromInput();
    }
    return uncompressedInputStreamDelegate.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    return super.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return super.read(b, off, len);
  }

  @Override
  public int available() throws IOException {
    return (isEndOfInputStream && uncompressedInputStreamDelegate.available() == 0) ? 0 : 1 + uncompressedInputStreamDelegate.available();
  }

  @Override
  public void close() throws IOException {
    inputStream.close();
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  @Override
  public long skip(long n) throws IOException {
    throw new UnsupportedOperationException("Sorry, but skip() isn't supported by this BrotliInputStream implementation.");
  }

  @Override
  public synchronized void mark(int readlimit) {
    throw new UnsupportedOperationException("Sorry, but mark() isn't supported by this BrotliInputStream implementation.");
  }

  @Override
  public synchronized void reset() throws IOException {
    throw new UnsupportedOperationException("Sorry, but reset() isn't supported by this BrotliInputStream implementation.");
  }

  private void readChunkFromInput() throws IOException {
    byte[] uncompressedBuffer = new byte[INTERNAL_UNCOMPRESSED_BUFFER_SIZE];
    if (brotliStreamDeCompressor.needsMoreOutput()) {
      int length = brotliStreamDeCompressor.deCompress(new byte[0], uncompressedBuffer);
      uncompressedInputStreamDelegate = new ByteArrayInputStream(uncompressedBuffer, 0, length);
      return;
    }

    byte[] in = new byte[4 * 1024];
    int uncompressedBufferPosition = 0;
    while (!isEndOfInputStream && (uncompressedBufferPosition == 0 || brotliStreamDeCompressor.needsMoreInput())) {
      int inLength = inputStream.read(in);
      if (inLength > 0) {
        uncompressedBufferPosition += brotliStreamDeCompressor.deCompress(in, 0, inLength, uncompressedBuffer, uncompressedBufferPosition, uncompressedBuffer.length - uncompressedBufferPosition);
      } else {
        isEndOfInputStream = true;
      }
    }
    uncompressedInputStreamDelegate = new ByteArrayInputStream(uncompressedBuffer, 0, uncompressedBufferPosition);
  }

}
