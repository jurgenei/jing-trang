package com.thaiopensource.util;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 *
 */
public class Utf8Test {
  @Test
  public void testEncode() throws UnsupportedEncodingException {
    for (int i = 0; i < 0x10FFFF; i++) {
      if (Utf16.isSurrogate(i))
        continue;
      char[] chars;
      if (i <= 0xFFFF)
        chars = new char[] { (char)i };
      else
      chars = new char[] { Utf16.surrogate1(i), Utf16.surrogate2(i) };
      assertArrayEquals(new String(chars).getBytes("UTF-8"), Utf8.encode(i));
    }
  }

}
