package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ReviewDTO {

    private UUID id;
    private String text;
    private int evaluation;
    private String username;
    private UUID productID;
    private LocalDate date;
}
