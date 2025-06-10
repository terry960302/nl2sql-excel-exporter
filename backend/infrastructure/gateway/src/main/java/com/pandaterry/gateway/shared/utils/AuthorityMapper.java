package com.pandaterry.gateway.shared.utils;

import com.pandaterry.msa_contracts.enums.auth.RoleType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class AuthorityMapper {
    public static SimpleGrantedAuthority toGranted(RoleType role){
        return new SimpleGrantedAuthority(role.getAuthority());
    }
    public static List<SimpleGrantedAuthority> toGranted(List<RoleType> roles) {
        return roles.stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                .toList();
    }
}
