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

import javax.servlet.http.HttpServletRequest;

import static java.lang.Float.parseFloat;
import static org.meteogroup.jbrotli.servlet.BrotliServletFilter.BROTLI_HTTP_CONTENT_CODING;

class HttpAcceptEncodingParser {

  private static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
  private static final String CODING_SEPARATOR = ",";
  private static final String CODING_QVALUE_SEPARATOR = ";";
  private static final String QVALUE_PREFIX = "q=";

  boolean acceptBrotliEncoding(HttpServletRequest httpRequest) {
    return acceptBrotliEncoding(httpRequest.getHeader(HTTP_HEADER_ACCEPT_ENCODING));
  }

  boolean acceptBrotliEncoding(String headerString) {
    if (null != headerString) {
      String[] weightedCodings = headerString.split(CODING_SEPARATOR, 0);
      for (String weightedCoding : weightedCodings) {
        String[] coding_and_qvalue = weightedCoding.trim().split(CODING_QVALUE_SEPARATOR, 2);
        if (coding_and_qvalue.length > 0) {
          if (BROTLI_HTTP_CONTENT_CODING.equals(coding_and_qvalue[0].trim())) {
            if (coding_and_qvalue.length == 1) {
              return true;
            } else {
              String qvalue = coding_and_qvalue[1].trim();
              if (qvalue.startsWith(QVALUE_PREFIX)) {
                try {
                  return parseFloat(qvalue.substring(2).trim()) > 0;
                } catch (NumberFormatException e) {
                  return false;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }
}
