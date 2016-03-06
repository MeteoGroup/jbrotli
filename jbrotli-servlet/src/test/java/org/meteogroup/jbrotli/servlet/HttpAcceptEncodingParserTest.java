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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpAcceptEncodingParserTest {

  private HttpAcceptEncodingParser parser;

  @BeforeMethod
  public void setUp() throws Exception {
    parser = new HttpAcceptEncodingParser();
  }

  @DataProvider
  public static Object[][] acceptEncodingHeaderStrings() {
    return new Object[][]{
        {"gzip, compress", false},
        {"gzip, br, compress", true},
        {"gzip, br;q=1, compress", true},
        {"gzip, br;q=.1", true},
        {"gzip, br;q=?", false},
        {"gzip, br; q=1, compress", true},
        {"gzip, br ; q=1 , compress", true},
        {"gzip, br ; q= 1 , compress", true},
        {"gzip, br ; q = 1 , compress", false},
        {"gzip, brotli;q=1, compress", false},
        {"gzip, br;q=0.001, compress", true},
        {"gzip, br;q=0, compress", false},
        {"gzip, br;q=0.000, compress", false},
        {"gzip, *;q=1", false},
    };
  }

  @Test(dataProvider = "acceptEncodingHeaderStrings")
  public void check_if_brotli_content_is_accepted_by_the_Accept_Encoding(String headerString, boolean expectBrotliAccepted) {
    // when
    assertThat(parser.acceptBrotliEncoding(headerString)).isEqualTo(expectBrotliAccepted);
  }

  @Test
  public void parser_is_null_safe() throws Exception {
    assertThat(parser.acceptBrotliEncoding((String)null)).isFalse();
  }
}