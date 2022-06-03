package me.janah.ebankingbackend.dtos;

import lombok.Data;

@Data
public class TransferRequestDTO {
    private String fromAccountId;
    private String toAccountId;
    private double amount;
    private String description;
}
