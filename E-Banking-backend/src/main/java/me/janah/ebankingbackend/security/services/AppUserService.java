package me.janah.ebankingbackend.security.services;

import me.janah.ebankingbackend.dtos.EmployeeDTO;
import me.janah.ebankingbackend.dtos.InEmployeeDTO;
import me.janah.ebankingbackend.exceptions.AppRoleAlreadyExistsException;
import me.janah.ebankingbackend.exceptions.AppRoleNotFoundException;
import me.janah.ebankingbackend.exceptions.EmployeeNotFoundException;
import me.janah.ebankingbackend.exceptions.PasswordsDoNotMatchException;
import me.janah.ebankingbackend.security.entities.AppRole;
import me.janah.ebankingbackend.security.entities.AppUser;

public interface AppUserService {

    EmployeeDTO saveNewEmployee(InEmployeeDTO inEmployeeDTO) throws AppRoleNotFoundException, EmployeeNotFoundException;

    AppRole saveNewRole(String roleName, String description) throws AppRoleAlreadyExistsException;
    void addRoleToUser(String username, String roleName) throws EmployeeNotFoundException, AppRoleNotFoundException;
    void removeRoleFromUser(String username, String roleName) throws EmployeeNotFoundException, AppRoleNotFoundException;
    AppUser loadUserByUsername(String username);
}
