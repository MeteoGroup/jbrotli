/*
 * Copyright (c) 2016. MeteoGroup Deutschland GmbH
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.meteogroup.jbrotli.libloader.ARCH.*;
import static org.meteogroup.jbrotli.libloader.OS.*;

class LibraryLoader {

  private final String libName;
  private final LoaderResult loaderResult = new LoaderResult();

  LibraryLoader(String libName) {
    this.libName = libName;
  }

  boolean tryAlreadyLoaded() {
    try {
      new BrotliStreamCompressor(new Brotli.Parameter().setQuality(0)).close();
      loaderResult.setAlreadyLoaded(true);
      return true;
    } catch (UnsatisfiedLinkError e) {
      loaderResult.setAlreadyLoaded(false);
      return false;
    }
  }

  boolean trySystemLibraryLoading() {
    try {
      Runtime.getRuntime().loadLibrary(libName);
      loaderResult.setLoadedFromSystemLibraryPath(true);
      return true;
    } catch (UnsatisfiedLinkError e) {
      loaderResult.setLoadedFromSystemLibraryPath(false);
    }
    return false;
  }

  boolean tryLoadingFromTemporaryFolder() throws SecurityException, IllegalStateException {

    String nativeLibName = System.mapLibraryName(libName);
    loaderResult.setNativeLibName(nativeLibName);

    File tempFolder;
    try {
      tempFolder = Files.createTempDirectory(libName).toFile();
    } catch (IOException e) {
      throw new IllegalStateException("Can't create temporary folder. Make sure you have a temp. folder with write access available.", e);
    }

    File libFile = new File(tempFolder, nativeLibName);
    loaderResult.setTemporaryLibFile(libFile.getAbsolutePath());
    String libNameWithinClasspath = "/lib/" + determineOsArchName() + "/" + nativeLibName;
    loaderResult.setLibNameWithinClasspath(libNameWithinClasspath);
    try {
      loaderResult.setUsedThisClassloader(copyStreamToFile(getClass().getResourceAsStream(libNameWithinClasspath), libFile.toPath()));
      if (!loaderResult.isUsedThisClassloader()) {
        loaderResult.setUsedSystemClassloader(copyStreamToFile(getSystemClassLoader().getResourceAsStream(libNameWithinClasspath), libFile.toPath()));
      }
    } catch (IOException e) {
      throw new IllegalStateException("Can't write to " + libFile, e);
    }

    if (loaderResult.isUsedThisClassloader() || loaderResult.isUsedSystemClassloader()) {
      libFile.deleteOnExit();
      loaderResult.setMadeReadable(libFile.setReadable(true));
      loaderResult.setMadeExecutable(libFile.setExecutable(true));
      Runtime.getRuntime().load(libFile.getAbsolutePath());
      return true;
    }
    return false;
  }

  private boolean copyStreamToFile(InputStream inputStream, Path targetPath) throws IOException {
    if (inputStream == null) return false;
    Files.copy(inputStream, targetPath);
    inputStream.close();
    return true;
  }

  LoaderResult getResult() {
    return loaderResult;
  }

  private String determineOsArchName() {
    return determineOS() + "-" + determineArch();
  }

  private String determineOS() {
    String osName = System.getProperty("os.name").toLowerCase(Locale.US);
    if (LINUX.matches(osName)) return LINUX.name;
    if (WIN32.matches(osName)) return WIN32.name;
    if (OSX.matches(osName)) return OSX.name;
    return osName;
  }

  private String determineArch() {
    String osArch = System.getProperty("os.arch").toLowerCase(Locale.US);
    if (X86_AMD64.matches(osArch)) return X86_AMD64.name;
    if (X86.matches(osArch)) return X86.name;
    if (ARM32_VFP_HFLT.matches(osArch)) return ARM32_VFP_HFLT.name;
    return osArch;
  }

}
