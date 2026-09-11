package com.thaiopensource.xml.out;

import org.junit.jupiter.api.Test;

import java.io.CharConversionException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlWriterTest {
  @Test
  void writesXmlDeclAndEscapedContent() throws Exception {
    StringWriter out = new StringWriter();
    XmlWriter writer = new XmlWriter(out, CharRepertoire.getInstance("US-ASCII"));
    writer.setNewline("\r\n");

    writer.writeXmlDecl("UTF-8");
    writer.startElement("root");
    writer.attribute("a", "<>&\"\n\t\r");
    writer.characters("A\u00E9", true);
    writer.endElement();

    String xml = out.toString();
    assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"));
    assertTrue(xml.contains("a=\"&lt;&gt;&amp;&quot;&#xA;&#x9;&#xD;\""));
    assertTrue(xml.contains(">&#x0041;&#x00E9;</root>\r\n"));
  }

  @Test
  void growsElementStackBeyondInitialCapacity() throws Exception {
    StringWriter out = new StringWriter();
    XmlWriter writer = new XmlWriter(out, CharRepertoire.getInstance("UTF-8"));

    int depth = 25;
    for (int i = 0; i < depth; i++) {
      writer.startElement("n" + i);
    }
    for (int i = 0; i < depth; i++) {
      writer.endElement();
    }

    String xml = out.toString();
    assertTrue(xml.contains("<n0>"));
    assertTrue(xml.contains("</n0>"));
  }

  @Test
  void writesCommentAndProcessingInstructionAroundElements() throws Exception {
    StringWriter out = new StringWriter();
    XmlWriter writer = new XmlWriter(out, CharRepertoire.getInstance("UTF-8"));

    writer.startElement("root");
    writer.startElement("child");
    writer.endElement();
    writer.comment("done");
    writer.processingInstruction("pi", "data");
    writer.endElement();

    String xml = out.toString();
    assertTrue(xml.contains("<!--done-->"));
    assertTrue(xml.contains("<?pi data?>"));
    assertTrue(xml.contains("<child/>"));
  }

  @Test
  void attributeOutsideStartTagThrows() throws Exception {
    StringWriter out = new StringWriter();
    XmlWriter writer = new XmlWriter(out, CharRepertoire.getInstance("UTF-8"));

    assertThrows(IllegalStateException.class, () -> writer.attribute("x", "y"));
  }

  @Test
  void malformedSurrogateInMarkupThrows() throws Exception {
    StringWriter out = new StringWriter();
    XmlWriter writer = new XmlWriter(out, CharRepertoire.getInstance("UTF-8"));

    assertThrows(CharConversionException.class, () -> writer.startElement("a\uD800"));
  }
}

