package com.thaiopensource.xml.sax;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorHandlerImplTest {
  @Test
  void formatsWarningAndErrorWithLocation() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ErrorHandlerImpl handler = new ErrorHandlerImpl(out);

    SAXParseException ex = new SAXParseException("bad content", null, "file:/tmp/doc.xml", 12, 8);
    handler.warning(ex);
    handler.error(ex);

    String text = out.toString(StandardCharsets.UTF_8);
    assertTrue(text.contains("warning"));
    assertTrue(text.contains("error"));
    assertTrue(text.contains("doc.xml"));
    assertTrue(text.contains("12"));
    assertTrue(text.contains("8"));
  }

  @Test
  void fatalErrorRethrowsSameException() {
    ErrorHandlerImpl handler = new ErrorHandlerImpl(new ByteArrayOutputStream());
    SAXParseException ex = new SAXParseException("fatal", null, null, 2, 3);

    SAXParseException thrown = assertThrows(SAXParseException.class, () -> handler.fatalError(ex));
    assertSame(ex, thrown);
  }

  @Test
  void printExceptionHandlesWrappedAndFileNotFoundCases() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ErrorHandlerImpl handler = new ErrorHandlerImpl(out);

    handler.printException(new SAXException("outer", new IllegalStateException("inner")));
    handler.printException(new FileNotFoundException("missing.txt"));

    String text = out.toString(StandardCharsets.UTF_8);
    assertTrue(text.contains("outer"));
    assertTrue(text.contains("file not found"));
    assertTrue(text.contains("missing.txt"));
  }
}

