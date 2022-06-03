package me.janah.ebankingbackend.dtos;

import lombok.Data;

@Data
public class CreditRequestDTO {
    private String accountId;
    private double amount;
    private String description;
}
