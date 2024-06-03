package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CardDTO {

    private Long id;

    private String cardNumber;

    private String owner;

    private String cvv;

    private LocalDate expiryDate;
}
