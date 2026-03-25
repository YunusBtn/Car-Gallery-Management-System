package com.yunus.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {


    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName name;


    public enum RoleName {
        ADMIN, MANAGER, USER, CUSTOMER, GALLERIST
    }


}
