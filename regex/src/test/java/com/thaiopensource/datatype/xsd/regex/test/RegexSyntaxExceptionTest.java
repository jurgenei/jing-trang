package com.thaiopensource.datatype.xsd.regex.test;

import com.thaiopensource.datatype.xsd.regex.RegexSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexSyntaxExceptionTest {
  @Test
  void singleArgumentConstructorUsesUnknownPosition() {
    RegexSyntaxException ex = new RegexSyntaxException("bad regex");

    assertEquals(RegexSyntaxException.UNKNOWN_POSITION, ex.getPosition());
    assertEquals("bad regex", ex.getMessage());
  }

  @Test
  void twoArgumentConstructorStoresPosition() {
    RegexSyntaxException ex = new RegexSyntaxException("bad regex", 7);

    assertEquals(7, ex.getPosition());
    assertEquals("bad regex", ex.getMessage());
  }
}

