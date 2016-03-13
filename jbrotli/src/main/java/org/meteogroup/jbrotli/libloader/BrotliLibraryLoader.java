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

package org.meteogroup.jbrotli.libloader;

import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;

import java.io.*;
import java.nio.file.Files;

public class BrotliLibraryLoader {

  private static final String BROTLI_LIB_NAME = "brotli";

  /**
   * load native 'brotli' library ...
   *
   * @throws UnsatisfiedLinkError
   * @throws IllegalStateException
   * @throws SecurityException
   */
  public static synchronized void load() throws UnsatisfiedLinkError, IllegalStateException, SecurityException {
    LibraryLoader libraryLoader = new LibraryLoader(BROTLI_LIB_NAME);
    if (libraryLoader.tryAlreadyLoaded()) return;
    if (libraryLoader.trySystemLibraryLoading()) return;
    if (libraryLoader.tryLoadingFromTemporaryFolder()) return;
    String details = libraryLoader.getResult().asFormattedString();
    throw new UnsatisfiedLinkError("Couldn't load native library '" + BROTLI_LIB_NAME + "'. " + details);
  }

}