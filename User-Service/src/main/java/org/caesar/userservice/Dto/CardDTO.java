package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CardDTO {

    private UUID id;
    private String cardNumber;
    private String owner;
    private String cvv;
    private String expiryDate;
}
