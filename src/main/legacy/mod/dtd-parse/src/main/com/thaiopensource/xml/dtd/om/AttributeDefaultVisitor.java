package com.thaiopensource.xml.dtd.om;

public interface AttributeDefaultVisitor {
  void defaultValue(String value) throws Exception;
  void fixedValue(String value) throws Exception;
  void impliedValue() throws Exception;
  void requiredValue() throws Exception;
  void attributeDefaultRef(String name, AttributeDefault ad)
    throws Exception;
}
