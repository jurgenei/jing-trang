package com.thaiopensource.relaxng.jaxp;

import com.thaiopensource.validation.LSInputImpl;
import com.thaiopensource.validation.SchemaFactory2;
import com.thaiopensource.xml.sax.DraconianErrorHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test SchemaFactoryImpl.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SchemaFactoryImplTest {
  protected final Class<? extends SchemaFactory2> factoryClass;
  private static int filenameIndex = 0;

  protected SchemaFactoryImplTest(Class<? extends SchemaFactory2> factoryClass) {
    this.factoryClass = factoryClass;
  }

  protected SchemaFactory2 factory() {
    try {
      return factoryClass.getDeclaredConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  @ParameterizedTest
  @MethodSource("valid")
  public void testValidCharStream(String schemaString, String docString) throws SAXException, IOException {
    factory().newSchema(charStreamSource(schemaString)).newValidator().validate(charStreamSource(docString));
  }

  @ParameterizedTest
  @MethodSource("valid")
  public void testValidFile(String schemaString, String docString) throws SAXException, IOException {
    factory().newSchema(fileSource(schemaString)).newValidator().validate(fileSource(docString));
  }

  protected Stream<Arguments> valid() {
    return Stream.of(
            Arguments.of(createSchema("doc"), "<doc/>"),
            Arguments.of(element("doc", new String[]{attribute("att")}), "<doc att='val'/>")
    );
  }

  private static SAXSource charStreamSource(String s) {
    return new SAXSource(new InputSource(new StringReader(s)));
  }

  private static synchronized Source fileSource(String s) throws IOException {
    final File file = new File("t" + filenameIndex++);
    Writer w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
    w.write(s);
    w.close();
    return new StreamSource(file);
  }

  private static class CountErrorHandler extends DraconianErrorHandler {
    int errorCount = 0;

    public void error(SAXParseException e) throws SAXException {
      ++errorCount;
    }
  }

  @Test
  public void testErrorHandlerNoThrow() throws SAXException, IOException {
    SchemaFactory f = factory();
    Validator v = f.newSchema(charStreamSource(createSchema("doc"))).newValidator();
    CountErrorHandler eh = new CountErrorHandler() {
      public void error(SAXParseException e) throws SAXException {
        if (errorCount == 0) {
          assertEquals(2, e.getLineNumber());
        }
        super.error(e);
      }
    };
    v.setErrorHandler(eh);
    assertSame(eh, v.getErrorHandler());
    v.validate(charStreamSource("<doc>\n<bad/></doc>"));
    assertTrue(eh.errorCount > 0);
  }

  @Test
  public void testErrorHandlerThrowRuntime() {
    assertThrows(RuntimeException.class, () -> {
      SchemaFactory f = factory();
      Validator v = f.newSchema(charStreamSource(createSchema("doc"))).newValidator();
      v.setErrorHandler(new DraconianErrorHandler() {
        public void error(SAXParseException e) throws SAXException {
          assertEquals(2, e.getLineNumber());
          throw new RuntimeException();
        }
      });
      v.validate(charStreamSource("<doc>\n<bad/></doc>"));
    });
  }

  static class MySAXException extends SAXException {
  }

  @Test
  public void testErrorHandlerThrowSAX() {
    assertThrows(MySAXException.class, () -> {
      SchemaFactory f = factory();
      Validator v = f.newSchema(charStreamSource(createSchema("doc"))).newValidator();
      v.setErrorHandler(new DraconianErrorHandler() {
        public void error(SAXParseException e) throws SAXException {
          assertEquals(2, e.getLineNumber());
          throw new MySAXException();
        }
      });
      v.validate(charStreamSource("<doc>\n<bad/></doc>"));
    });
  }

  @Test
  public void testInstanceResourceResolver() throws SAXException, IOException {
    SchemaFactory f = factory();
    Validator v = f.newSchema(charStreamSource(element("doc", element("inner")))).newValidator();
    assertNull(v.getResourceResolver());
    LSResourceResolver rr = new LSResourceResolver() {
      public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        int slashIndex = systemId.lastIndexOf('/');
        if (slashIndex >= 0) {
          systemId = systemId.substring(slashIndex + 1);
        }
        assertEquals("e.xml", systemId);
        assertEquals("http://www.w3.org/TR/REC-xml", type);
        LSInput in = new LSInputImpl();
        in.setStringData("<inner/>");
        return in;
      }
    };
    v.setResourceResolver(rr);
    assertSame(rr, v.getResourceResolver());
    v.validate(charStreamSource("<!DOCTYPE doc [ <!ENTITY e SYSTEM 'e.xml'> ]><doc>&e;</doc>"));
  }

  @Test
  public void testSchemaResourceResolver() throws SAXException, IOException {
    SchemaFactory f = factory();
    assertNull(f.getResourceResolver());
    LSResourceResolver rr = new LSResourceResolver() {
      public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        assertEquals("myschema", systemId);
        assertEquals(getLSType(), type);
        assertNull(baseURI);
        assertNull(namespaceURI);
        assertNull(publicId);
        LSInput in = new LSInputImpl();
        in.setStringData(createSchema("doc"));
        return in;
      }
    };
    f.setResourceResolver(rr);
    assertSame(rr, f.getResourceResolver());
    Validator v = f.newSchema(charStreamSource(externalRef("myschema"))).newValidator();
    v.validate(charStreamSource("<doc/>"));
  }

  @Test
  public void testNewSchemaNoArgs() {
    assertThrows(UnsupportedOperationException.class, () -> factory().newSchema());
  }

  Stream<Arguments> supportedFeatures() {
    return Stream.of(Arguments.of(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.FALSE));
  }

  @ParameterizedTest
  @MethodSource("supportedFeatures")
  public void testSupportedFeatures(String feature, Boolean defaultValueObj)
          throws SAXNotRecognizedException, SAXNotSupportedException {
    SchemaFactory f = factory();
    boolean defaultValue = defaultValueObj;
    assertEquals(defaultValue, f.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING));
    f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, !defaultValue);
    assertEquals(!defaultValue, f.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING));
  }

  @ParameterizedTest
  @MethodSource("supportedFeatures")
  public void testFeatureInheritance(String feature, Boolean defaultValueObj)
          throws SAXException, SAXNotRecognizedException, SAXNotSupportedException {
    SchemaFactory f = factory();
    boolean defaultValue = defaultValueObj;
    assertEquals(defaultValue, f.getFeature(feature));
    f.setFeature(feature, !defaultValue);
    ValidatorHandler vh = f.newSchema(charStreamSource(createSchema("doc"))).newValidatorHandler();
    assertEquals(defaultValue, vh.getFeature(feature));
  }

  @Test
  public void testUnrecognizedGetFeature() {
    SchemaFactory f = factory();
    assertThrows(SAXNotRecognizedException.class,
            () -> f.getFeature("http://thaiopensource.com/features/no-such-feature"));
  }

  @Test
  public void testUnrecognizedSetFeature() {
    SchemaFactory f = factory();
    assertThrows(SAXNotRecognizedException.class,
            () -> f.setFeature("http://thaiopensource.com/features/no-such-feature", false));
  }

  @Test
  public void testNullGetFeature() {
    SchemaFactory f = factory();
    assertThrows(NullPointerException.class, () -> f.getFeature(null));
  }

  @Test
  public void testNullSetFeature() {
    SchemaFactory f = factory();
    assertThrows(NullPointerException.class, () -> f.setFeature(null, true));
  }

  @Test
  public void testUnrecognizedSetProperty() {
    SchemaFactory f = factory();
    assertThrows(SAXNotRecognizedException.class,
            () -> f.setProperty("http://thaiopensource.com/properties-no-such-property", null));
  }

  @Test
  public void testUnrecognizedGetProperty() {
    SchemaFactory f = factory();
    assertThrows(SAXNotRecognizedException.class,
            () -> f.getProperty("http://thaiopensource.com/properties/no-such-property"));
  }

  private String createSchema(String rootElement) {
    return element(rootElement);
  }

  private String element(String name) {
    return element(name, new String[]{});
  }

  private String element(String name, String contentPattern) {
    return element(name, new String[]{contentPattern});
  }

  protected abstract String element(String name, String[] contentPatterns);

  protected abstract String attribute(String name);

  protected abstract String externalRef(String uri);

  protected abstract String getLSType();
}
