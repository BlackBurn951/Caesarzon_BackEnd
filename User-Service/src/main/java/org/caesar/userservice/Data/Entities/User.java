package org.caesar.userservice.Data.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private String id;

    private String firstName;

    private String lastName;

    private String username;

    private String phoneNumber;

    private String email;

    private String otp;

    private boolean onChanges;

    public User() {
    }

    public User(String id, String firstName, String lastName, String username, String phoneNumber, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
