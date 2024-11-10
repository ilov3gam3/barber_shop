package org.example.barber_shop.DTO.File;

import lombok.Getter;
import lombok.Setter;
import org.example.barber_shop.DTO.User.UserResponseNoFile;

import java.sql.Timestamp;

@Getter
@Setter
public class FileResponse {
    public int id;
    public String name;
    public String url;
    public String thumbUrl;
    public String mediumUrl;
    public UserResponseNoFile owner;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
