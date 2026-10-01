package com.thaiopensource.xml.em;

public record ExternalId(String systemId, String publicId, String baseUri) {

  public ExternalId(String systemId) {
    this(systemId, null, null);
  }
}
