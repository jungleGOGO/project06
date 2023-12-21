package com.team36.domain;

import com.team36.constant.MemberRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "roleSet")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mid;

    private String mpw;

    private String mname;

    private String nickname;

    @Column(unique = true)
    private String email;

    private Integer active;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();

    @OneToOne(mappedBy = "member")
    private Profile profile;

    public void changePassword(String mpw ){
        this.mpw = mpw;
    }
    public void changeMname(String mname) { this.mname=mname; }
    public void addRole(MemberRole memberRole){
        this.roleSet.add(memberRole);
    }
    public void changeActive(Integer active, Integer mid) { this.active=active; this.mid=mid; }

}