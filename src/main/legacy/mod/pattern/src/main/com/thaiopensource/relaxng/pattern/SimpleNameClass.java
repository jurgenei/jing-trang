package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;


record SimpleNameClass(Name name) implements NameClass {

  public boolean contains(Name name) {
    return this.name.equals(name);
  }

  public int containsSpecificity(Name name) {
    return contains(name) ? SPECIFICITY_NAME : SPECIFICITY_NONE;
  }

  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof SimpleNameClass(Name name1)))
      return false;
    return name.equals(name1);
  }

  public void accept(NameClassVisitor visitor) {
    visitor.visitName(name);
  }

  public boolean isOpen() {
    return false;
  }
}
