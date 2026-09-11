package com.thaiopensource.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class UriEncoderTest {
  static Stream<String> createPaths() {
    return Stream.of(
            "foo",
            "<>\"{}|\\^`",
            "\r\n\t\u0001\u007f",
            " ",
            "\u0080\u009f",
            "\u00a0\u2000\u2028\u2029",
            "\u0e01"
    );
  }

  @ParameterizedTest
  @MethodSource("createPaths")
  public void testURIEncode(String path) throws URISyntaxException {
    assertEquals(new URI("http", "example.com", "/" + path, null).toString(),
            UriEncoder.encode("http://example.com/" + path));
  }

  @ParameterizedTest
  @MethodSource("createPaths")
  public void testURIEncodeAsAscii(String path) throws URISyntaxException {
    assertEquals(new URI("http", "example.com", "/" + path, null).toASCIIString(),
            UriEncoder.encodeAsAscii("http://example.com/" + path));
  }

  @Test
  public void testPercentEncode() {
    assertEquals("%00%7E%7F%80%FF", new String(UriEncoder.percentEncode(new byte[] {
            0x00, 0x7e, 0x7f, (byte)0x80, (byte)0xFF })));
  }
}
