package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.util.Equal;

public record Annotation(String documentation) {

  public boolean equals(Object obj) {
    return obj instanceof Annotation && Equal.equal(documentation, ((Annotation) obj).documentation);
  }

}
