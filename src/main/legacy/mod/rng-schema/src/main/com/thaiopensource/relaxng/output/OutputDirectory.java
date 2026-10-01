package com.thaiopensource.relaxng.output;

import com.thaiopensource.xml.out.CharRepertoire;

import java.io.Writer;
import java.io.IOException;

public interface OutputDirectory {
  record Stream(Writer writer, String encoding, CharRepertoire charRepertoire) {
  }
  Stream open(String sourceUri, String encoding) throws IOException;
  String reference(String fromSourceUri, String toSourceUri);
  String getLineSeparator();
  int getLineLength();
  void setLineLength(int lineLength);
  int getIndent();
  void setIndent(int indent);
  /**
   * This overrides the encoding specified with open.
   */
  void setEncoding(String encoding);
}
