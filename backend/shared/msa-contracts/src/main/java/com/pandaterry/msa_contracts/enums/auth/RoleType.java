package com.pandaterry.msa_contracts.enums.auth;

public enum RoleType {
    USER,
    OPERATOR,
    ADMIN,
    AGENT,
    ;

    public static final String PREFIX = "ROLE_";

    public String getAuthority() {
        return PREFIX + this.name();
    }

    public static RoleType from(String name){
        for(RoleType role : RoleType.values()){
            if(name.equals(role.name())){
                return role;
            }
        }
        throw new IllegalArgumentException("대응하는 RoleType enum이 없습니다.");
    }
}
