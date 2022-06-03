package me.janah.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.janah.ebankingbackend.dtos.*;
import me.janah.ebankingbackend.entities.*;
import me.janah.ebankingbackend.enums.AccountStatus;
import me.janah.ebankingbackend.enums.OperationType;
import me.janah.ebankingbackend.exceptions.*;
import me.janah.ebankingbackend.mappers.BankAccountMapper;
import me.janah.ebankingbackend.repositories.AccountOperationRepository;
import me.janah.ebankingbackend.repositories.BankAccountRepository;
import me.janah.ebankingbackend.repositories.CustomerRepository;
import me.janah.ebankingbackend.security.services.AppUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapper dtoMapper;
    private PasswordEncoder passwordEncoder;
    private AppUserService appUserService;

    @Override
    public CustomerDTO saveCustomer(InCustomerDTO customerDTO) throws AppRoleNotFoundException, EmployeeNotFoundException {
        log.info("Saving customer");
        Customer customer = dtoMapper.fromInCustomerDTO(customerDTO);
        if (customer.getPassword() != null) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }
        if (customer.getUserId() == null) {
            customer.setUserId(UUID.randomUUID().toString());
        }
        customer = customerRepository.save(customer);
        // by default add ROLE_CUSTOMER role to the customer
        appUserService.addRoleToUser(customer.getUsername(), "ROLE_CUSTOMER");
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public BankAccountDTO saveBankAccount(InBankAccountDTO bankAccountDTO) throws CustomerNotFoundException {
        log.info("Saving bank account");
        if (Objects.equals(bankAccountDTO.getAccountType(), "CurrentAccount")){
            return this.saveCurrentBankAccount(bankAccountDTO.getBalance(), bankAccountDTO.getOverDraft(), bankAccountDTO.getCustomerDTO().getUserId());
        }

        return this.saveSavingBankAccount(bankAccountDTO.getBalance(), bankAccountDTO.getInterestRate(), bankAccountDTO.getCustomerDTO().getUserId());
    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, String customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            log.error("Customer not found");
            throw new CustomerNotFoundException("Customer not found");
        }
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setStatus(AccountStatus.CREATED);
        return dtoMapper.fromCurrentAccount(bankAccountRepository.save(currentAccount));
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, String customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            log.error("Customer not found");
            throw new CustomerNotFoundException("Customer not found");
        }
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setStatus(AccountStatus.CREATED);
        return dtoMapper.fromSavingAccount(bankAccountRepository.save(savingAccount));
    }

    @Override
    public List<BankAccountDTO> getAllBankAccounts() {
        List<BankAccount> bankAccountsList = bankAccountRepository.findAll();
        return bankAccountsList.stream().map(bankAccount -> {
            if (bankAccount instanceof CurrentAccount) {
                return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
            }
            return dtoMapper.fromSavingAccount((SavingAccount) bankAccount);
        }).collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO getBankAccount(String id) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(id).orElseThrow(
                () -> new BankAccountNotFoundException("Bank account not found")
        );

        if (bankAccount instanceof CurrentAccount) {
            return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
        }
        return dtoMapper.fromSavingAccount((SavingAccount) bankAccount);
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, InsufficientBalanceException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(
                () -> new BankAccountNotFoundException("Bank account not found")
        );
        if (bankAccount.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient funds");
        }
        // create debit operation
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        // update balance
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(
                () -> new BankAccountNotFoundException("Bank account not found")
        );
        // create credit operation
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        // update balance
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String fromAccountId, String toAccountId, double amount, String description) throws BankAccountNotFoundException, InsufficientBalanceException {
        // debit from account
        this.debit(fromAccountId, amount, description);
        // credit to account
        this.credit(toAccountId, amount, description);
    }

    @Override
    public CustomerDTO getCustomer(String id) throws CustomerNotFoundException {
        return customerRepository.findById(id).map(customer -> dtoMapper.fromCustomer(customer))
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }

    @Override
    public CustomerDTO getCustomerByUsername(String username) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(InCustomerDTO inCustomerDTO) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(inCustomerDTO.getUserId()).orElse(null);
        if (customer == null) {
            log.error("Customer not found");
            throw new CustomerNotFoundException("Customer not found");
        }
        log.info("Updating customer");
        customer = dtoMapper.fromInCustomerDTO(inCustomerDTO);
        customer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public void deleteCustomer(String id) throws CustomerNotFoundException {
        customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found")
        );
        customerRepository.deleteById(id);
    }

    @Override
    public List<AccountOperationDTO> accountOperationsHistory(String accountId) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream()
                .map(accountOperation -> dtoMapper.fromAccountOperation(accountOperation))
                .collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(
                () -> new BankAccountNotFoundException("Bank account not found")
        );
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByIdDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        accountHistoryDTO.setAccountOperationDTOS(accountOperations.stream()
                .map(accountOperation -> dtoMapper.fromAccountOperation(accountOperation))
                .collect(Collectors.toList()));
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        accountHistoryDTO.setPageSize(accountOperations.getSize());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        return accountHistoryDTO;
    }

    @Override
    public List<BankAccountDTO> getCustomerAccounts(String id) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found")
        );
        return customer.getBankAccounts().stream()
                .map(bankAccount -> {
                    if (bankAccount instanceof CurrentAccount) {
                        return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
                    }
                    return dtoMapper.fromSavingAccount((SavingAccount) bankAccount);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> searchCustomer(String name) {
        List<Customer> customers = customerRepository.searchCustomers(name);
        if (customers.isEmpty()) {
            //System.out.println("No customers found");
            return Collections.emptyList();
        }
        return customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
    }
}
