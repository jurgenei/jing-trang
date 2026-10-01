package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.xml.util.Name;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record Wildcard(boolean positive, Set<String> namespaces, Set<Name> excludedNames) {
  public Wildcard(boolean positive, Set<String> namespaces, Set<Name> excludedNames) {
    this.positive = positive;
    this.namespaces = Collections.unmodifiableSet(namespaces);
    this.excludedNames = Collections.unmodifiableSet(excludedNames);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Wildcard(boolean positive1, Set<String> namespaces1, Set<Name> names)))
      return false;
    return (this.positive == positive1
      && this.namespaces.equals(namespaces1)
      && this.excludedNames.equals(names));
  }

  public int hashCode() {
    return namespaces.hashCode() ^ excludedNames.hashCode();
  }

  public boolean contains(Name name) {
    return namespaces.contains(name.getNamespaceUri()) == positive && !excludedNames.contains(name);
  }

  public static Wildcard union(Wildcard wc1, Wildcard wc2) {
    boolean positive;
    Set<String> namespaces = new HashSet<String>();
    if (wc1.positive() && wc2.positive()) {
      positive = true;
      namespaces.addAll(wc1.namespaces());
      namespaces.addAll(wc2.namespaces());
    } else {
      positive = false;
      if (!wc1.positive() && !wc2.positive()) {
        namespaces.addAll(wc1.namespaces());
        namespaces.retainAll(wc2.namespaces());
      } else if (!wc1.positive()) {
        namespaces.addAll(wc1.namespaces());
        namespaces.removeAll(wc2.namespaces());
      } else {
        namespaces.addAll(wc2.namespaces());
        namespaces.removeAll(wc1.namespaces());
      }
    }
    Set<Name> excludedNames = new HashSet<Name>();
    addExcludedNames(excludedNames, wc1, wc2);
    addExcludedNames(excludedNames, wc2, wc1);
    return new Wildcard(positive, namespaces, excludedNames);
  }

  /**
   * Add to result all members of the excludedNames of wc1 that are not contained in wc2.
   */
  private static void addExcludedNames(Set<Name> result, Wildcard wc1, Wildcard wc2) {
    for (Name name : wc1.excludedNames()) {
      if (!wc2.contains(name))
        result.add(name);
    }
  }
}
