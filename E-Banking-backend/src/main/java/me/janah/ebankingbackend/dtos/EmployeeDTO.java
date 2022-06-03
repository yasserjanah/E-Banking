package me.janah.ebankingbackend.dtos;

import lombok.Data;

@Data
public class EmployeeDTO {
    private String userId;
    private String username;
    //private String password;
    private boolean active;
    private String employeeId;
}
