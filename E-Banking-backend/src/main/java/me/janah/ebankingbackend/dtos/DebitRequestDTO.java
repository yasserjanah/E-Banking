package me.janah.ebankingbackend.dtos;

import lombok.Data;

@Data
public class DebitRequestDTO {
    private String accountId;
    private double amount;
    private String description;
}
