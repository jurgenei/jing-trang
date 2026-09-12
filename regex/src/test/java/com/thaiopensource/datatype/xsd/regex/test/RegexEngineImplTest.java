package com.thaiopensource.datatype.xsd.regex.test;

import com.thaiopensource.datatype.xsd.regex.Regex;
import com.thaiopensource.datatype.xsd.regex.RegexSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegexEngineImplTest {
  @Test
  void javaEngineCompilesAndMatches() throws Exception {
    com.thaiopensource.datatype.xsd.regex.java.RegexEngineImpl engine =
        new com.thaiopensource.datatype.xsd.regex.java.RegexEngineImpl();

    Regex regex = engine.compile("[a-z]{2}[0-9]");

    assertTrue(regex.matches("ab3"));
  }

  @Test
  void javaEngineRejectsInvalidPattern() {
    com.thaiopensource.datatype.xsd.regex.java.RegexEngineImpl engine =
        new com.thaiopensource.datatype.xsd.regex.java.RegexEngineImpl();

    RegexSyntaxException ex = assertThrows(RegexSyntaxException.class, () -> engine.compile("["));
    assertNotNull(ex.getMessage());
  }

  @Test
  void xercesEngineRejectsInvalidPattern() {
    com.thaiopensource.datatype.xsd.regex.xerces2.RegexEngineImpl engine =
        new com.thaiopensource.datatype.xsd.regex.xerces2.RegexEngineImpl();

    RegexSyntaxException ex = assertThrows(RegexSyntaxException.class, () -> engine.compile("["));
    assertNotNull(ex.getMessage());
  }
}

