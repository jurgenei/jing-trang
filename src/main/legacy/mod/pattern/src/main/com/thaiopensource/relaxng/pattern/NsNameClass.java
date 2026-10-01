package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

record NsNameClass(String namespaceUri) implements NameClass {

  public boolean contains(Name name) {
    return this.namespaceUri.equals(name.getNamespaceUri());
  }

  public int containsSpecificity(Name name) {
    return contains(name) ? SPECIFICITY_NS_NAME : SPECIFICITY_NONE;
  }

  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof NsNameClass))
      return false;
    return namespaceUri.equals(((NsNameClass) obj).namespaceUri);
  }

  public void accept(NameClassVisitor visitor) {
    visitor.visitNsName(namespaceUri);
  }

  public boolean isOpen() {
    return true;
  }
}
