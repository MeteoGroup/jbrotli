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

package org.meteogroup.jbrotli.playground;

import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.scijava.nativelib.NativeLoader;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestSingleFileCompression {

  public static void main(String[] args) {
    try {
      new TestSingleFileCompression().run();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void run() throws Exception {
    NativeLoader.loadLibrary("brotli");

    byte[] data = loadDemoData();

    BrotliStreamCompressor brotliStreamCompressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);
    byte[] compressedBuffer = brotliStreamCompressor.compressArray(data, true);

    writeDemoDataCompressed(compressedBuffer);
  }

  private void writeDemoDataCompressed(byte[] compressedBuffer) throws IOException {
    try (FileOutputStream fileOutputStream = new FileOutputStream("cp.html.brotli");) {
      fileOutputStream.write(compressedBuffer, 0, compressedBuffer.length);
    }
  }

  private byte[] loadDemoData() throws IOException {
    InputStream inputStream = this.getClass().getResourceAsStream("/cp.html");
    return readAll(inputStream);
  }

  private byte[] readAll(InputStream inputStream) throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
      byte[] buf = new byte[16 * 1024];
      for (int read; (read = inputStream.read(buf)) > 0; ) {
        baos.write(buf, 0, read);
      }
      return baos.toByteArray();
    }
  }

}
