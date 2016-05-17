package org.meteogroup.jbrotli;

public class BrotliStreamDeCompressorResult {
  final int errorCode;
  final int bytesConsumed;
  final int bytesProduced;

  public BrotliStreamDeCompressorResult(int errorCode, int bytesConsumed, int bytesProduced) {
    this.errorCode = errorCode;
    this.bytesConsumed = bytesConsumed;
    this.bytesProduced = bytesProduced;
  }
}
