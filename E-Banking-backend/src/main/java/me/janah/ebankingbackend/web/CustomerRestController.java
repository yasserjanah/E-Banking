package me.janah.ebankingbackend.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.janah.ebankingbackend.dtos.BankAccountDTO;
import me.janah.ebankingbackend.dtos.CustomerDTO;
import me.janah.ebankingbackend.dtos.InCustomerDTO;
import me.janah.ebankingbackend.entities.Customer;
import me.janah.ebankingbackend.exceptions.AppRoleNotFoundException;
import me.janah.ebankingbackend.exceptions.CustomerNotFoundException;
import me.janah.ebankingbackend.exceptions.EmployeeNotFoundException;
import me.janah.ebankingbackend.mappers.BankAccountMapper;
import me.janah.ebankingbackend.security.JWTUtility;
import me.janah.ebankingbackend.services.BankAccountService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CustomerRestController {

    private BankAccountService bankAccountService;
    private BankAccountMapper dtoMapper;

    @GetMapping("/customers")
    private List<CustomerDTO> customerList() {
        // print security context
        log.info("Security context: " + SecurityContextHolder.getContext().getAuthentication());
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id", required = true) String id) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(id);
    }

    // save customer
    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody InCustomerDTO customerDTO) throws AppRoleNotFoundException, EmployeeNotFoundException {
        return bankAccountService.saveCustomer(customerDTO);
    }

    // update customer
    @PutMapping("/customers/{id}")
    public CustomerDTO updateCustomer(@PathVariable String id, @RequestBody InCustomerDTO customerDTO) throws CustomerNotFoundException {
        customerDTO.setUserId(id);
        return bankAccountService.updateCustomer(customerDTO);
    }

    // delete customer
    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable(name = "id", required = true) String id) throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(id);
    }

    @GetMapping("/customers/{id}/accounts")
    public List<BankAccountDTO> getCustomerAccounts(@PathVariable(name = "id", required = true) String id) throws CustomerNotFoundException {
        return bankAccountService.getCustomerAccounts(id);
    }

    // search customer
    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomer(@RequestParam(name = "name", defaultValue = "") String name) {
        return bankAccountService.searchCustomer(name);
    }

    /*
    @GetMapping("/customers/current")
    public CustomerDTO getCurrentUser(HttpServletRequest request) throws CustomerNotFoundException {
        String token = request.getHeader(JWTUtility.HEADER_STRING).substring(JWTUtility.TOKEN_PREFIX.length());
        String username = JWTUtility.validateToken(token);
        System.out.println("Username: "+username);
        return bankAccountService.getCustomerByUsername(username);
    }*/

}
