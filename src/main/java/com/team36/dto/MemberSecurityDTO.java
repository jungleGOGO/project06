package com.team36.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User {

    private int mid;
    private String mpw;
    private String mname;
    private String email;
    private int active;
    private Map<String, Object> props; //소셜 로그인 정보
    public MemberSecurityDTO(int mid, String password, String name, String username, int active,
                             Collection<? extends GrantedAuthority> authorities) {
        super(String.valueOf(mid), password, authorities);
        this.mid = mid;
        this.mpw = password;
        this.mname = name;
        this.email = username;
        this.active = active;
    }

    public Map<String, Object> getAttributes() {
        return this.getProps();
    }
    public String getName() {
        return this.mname;
    }

}