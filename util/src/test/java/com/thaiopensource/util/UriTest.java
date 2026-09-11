package com.thaiopensource.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
public class UriTest {
  static Stream<org.junit.jupiter.params.provider.Arguments> resolveData() {
    return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of("http://example.com/", "foo", "http://example.com/foo"),
            org.junit.jupiter.params.provider.Arguments.of("http://example.com/<>{}", "foo", "http://example.com/foo"),
            org.junit.jupiter.params.provider.Arguments.of("http://example.com/", "foo bar", "http://example.com/foo%20bar"),
            org.junit.jupiter.params.provider.Arguments.of("http://example.com/", "\u0e01", "http://example.com/\u0e01"),
            org.junit.jupiter.params.provider.Arguments.of("junk", "foo", "foo"),
            org.junit.jupiter.params.provider.Arguments.of("null", "foo", "foo")
    );
  }

  @ParameterizedTest
  @MethodSource("resolveData")
  public void testResolve(String base, String ref, String result) {
    assertEquals(result, Uri.resolve(base, ref));
  }

  static Stream<String> createValid() {
    return Stream.of("http://192.168.88.1", "", "random:stuff", "random:\u0e01", "foo");
  }

  @ParameterizedTest
  @MethodSource("createValid")
  public void testValid(String uri) {
    assertTrue(Uri.isValid(uri));
  }

  static Stream<String> createInvalid() {
    return Stream.of("foo%0G", "foo#bar#baz", "foo_bar:baz", "123:foo");
  }

  @ParameterizedTest
  @MethodSource("createInvalid")
  public void testInvalid(String uri) {
     assertFalse(Uri.isValid(uri));
  }
}
