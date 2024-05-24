package org.caesar.userservice.Data.Entities;


import jakarta.persistence.*;

@Entity
@Table(name ="utente")
public class User {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "cognome")
    private String surname;

    @Column(name = "username")
    private String username;

    @Column(name = "num_telefono")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

//    @Column(name = "foto_profilo")
//    private Byte profilePic;






}
