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

package org.meteogroup.jbrotli;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BrotliErrorCheckerTest {

  @Test
  public void error_codes_which_are_positive_are_OK() throws Exception {
    assertThat(BrotliErrorChecker.isBrotliOk(-1)).isFalse();
    assertThat(BrotliErrorChecker.isBrotliOk(0)).isTrue();
    assertThat(BrotliErrorChecker.isBrotliOk(1)).isTrue();
  }

  @Test(expectedExceptions = BrotliException.class)
  public void when_valid_error_exception_is_thrown() throws Exception {

    BrotliErrorChecker.assertBrotliOk(-1);

    // expect UncheckedIOException
  }

  @Test
  public void when_no_error_nothing_happens() throws Exception {

    BrotliErrorChecker.assertBrotliOk(0);
    BrotliErrorChecker.assertBrotliOk(1);

    // expect nothing happens
  }

  @Test
  public void assertBroltiOk_returns_the_error_code_if_all_ok() throws Exception {
    int actual = BrotliErrorChecker.assertBrotliOk(23_42);

    assertThat(actual).isEqualTo(23_42);
  }
}