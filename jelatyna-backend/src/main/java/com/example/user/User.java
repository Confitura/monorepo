package com.example.user;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String username;
    private boolean isAdmin;
}
