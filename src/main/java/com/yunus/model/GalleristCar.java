package com.yunus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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
@Table(name = "gallerist_car")
public class GalleristCar extends BaseEntity {

    @ManyToOne
    private Gallerist gallerist;

    @OneToOne
    private Car car;
}
