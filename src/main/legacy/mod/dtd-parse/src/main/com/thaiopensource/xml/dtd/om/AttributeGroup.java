package com.thaiopensource.xml.dtd.om;

public record AttributeGroup(AttributeGroupMember[] members) {

  @Override
  public AttributeGroupMember[] members() {
    AttributeGroupMember[] tem = new AttributeGroupMember[members.length];
    System.arraycopy(members, 0, tem, 0, members.length);
    return tem;
  }

  public void accept(AttributeGroupVisitor visitor) throws Exception {
    for (int i = 0; i < members.length; i++)
      members[i].accept(visitor);
  }
}
