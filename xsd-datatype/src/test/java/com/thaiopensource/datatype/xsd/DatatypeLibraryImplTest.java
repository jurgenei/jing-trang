package com.thaiopensource.datatype.xsd;

import com.thaiopensource.datatype.xsd.regex.java.RegexEngineImpl;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeLibrary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatatypeLibraryImplTest {
  DatatypeLibrary lib = new DatatypeLibraryImpl(new RegexEngineImpl());
  @Test
  public void testCreateDatatype() throws DatatypeException {
    Datatype dt = lib.createDatatype("integer");
    try {
      dt.checkValid("foo", null);
    }
    catch (DatatypeException e) {
      String message = e.getMessage();
      assertNotNull(message);
      assertTrue(message.contains("integer"));
    }
  }

  @Test
  public void testCreateDatatypeBuilder() throws DatatypeException {
    DatatypeBuilder dtb = lib.createDatatypeBuilder("decimal");
    dtb.addParameter("fractionDigits", "2", null);
    dtb.addParameter("totalDigits", "3", null);
    dtb.addParameter("maxInclusive", "42", null);
    dtb.addParameter("minInclusive", "-17", null);
    Datatype dt = dtb.createDatatype();
    try {
      dt.checkValid("foo", null);
    }
    catch (DatatypeException e) {
      String message = e.getMessage();
      assertNotNull(message);
      assertTrue(message.contains("decimal"));
      assertFalse(message.contains("digits"));
    }
    try {
      dt.checkValid("47", null);
    }
    catch (DatatypeException e) {
      String message = e.getMessage();
      assertNotNull(message);
      assertTrue(message.contains("42"));
    }
    try {
      dt.checkValid("-30", null);
    }
    catch (DatatypeException e) {
      String message = e.getMessage();
      assertNotNull(message);
      assertTrue(message.contains("-17"));
    }
    try {
      dt.checkValid("0.123", null);
    }
    catch (DatatypeException e) {
      String message = e.getMessage();
      assertNotNull(message);
      assertTrue(message.contains("digits"));
      assertTrue(message.contains("point"));
      assertTrue(message.contains("3"));
      assertTrue(message.contains("2"));
    }
    try {
      dt.checkValid("10.12", null);
    }
    catch (DatatypeException e) {
      String message = e.getMessage();
      assertNotNull(message);
      assertTrue(message.contains("digits"));
      assertFalse(message.contains("point"));
      assertTrue(message.contains("3"));
      assertTrue(message.contains("4"));
    }
  }

  @Test
  public void testUnknownDatatypeRejected() {
    assertThrows(DatatypeException.class, () -> lib.createDatatype("notAType"));
  }

  @Test
  public void testRegexDatatypeFailsWithoutRegexEngine() {
    DatatypeLibrary withoutRegex = new DatatypeLibraryImpl(null);
    assertThrows(DatatypeException.class, () -> withoutRegex.createDatatypeBuilder("language"));
  }
}
