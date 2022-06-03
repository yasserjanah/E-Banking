package me.janah.ebankingbackend.dtos;

import lombok.Data;
import me.janah.ebankingbackend.enums.AccountStatus;

import java.util.Date;

@Data
public class InBankAccountDTO {
    private String accountType;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double overDraft;
    private double interestRate;
}
