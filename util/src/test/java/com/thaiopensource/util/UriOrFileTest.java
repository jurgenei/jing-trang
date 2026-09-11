package com.thaiopensource.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UriOrFileTest {
  static Stream<String> files() {
    return Stream.of("foo", "./foo:bar", "foo\u0e01", "c:foo", "c:\\foo", "12:34");
  }

  @ParameterizedTest
  @MethodSource("files")
  public void testFileRoundTrip(String file) {
    assertEquals(new File(file).getAbsolutePath(), UriOrFile.uriToUriOrFile(UriOrFile.toUri(file)));
  }

  static Stream<String> uris() {
    return Stream.of("foo:bar", "http://www.example.com", "fo:o");
  }

  @ParameterizedTest
  @MethodSource("uris")
  public void testUriRoundTrip(String uri) {
    assertEquals(uri, UriOrFile.toUri(uri));
  }

  @ParameterizedTest
  @MethodSource("files")
  public void testToUri(String file) {
    assertEquals(new File(file).getAbsoluteFile().toURI().toString(), UriOrFile.toUri(file));
  }
}
