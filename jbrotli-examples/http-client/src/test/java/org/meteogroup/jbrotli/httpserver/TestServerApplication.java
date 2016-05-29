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

package org.meteogroup.jbrotli.httpserver;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlet.FilterHolder;
import org.meteogroup.jbrotli.servlet.BrotliServletFilter;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static com.google.common.base.Preconditions.checkNotNull;

public class TestServerApplication extends Application<TestServerApplication.TestServerConfiguration> {

  public static void main(String[] args) throws Exception {
    new TestServerApplication().run(args);
  }

  @Override
  public String getName() {
    return "test-server";
  }

  @Override
  public void initialize(Bootstrap<TestServerConfiguration> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/public/", "/", "index.html"));
  }

  @Override
  public void run(TestServerConfiguration configuration, Environment environment) {
    final FilterHolder brotliFilterHolder = new FilterHolder(checkNotNull(new BrotliServletFilter()));
    brotliFilterHolder.setName("BrotliCompressionFilter");
    environment.getApplicationContext().getServletHandler().addFilter(brotliFilterHolder);
    brotliFilterHolder.getRegistration().addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
  }

  public static class TestServerConfiguration extends Configuration {

  }
}