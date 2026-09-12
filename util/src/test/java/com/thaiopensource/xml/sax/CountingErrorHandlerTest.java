package com.thaiopensource.xml.sax;

import org.junit.jupiter.api.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountingErrorHandlerTest {
  @Test
  void countsWarningErrorAndFatalEvents() throws Exception {
    CountingErrorHandler handler = new CountingErrorHandler();

    handler.warning(new SAXParseException("warn", null));
    handler.error(new SAXParseException("err", null));
    handler.fatalError(new SAXParseException("fatal", null));

    assertEquals(1, handler.getWarningCount());
    assertEquals(1, handler.getErrorCount());
    assertEquals(1, handler.getFatalErrorCount());
    assertTrue(handler.getHadErrorOrFatalError());
  }

  @Test
  void resetClearsAllCountersAndFlags() throws Exception {
    CountingErrorHandler handler = new CountingErrorHandler();
    handler.warning(new SAXParseException("warn", null));
    handler.error(new SAXParseException("err", null));

    handler.reset();

    assertEquals(0, handler.getWarningCount());
    assertEquals(0, handler.getErrorCount());
    assertEquals(0, handler.getFatalErrorCount());
    assertFalse(handler.getHadErrorOrFatalError());
  }

  @Test
  void forwardsCallsToConfiguredDelegate() throws Exception {
    RecordingErrorHandler delegate = new RecordingErrorHandler();
    CountingErrorHandler handler = new CountingErrorHandler(delegate);

    SAXParseException warning = new SAXParseException("warn", null);
    SAXParseException error = new SAXParseException("err", null);
    SAXParseException fatal = new SAXParseException("fatal", null);

    handler.warning(warning);
    handler.error(error);
    handler.fatalError(fatal);

    assertEquals(1, delegate.warningCount);
    assertEquals(1, delegate.errorCount);
    assertEquals(1, delegate.fatalCount);
    assertSame(warning, delegate.lastWarning);
    assertSame(error, delegate.lastError);
    assertSame(fatal, delegate.lastFatal);
    assertSame(delegate, handler.getErrorHandler());
  }

  @Test
  void setErrorHandlerReplacesDelegate() throws Exception {
    CountingErrorHandler handler = new CountingErrorHandler();
    RecordingErrorHandler delegate = new RecordingErrorHandler();

    handler.setErrorHandler(delegate);
    handler.warning(new SAXParseException("warn", null));

    assertSame(delegate, handler.getErrorHandler());
    assertEquals(1, delegate.warningCount);
  }

  private static final class RecordingErrorHandler implements ErrorHandler {
    private int warningCount;
    private int errorCount;
    private int fatalCount;
    private SAXParseException lastWarning;
    private SAXParseException lastError;
    private SAXParseException lastFatal;

    @Override
    public void warning(SAXParseException exception) throws SAXException {
      warningCount++;
      lastWarning = exception;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
      errorCount++;
      lastError = exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
      fatalCount++;
      lastFatal = exception;
    }
  }
}

