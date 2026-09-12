package com.thaiopensource.datatype.xsd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrimitiveDatatypeBehaviorTest {
  @Test
  void booleanDatatypeAcceptsXmlSchemaBooleanLexicalForms() {
    BooleanDatatype datatype = new BooleanDatatype();

    assertTrue(datatype.lexicallyAllows("true"));
    assertTrue(datatype.lexicallyAllows("false"));
    assertTrue(datatype.lexicallyAllows("1"));
    assertTrue(datatype.lexicallyAllows("0"));
    assertFalse(datatype.lexicallyAllows("yes"));

    assertEquals(Boolean.TRUE, datatype.getValue("true", null));
    assertEquals(Boolean.TRUE, datatype.getValue("1", null));
    assertEquals(Boolean.FALSE, datatype.getValue("0", null));
  }

  @Test
  void anyUriDatatypeRejectsInvalidUriAndIsNotAlwaysValid() {
    AnyUriDatatype datatype = new AnyUriDatatype();

    assertTrue(datatype.lexicallyAllows("https://example.com/a?b=c"));
    assertFalse(datatype.lexicallyAllows("http://[invalid"));
    assertFalse(datatype.alwaysValid());
    assertEquals("uri", datatype.getLexicalSpaceKey());
  }
}

