package me.janah.ebankingbackend.mappers;

import me.janah.ebankingbackend.dtos.*;
import me.janah.ebankingbackend.entities.*;

public interface BankAccountMapper {
    CustomerDTO fromCustomer(Customer customer);

    Customer fromCustomerDTO(CustomerDTO customerDTO);

    Customer fromInCustomerDTO(InCustomerDTO customerDTO);

    EmployeeDTO fromEmployee(Employee employee);

    SavingAccountDTO fromSavingAccount(SavingAccount savingAccount);

    SavingAccount fromSavingAccountDTO(SavingAccountDTO savingAccountDTO);

    CurrentAccountDTO fromCurrentAccount(CurrentAccount currentAccount);

    CurrentAccount fromCurrentAccountDTO(CurrentAccountDTO currentAccountDTO);

    AccountOperationDTO fromAccountOperation(AccountOperation accountOperation);

    AccountOperation fromAccountOperationDTO(AccountOperationDTO accountOperationDTO);
}
