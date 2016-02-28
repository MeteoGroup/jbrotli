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

public class Brotli {

  public static final String BROTLI_VERSION = "HEAD-2015-12-11-commit_90eb91b04cc61c2562b92ba2aa6f59a7dc3950b3";

  public static final Mode DEFAULT_MODE = Mode.GENERIC;
  public static final int DEFAULT_QUALITY = 11;
  public static final int DEFAULT_LGWIN = 22;
  public static final int DEFAULT_LGBLOCK = 0;
  public static final Parameter DEFAULT_PARAMETER = new Parameter(DEFAULT_MODE, DEFAULT_QUALITY, DEFAULT_LGWIN, DEFAULT_LGBLOCK);

  public static class Parameter {

    private Mode mode = DEFAULT_MODE;
    private int quality = DEFAULT_QUALITY;
    private int lgwin = DEFAULT_LGWIN;
    private int lgblock = DEFAULT_LGBLOCK;

    /**
     * Use default parameter settings
     */
    public Parameter() {
    }

    /**
     * @param mode    mode
     * @param quality quality
     * @param lgwin   lgwin
     * @param lgblock lgblock
     */
    public Parameter(Mode mode, int quality, int lgwin, int lgblock) {
      this.mode = mode;
      this.quality = quality;
      this.lgwin = lgwin;
      this.lgblock = lgblock;
    }

    /**
     * @return mode
     */
    public Mode getMode() {
      return mode;
    }

    /**
     * @param mode the mode, default {@link Brotli#DEFAULT_MODE}
     * @return this, for fluent writing
     */
    public Parameter setMode(Mode mode) {
      this.mode = mode;
      return this;
    }

    /**
     * Controls the compression-speed vs compression-density tradeoffs. The higher the quality, the slower the compression. Range is 0 to 11.
     *
     * @return quality
     */
    public int getQuality() {
      return quality;
    }

    /**
     * Controls the compression-speed vs compression-density tradeoffs. The higher the quality, the slower the compression. Range is 0 to 11.
     *
     * @param quality range 0..11, default {@link Brotli#DEFAULT_QUALITY}
     * @return this, for fluent writing
     */
    public Parameter setQuality(int quality) {
      this.quality = quality;
      return this;
    }

    /**
     * Base 2 logarithm of the sliding window size. Range is 10 to 24.
     *
     * @return lgwin
     */
    public int getLgwin() {
      return lgwin;
    }

    /**
     * Base 2 logarithm of the sliding window size. Range is 10 to 24.
     *
     * @param lgwin range 10..24, default {@link Brotli#DEFAULT_LGWIN}
     * @return this, for fluent writing
     */
    public Parameter setLgwin(int lgwin) {
      this.lgwin = lgwin;
      return this;
    }

    /**
     * Base 2 logarithm of the maximum input block size. Range is 16 to 24. If set to 0, the value will be set based on the quality.
     *
     * @return lbblock
     */
    public int getLgblock() {
      return lgblock;
    }

    /**
     * Base 2 logarithm of the maximum input block size. Range is 16 to 24. If set to 0, the value will be set based on the quality.
     *
     * @param lgblock range 16..24, default {@link Brotli#DEFAULT_LGBLOCK}
     * @return this, for fluent writing
     */
    public Parameter setLgblock(int lgblock) {
      this.lgblock = lgblock;
      return this;
    }
  }

  public enum Mode {

    /**
     * Default compression mode. The compressor does not know anything in advance about the properties of the input.
     */
    GENERIC(0),

    /**
     * Compression mode for UTF-8 format text input.
     */
    TEXT(1),

    /**
     * Compression mode used in WOFF 2.0.
     */
    FONT(2);

    public int mode;

    Mode(int mode) {
      this.mode = mode;
    }
  }
}
