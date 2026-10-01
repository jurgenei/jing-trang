package com.thaiopensource.xml.infer;

import com.thaiopensource.xml.util.Name;

public record AttributeDecl(Name datatype, boolean optional) {

  /**
   * @return null for anything
   */
  @Override
  public Name datatype() {
    return datatype;
  }
}
