package me.janah.ebankingbackend.services;

import me.janah.ebankingbackend.dtos.*;
import me.janah.ebankingbackend.exceptions.*;

import java.util.List;

public interface BankAccountService {
    //CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CustomerDTO saveCustomer(InCustomerDTO customerDTO) throws AppRoleNotFoundException, EmployeeNotFoundException;

    BankAccountDTO saveBankAccount(InBankAccountDTO bankAccountDTO) throws CustomerNotFoundException;

    CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, String customerId) throws CustomerNotFoundException;

    SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, String customerId) throws CustomerNotFoundException;

    List<BankAccountDTO> getAllBankAccounts();
    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String id) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, InsufficientBalanceException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String fromAccountId, String toAccountId, double amount, String description) throws BankAccountNotFoundException, InsufficientBalanceException;

    CustomerDTO getCustomer(String id) throws CustomerNotFoundException;

    CustomerDTO getCustomerByUsername(String username) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(InCustomerDTO inCustomerDTO) throws CustomerNotFoundException;

    void deleteCustomer(String id) throws CustomerNotFoundException;

    List<AccountOperationDTO> accountOperationsHistory(String accountId) throws BankAccountNotFoundException;

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<BankAccountDTO> getCustomerAccounts(String id) throws CustomerNotFoundException;

    List<CustomerDTO> searchCustomer(String name);
}
