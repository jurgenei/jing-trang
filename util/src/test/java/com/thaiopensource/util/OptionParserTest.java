package com.thaiopensource.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OptionParserTest {
  @Test
  void parsesCombinedAndSeparateOptionArguments() throws Exception {
    OptionParser parser = new OptionParser("abc:", new String[]{"-ab", "-c", "value", "rest"});

    List<String> seen = new ArrayList<>();
    while (parser.moveToNextOption()) {
      seen.add(parser.getOptionCharString() + "=" + parser.getOptionArg());
    }

    assertEquals(List.of("a=null", "b=null", "c=value"), seen);
    assertArrayEquals(new String[]{"rest"}, parser.getRemainingArgs());
  }

  @Test
  void parsesAttachedOptionArgument() throws Exception {
    OptionParser parser = new OptionParser("c:", new String[]{"-cinline", "rest"});

    assertEquals(true, parser.moveToNextOption());
    assertEquals('c', parser.getOptionChar());
    assertEquals("inline", parser.getOptionArg());
    assertEquals(false, parser.moveToNextOption());
    assertArrayEquals(new String[]{"rest"}, parser.getRemainingArgs());
  }

  @Test
  void stopsAtDoubleDash() throws Exception {
    OptionParser parser = new OptionParser("a", new String[]{"-a", "--", "-a", "tail"});

    assertEquals(true, parser.moveToNextOption());
    assertEquals('a', parser.getOptionChar());
    assertNull(parser.getOptionArg());
    assertEquals(false, parser.moveToNextOption());
    assertArrayEquals(new String[]{"-a", "tail"}, parser.getRemainingArgs());
  }

  @Test
  void throwsOnInvalidOption() {
    OptionParser parser = new OptionParser("a", new String[]{"-z"});
    assertThrows(OptionParser.InvalidOptionException.class, parser::moveToNextOption);
  }

  @Test
  void throwsOnMissingOptionArgument() {
    OptionParser parser = new OptionParser("a:", new String[]{"-a"});
    assertThrows(OptionParser.MissingArgumentException.class, parser::moveToNextOption);
  }
}

