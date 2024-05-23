package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "ordine")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @Column(name= "num_ordine")
    private String orderNumber;

    @Column(name= "stato_ordine")
    private String orderState;

    @Column(name= "data_consegna_prevista")
    private LocalDate expectedDeliveryDate;

    @Column(name= "data_acquisto")
    private LocalDate purchaseDate;

    @Column(name= "data_reso")
    private LocalDate refundDate;

    @Column(name= "reso")
    private Boolean refund;

    @Column(name= "stato_reso")
    private String refundState;

    @Column(name= "id_indirizzo")
    private long addressID;

    @Column(name= "id_metodo_pagamento")
    private long paymentMethodID;
}
