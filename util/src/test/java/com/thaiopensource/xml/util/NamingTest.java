package com.thaiopensource.xml.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class NamingTest {
  @Test
  public void testIsQname() {
    assertFalse(Naming.isQname("foo::"));
  }
}
