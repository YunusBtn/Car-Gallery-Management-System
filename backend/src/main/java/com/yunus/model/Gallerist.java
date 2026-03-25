package com.yunus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gallerist")
public class Gallerist extends BaseEntity{

    private String firstName;

    private String lastName;

    @OneToOne
    private Address address;

    @OneToOne
    private User user;

}
