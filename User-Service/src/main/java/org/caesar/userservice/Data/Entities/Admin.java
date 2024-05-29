package org.caesar.userservice.Data.Entities;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Admin {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String username;

    public Admin() {}

    public Admin(String id, String firstName, String lastName, String email, String password, String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }
}
