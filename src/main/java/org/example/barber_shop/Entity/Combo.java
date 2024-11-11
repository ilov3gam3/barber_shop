package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "combos")
@ToString
public class Combo extends DistributedEntity{
    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;

    private long price;

    private int estimateTime; // in minutes

    @ManyToMany
    private List<Service> services;

    @ManyToMany
    private List<File> images;
}
