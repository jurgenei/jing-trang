package com.thaiopensource.xml.dtd.parse;

record Atom(int tokenType, String token, Entity entity) {
  Atom(Entity entity) {
    this(-1, null, entity);
  }

  Atom(int tokenType, String token) {
    this(tokenType, token, null);
  }

  public int hashCode() {
    return token.hashCode();
  }

  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Atom(int type, String token1, Entity entity1)))
      return false;
    if (this.entity != null)
      return this.entity == entity1;
    else
      return this.tokenType == type && this.token.equals(token1);
  }
}

