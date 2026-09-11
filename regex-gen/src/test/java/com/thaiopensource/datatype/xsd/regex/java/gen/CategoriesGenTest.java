package com.thaiopensource.datatype.xsd.regex.java.gen;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriesGenTest {
  @Test
  void loadAndSaveWritesExpectedRanges() throws Exception {
    String unicodeData = String.join("\n",
        "10000;<SAMPLE, First>;Lu;0;L;;;;;N;;;;;",
        "10001;<SAMPLE, Last>;Lu;0;L;;;;;N;;;;;",
        "10002;LOWER_A;Ll;0;L;;;;;N;;;;;",
        "10003;LOWER_B;Ll;0;L;;;;;N;;;;;",
        "0041;BMP_SKIPPED;Lu;0;L;;;;;N;;;;;"
    );

    CategoriesGen gen = new CategoriesGen();
    gen.load(new BufferedReader(new StringReader(unicodeData)));

    StringWriter out = new StringWriter();
    gen.save(out, "\n");
    String text = out.toString();

    assertTrue(text.contains("CATEGORY_NAMES"));
    assertTrue(text.contains("Lu"));
    assertTrue(text.contains("Ll"));
    assertTrue(text.contains("0x10000, 0x10001"));
    assertTrue(text.contains("0x10002, 0x10003"));
    assertTrue(!text.contains("0x41"));
  }

  @Test
  void addAppendsRangesForSameCategory() throws Exception {
    CategoriesGen gen = new CategoriesGen();
    gen.add(0x10010, 0x1001F, "Lu");
    gen.add(0x10030, 0x1003F, "Lu");

    StringWriter out = new StringWriter();
    gen.save(out, "\n");
    String text = out.toString();

    assertTrue(text.contains("// Lu"));
    assertTrue(text.contains("0x10010, 0x1001f"));
    assertTrue(text.contains("0x10030, 0x1003f"));
  }
}

