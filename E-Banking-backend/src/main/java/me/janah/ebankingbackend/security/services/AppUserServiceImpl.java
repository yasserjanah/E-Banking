package me.janah.ebankingbackend.security.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.janah.ebankingbackend.dtos.EmployeeDTO;
import me.janah.ebankingbackend.dtos.InEmployeeDTO;
import me.janah.ebankingbackend.entities.Employee;
import me.janah.ebankingbackend.exceptions.AppRoleAlreadyExistsException;
import me.janah.ebankingbackend.exceptions.AppRoleNotFoundException;
import me.janah.ebankingbackend.exceptions.EmployeeNotFoundException;
import me.janah.ebankingbackend.mappers.BankAccountMapper;
import me.janah.ebankingbackend.security.entities.AppRole;
import me.janah.ebankingbackend.security.entities.AppUser;
import me.janah.ebankingbackend.security.repositories.AppRoleRepository;
import me.janah.ebankingbackend.security.repositories.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;
    private BankAccountMapper dtoMapper;

    @Override
    public EmployeeDTO saveNewEmployee(InEmployeeDTO inEmployeeDTO) throws AppRoleNotFoundException, EmployeeNotFoundException {
        Employee employee = new Employee();
        employee.setUserId(UUID.randomUUID().toString());
        employee.setUsername(inEmployeeDTO.getUsername());
        employee.setPassword(passwordEncoder.encode(inEmployeeDTO.getPassword()));
        employee.setActive(true);
        employee.setEmployeeId(inEmployeeDTO.getEmployeeId());
        Employee savedUser = appUserRepository.save(employee);
        log.info("Saved user: " + savedUser);
        this.addRoleToUser(savedUser.getUsername(), "ROLE_EMPLOYEE");
        return dtoMapper.fromEmployee(savedUser);
    }

    @Override
    public AppRole saveNewRole(String roleName, String description) throws AppRoleAlreadyExistsException {
        if (appRoleRepository.findByRoleName(roleName) != null) throw new AppRoleAlreadyExistsException("Role '"+roleName+"' already exists");
        AppRole appRole = new AppRole();
        appRole.setRoleName(roleName);
        appRole.setDescription(description);
        return appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String username, String roleName) throws EmployeeNotFoundException, AppRoleNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findByRoleName(roleName);
        if (appUser == null) throw new EmployeeNotFoundException("User '"+username+"' does not exist");
        if (appRole == null) throw new AppRoleNotFoundException("Role '"+roleName+"' does not exist");
        appUser.getAppRoles().add(appRole);
    }

    @Override
    public void removeRoleFromUser(String username, String roleName) throws EmployeeNotFoundException, AppRoleNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findByRoleName(roleName);
        if (appUser == null) throw new EmployeeNotFoundException("User '"+username+"' does not exist");
        if (appRole == null) throw new AppRoleNotFoundException("Role '"+roleName+"' does not exist");
        appUser.getAppRoles().remove(appRole);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }
}
