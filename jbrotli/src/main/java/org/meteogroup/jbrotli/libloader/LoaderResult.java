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

import static java.lang.Boolean.TRUE;

class LoaderResult {

  private Boolean alreadyLoaded = null;
  private Boolean loadedFromSystemLibraryPath = null;
  private String nativeLibName;
  private String libNameWithinClasspath;
  private Boolean usedThisClassloader;
  private Boolean usedSystemClassloader;
  private Boolean madeReadable;
  private Boolean madeExecutable;
  private String temporaryLibFile;

  String asFormattedString() {
    String result = "";
    result += "os.name=\"" + System.getProperty("os.name") + "\"";
    result += ", ";
    result += "os.arch=\"" + System.getProperty("os.arch") + "\"";
    result += ", ";
    result += "os.version=\"" + System.getProperty("os.version") + "\"";
    result += ", ";
    result += "java.vm.name=\"" + System.getProperty("java.vm.name") + "\"";
    result += ", ";
    result += "java.vm.version=\"" + System.getProperty("java.vm.version") + "\"";
    result += ", ";
    result += "java.vm.vendor=\"" + System.getProperty("java.vm.vendor") + "\"";
    result += ", ";
    result += "alreadyLoaded=\"" + alreadyLoaded + "\"";
    if (loadedFromSystemLibraryPath != null) {
      result += ", ";
      result += "loadedFromSystemLibraryPath=\"" + loadedFromSystemLibraryPath + "\"";
    }
    if (nativeLibName != null) {
      result += ", ";
      result += "nativeLibName=\"" + nativeLibName + "\"";
    }
    if (temporaryLibFile != null) {
      result += ", ";
      result += "temporaryLibFile=\"" + temporaryLibFile + "\"";
    }
    if (libNameWithinClasspath != null) {
      result += ", ";
      result += "libNameWithinClasspath=\"" + libNameWithinClasspath + "\"";
    }
    if (usedThisClassloader != null) {
      result += ", ";
      result += "usedThisClassloader=\"" + usedThisClassloader + "\"";
    }
    if (usedSystemClassloader != null) {
      result += ", ";
      result += "usedSystemClassloader=\"" + usedSystemClassloader + "\"";
    }
    if (madeReadable != null) {
      result += ", ";
      result += "madeReadable=\"" + madeReadable + "\"";
    }
    if (madeExecutable != null) {
      result += ", ";
      result += "madeExecutable=\"" + madeExecutable + "\"";
    }
    result += ", ";
    result += "java.library.path=\"" + System.getProperty("java.library.path") + "\"";

    return String.format("[LoaderResult: %s]", result);
  }

  void setAlreadyLoaded(Boolean alreadyLoaded) {
    this.alreadyLoaded = alreadyLoaded;
  }

  void setLoadedFromSystemLibraryPath(Boolean loadedFromSystemLibraryPath) {
    this.loadedFromSystemLibraryPath = loadedFromSystemLibraryPath;
  }

  void setNativeLibName(String nativeLibName) {
    this.nativeLibName = nativeLibName;
  }

  void setLibNameWithinClasspath(String libNameWithinClasspath) {
    this.libNameWithinClasspath = libNameWithinClasspath;
  }

  void setTemporaryLibFile(String temporaryLibFile) {
    this.temporaryLibFile = temporaryLibFile;
  }

  void setUsedThisClassloader(boolean usedThisClassloader) {
    this.usedThisClassloader = usedThisClassloader;
  }

  boolean isUsedThisClassloader() {
    return TRUE.equals(usedThisClassloader);
  }

  void setUsedSystemClassloader(boolean usedSystemClassloader) {
    this.usedSystemClassloader = usedSystemClassloader;
  }

  boolean isUsedSystemClassloader() {
    return TRUE.equals(usedSystemClassloader);
  }

  void setMadeReadable(boolean madeReadable) {
    this.madeReadable = madeReadable;
  }

  void setMadeExecutable(boolean madeExecutable) {
    this.madeExecutable = madeExecutable;
  }
}
