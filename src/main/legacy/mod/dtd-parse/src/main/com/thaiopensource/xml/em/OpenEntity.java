package com.thaiopensource.xml.em;

import java.io.Reader;

/**
 * Information about an open external entity.
 * This is used to by <code>EntityManager</code> to return
 * information about an external entity that is has opened.
 *
 * @see EntityManager
 */
public record OpenEntity(Reader reader, String location, String baseUri, String encoding) {
  /**
   * Creates and initializes an <code>OpenEntity</code>. which uses
   */
  public OpenEntity {
  }

  /**
   * Returns an Reader containing the entity's bytes.
   * If this is called more than once on the same
   * OpenEntity, it will return the same Reader.
   */
  @Override
  public Reader reader() {
    return reader;
  }

  /**
   * Returns the URI to use as the base URI for resolving relative URIs
   * contained in the entity.
   */
  @Override
  public String baseUri() {
    return baseUri;
  }

  /**
   * Returns a string representation of the location of the entity
   * suitable for use in error messages.
   */
  @Override
  public String location() {
    return location;
  }

  /**
   * Returns the encoding used by the entity or null if the encoding
   * that was used is unknown.
   */
  @Override
  public String encoding() {
    return encoding;
  }

}
