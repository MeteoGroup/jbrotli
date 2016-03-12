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

import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Stack;

import static java.nio.file.Files.createTempDirectory;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractWebAppIntegrationTest {

  private static final String MAVEN_MODULE_NAME = "simple-web-app";
  private final String serverAddress = "127.0.0.1";

  protected String webAppBaseDir;
  protected Tomcat tomcat;

  @BeforeClass
  public void startTomcatServer() throws Throwable {
    webAppBaseDir = createTempDirectory("jbrotli-servlet-examples-test").toString();

    tomcat = new Tomcat();
    tomcat.setBaseDir(webAppBaseDir);
    tomcat.setPort(0);
    tomcat.getConnector().setProperty("address", serverAddress);
    tomcat.getHost().setAppBase(webAppBaseDir);
    tomcat.getHost().setAutoDeploy(false);
    tomcat.getHost().setDeployOnStartup(true);
    tomcat.addWebapp(tomcat.getHost(), "/", webAppBaseDir);
    doCopyAllFiles(findWebAppSourceFolder(), webAppBaseDir);
    tomcat.start();
  }

  @AfterClass
  public final void shutdownTomcatServer() throws Throwable {
    if (tomcat.getServer() != null && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
      if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
        tomcat.stop();
      }
      tomcat.destroy();
    }
  }

  protected String getServerIpAddress() {
    return serverAddress;
  }

  protected int getServerPort() {
    return tomcat.getConnector().getLocalPort();
  }

  private File findWebAppSourceFolder() throws IOException {
    File cwd = new File(".").getCanonicalFile();
    File webAppSourceFolder = checkForWebAppPath(cwd);
    Stack<File> subDirs = new Stack<>();
    subDirs.push(cwd);
    while (webAppSourceFolder == null && !subDirs.isEmpty()) {
      cwd = subDirs.pop();
      subDirs.addAll(listSubDirectories(cwd));
      webAppSourceFolder = checkForWebAppPath(cwd);
    }
    assertThat(webAppSourceFolder).isNotNull();
    assertThat(webAppSourceFolder.isDirectory()).isTrue();
    return webAppSourceFolder;
  }

  private void doCopyAllFiles(File source, String targetStr) throws IOException {
    for (File srcFile : listFilesOnly(source)) {
      File targetFile = new File(targetStr, srcFile.getName());
      targetFile.deleteOnExit(); // auto-cleanup ;-)
      try (FileOutputStream fos = new FileOutputStream(targetFile)) {
        Files.copy(srcFile.toPath(), fos);
      }
    }
    for (File subDir : listSubDirectories(source)) {
      File targetSubFolder = new File(targetStr, subDir.getName());
      assertThat(targetSubFolder.mkdir()).isTrue();
      doCopyAllFiles(new File(source, subDir.getName()), targetSubFolder.getAbsolutePath());
    }
  }

  private File checkForWebAppPath(File cwd) {
    String cwdPath = cwd.getAbsolutePath();
    if (cwdPath.contains(MAVEN_MODULE_NAME)) {
      cwdPath = cwdPath.substring(0, cwdPath.indexOf(MAVEN_MODULE_NAME) + MAVEN_MODULE_NAME.length());
      return joinPaths(cwdPath, "src", "main", "webapp");
    }
    return null;
  }

  private static List<File> listSubDirectories(File cwd) {
    return asList(cwd.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.isDirectory();
      }
    }));
  }

  private static List<File> listFilesOnly(File cwd) {
    return asList(cwd.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.isFile();
      }
    }));
  }

  private static File joinPaths(String... pathNames) {
    File f = new File(pathNames[0]);
    for (int i = 1; i < pathNames.length; i++) {
      f = new File(f, pathNames[i]);
    }
    return f;
  }
}
