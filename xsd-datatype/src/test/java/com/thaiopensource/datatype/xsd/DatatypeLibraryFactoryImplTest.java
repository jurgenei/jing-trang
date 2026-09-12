package com.thaiopensource.datatype.xsd;

import com.thaiopensource.datatype.xsd.regex.java.RegexEngineImpl;
import com.thaiopensource.xml.util.WellKnownNamespaces;
import org.junit.jupiter.api.Test;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeLibrary;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatatypeLibraryFactoryImplTest {
  @Test
  void returnsNullForUnsupportedNamespace() {
    DatatypeLibraryFactoryImpl factory = new DatatypeLibraryFactoryImpl(new RegexEngineImpl());

    assertNull(factory.createDatatypeLibrary("urn:test:unsupported"));
  }

  @Test
  void cachesDatatypeLibraryInstance() {
    DatatypeLibraryFactoryImpl factory = new DatatypeLibraryFactoryImpl(new RegexEngineImpl());

    DatatypeLibrary first = factory.createDatatypeLibrary(WellKnownNamespaces.XML_SCHEMA_DATATYPES);
    DatatypeLibrary second = factory.createDatatypeLibrary(WellKnownNamespaces.XML_SCHEMA_DATATYPES);

    assertNotNull(first);
    assertSame(first, second);
  }

  @Test
  void explicitRegexEngineSupportsLanguageDatatype() throws Exception {
    DatatypeLibraryFactoryImpl factory = new DatatypeLibraryFactoryImpl(new RegexEngineImpl());
    DatatypeLibrary library = factory.createDatatypeLibrary(WellKnownNamespaces.XML_SCHEMA_DATATYPES);

    Datatype language = library.createDatatype("language");
    language.checkValid("en-US", null);
    assertThrows(DatatypeException.class, () -> language.checkValid("1nvalid", null));
  }
}

