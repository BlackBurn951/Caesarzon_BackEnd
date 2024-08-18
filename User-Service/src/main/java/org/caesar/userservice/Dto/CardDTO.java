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
    private double balance;

    public boolean equals(Object o) {
        if(this == o) return true;

        if(o == null) return false;

        if(this.getClass() != o.getClass()) return false;

        CardDTO a = (CardDTO) o;

        return cardNumber.equals(a.getCardNumber()) && owner.equals(a.getOwner()) &&
                cvv.equals(a.getCvv()) && expiryDate.equals(a.getExpiryDate());
    }
}
