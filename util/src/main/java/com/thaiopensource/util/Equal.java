package com.thaiopensource.util;

import java.util.Objects;

public class Equal {
  private Equal() { }

  static public boolean equal(Object obj1, Object obj2) {
    return Objects.equals(obj1, obj2);
  }
}
