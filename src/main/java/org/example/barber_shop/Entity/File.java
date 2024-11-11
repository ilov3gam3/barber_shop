package org.example.barber_shop.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "files")
@ToString
public class File extends DistributedEntity{
    private String name;
    private String url;
    private String thumbUrl;
    private String mediumUrl;
    private String deleteUrl;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public File(String url) {
        this.url = url;
    }
}
