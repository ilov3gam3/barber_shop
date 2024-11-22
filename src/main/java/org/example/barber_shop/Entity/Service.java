package org.example.barber_shop.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "services")
@ToString
public class Service extends DistributedEntity{
    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;

    private long price;

    private int estimateTime; // in minutes

    @ManyToOne
    @JoinColumn(name = "service_type_id")
    private ServiceType serviceType;

    @ManyToMany
    private List<Combo> combos;

    @OneToMany
    private List<File> images;

    private boolean deleted;
}
