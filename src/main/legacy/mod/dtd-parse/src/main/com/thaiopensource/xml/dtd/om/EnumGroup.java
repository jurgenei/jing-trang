package com.thaiopensource.xml.dtd.om;

public record EnumGroup(EnumGroupMember[] members) {

  @Override
  public EnumGroupMember[] members() {
    EnumGroupMember[] tem = new EnumGroupMember[members.length];
    System.arraycopy(members, 0, tem, 0, members.length);
    return tem;
  }

  public void accept(EnumGroupVisitor visitor) throws Exception {
    for (int i = 0; i < members.length; i++)
      members[i].accept(visitor);
  }
}
