package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class ReviewDTO{

    private UUID id;
    private int starNumber;
    private String description;
    private String reviewDate;
}