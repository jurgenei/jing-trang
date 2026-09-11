package com.thaiopensource.datatype.xsd.regex.test;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegexIntegrationTest {
  private static final String JAVA_ENGINE = "com.thaiopensource.datatype.xsd.regex.java.RegexEngineImpl";
  private static final String XERCES_ENGINE = "com.thaiopensource.datatype.xsd.regex.xerces2.RegexEngineImpl";

  @Test
  void regextestPassesForJavaEngine() throws Exception {
    TestDriver.RunResult result = TestDriver.runSuite(JAVA_ENGINE, testResourceUri("regextest.xml"));
    assertEquals(0, result.failures);
    assertTrue(result.tests > 0);
  }

  @Test
  void regextestPassesForXercesEngine() throws Exception {
    TestDriver.RunResult result = TestDriver.runSuite(XERCES_ENGINE, testResourceUri("regextest.xml"));
    assertEquals(0, result.failures);
    assertTrue(result.tests > 0);
  }

  @Test
  void hardtestPassesForJavaEngine() throws Exception {
    TestDriver.RunResult result = TestDriver.runSuite(JAVA_ENGINE, testResourceUri("hardtest.xml"));
    assertEquals(0, result.failures);
    assertTrue(result.tests > 0);
  }


  private static String testResourceUri(String name) {
    URL resource = RegexIntegrationTest.class.getResource("/" + name);
    assertNotNull(resource, "Missing test resource: " + name);
    return resource.toExternalForm();
  }
}

