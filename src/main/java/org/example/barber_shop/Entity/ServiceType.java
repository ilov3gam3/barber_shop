package org.example.barber_shop.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "service_types")
@ToString
public class ServiceType extends DistributedEntity{
    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;

    @OneToMany(mappedBy = "serviceType")
    private List<Service> services;
}
