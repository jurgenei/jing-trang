package com.thaiopensource.xml.sax;

import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForkContentHandlerTest {
  @Test
  void forwardsAllEventsToBothHandlersInOrder() throws Exception {
    RecordingContentHandler left = new RecordingContentHandler();
    RecordingContentHandler right = new RecordingContentHandler();
    ForkContentHandler fork = new ForkContentHandler(left, right);

    Locator locator = new LocatorImpl();
    Attributes attributes = new AttributesImpl();
    char[] text = "payload".toCharArray();
    char[] ws = " ".toCharArray();

    fork.setDocumentLocator(locator);
    fork.startDocument();
    fork.startPrefixMapping("p", "urn:test");
    fork.startElement("urn:test", "node", "p:node", attributes);
    fork.characters(text, 1, 3);
    fork.ignorableWhitespace(ws, 0, ws.length);
    fork.processingInstruction("pi", "v=1");
    fork.skippedEntity("entity");
    fork.endElement("urn:test", "node", "p:node");
    fork.endPrefixMapping("p");
    fork.endDocument();

    assertEquals(left.events, right.events);
    assertEquals(List.of(
        "setDocumentLocator",
        "startDocument",
        "startPrefixMapping:p=urn:test",
        "startElement:p:node",
        "characters:ayl",
        "ignorableWhitespace:1",
        "processingInstruction:pi=v=1",
        "skippedEntity:entity",
        "endElement:p:node",
        "endPrefixMapping:p",
        "endDocument"
    ), left.events);
  }

  private static final class RecordingContentHandler implements ContentHandler {
    private final List<String> events = new ArrayList<>();

    @Override
    public void setDocumentLocator(Locator locator) {
      events.add("setDocumentLocator");
    }

    @Override
    public void startDocument() throws SAXException {
      events.add("startDocument");
    }

    @Override
    public void endDocument() throws SAXException {
      events.add("endDocument");
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
      events.add("startPrefixMapping:" + prefix + "=" + uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
      events.add("endPrefixMapping:" + prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      events.add("startElement:" + qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      events.add("endElement:" + qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      events.add("characters:" + new String(ch, start, length));
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      events.add("ignorableWhitespace:" + length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
      events.add("processingInstruction:" + target + "=" + data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
      events.add("skippedEntity:" + name);
    }
  }
}

