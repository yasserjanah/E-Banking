package me.janah.ebankingbackend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDTO {
    private String userId;
    private String username;
    //private String password;
    private boolean active;
    private String name;
    private String email;
}
