package com.thaiopensource.xml.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class NamingTest {
  @Test
  public void testIsQname() {
    assertFalse(Naming.isQname("foo::"));
  }

  @Test
  public void testIsQnameValidSamples() {
    assertTrue(Naming.isQname("doc"));
    assertTrue(Naming.isQname("x:doc"));
    assertFalse(Naming.isQname(":doc"));
    assertFalse(Naming.isQname("x:"));
  }

  @Test
  public void testIsNameAndNmtoken() {
    assertTrue(Naming.isName("_root"));
    assertTrue(Naming.isNmtoken("root-1.2"));
    assertFalse(Naming.isName("1root"));
    assertFalse(Naming.isNmtoken("root value"));
  }
}
