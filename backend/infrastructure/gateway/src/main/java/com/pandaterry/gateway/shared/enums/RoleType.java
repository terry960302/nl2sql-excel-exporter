package com.pandaterry.gateway.shared.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    USER,
    OPERATOR,
    ADMIN,
    AGENT,
    ;

    public static final String PREFIX = "ROLE_";

    @Override
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
