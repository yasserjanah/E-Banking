package me.janah.ebankingbackend;

import me.janah.ebankingbackend.dtos.*;
import me.janah.ebankingbackend.entities.*;
import me.janah.ebankingbackend.enums.AccountStatus;
import me.janah.ebankingbackend.enums.OperationType;
import me.janah.ebankingbackend.exceptions.*;
import me.janah.ebankingbackend.repositories.AccountOperationRepository;
import me.janah.ebankingbackend.repositories.BankAccountRepository;
import me.janah.ebankingbackend.repositories.CustomerRepository;
import me.janah.ebankingbackend.security.services.AppUserService;
import me.janah.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EBankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBankingBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner createROLES(AppUserService appUserService){
        return args -> {
            appUserService.saveNewRole("ROLE_EMPLOYEE", "Employee with admin rights");
            appUserService.saveNewRole("ROLE_CUSTOMER", "Bank Customer");

            // create admin user
            try {
                InEmployeeDTO employee = new InEmployeeDTO();
                employee.setEmployeeId(UUID.randomUUID().toString());
                employee.setPassword("janah"); // TODO: change to real password
                employee.setUsername("employeeYasser");
                employee.setActive(true);
                appUserService.saveNewEmployee(employee);
            } catch (AppRoleNotFoundException | EmployeeNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }

    //@Bean
    CommandLineRunner commandLineRunner2(BankAccountService bankAccountService, AppUserService appUserService){
        return args -> {

            // create roles
            //appUserService.saveNewRole("ROLE_EMPLOYEE", "Employee with admin rights");
            //appUserService.saveNewRole("ROLE_CUSTOMER", "Bank Customer");

            Stream.of("Yasser", "Hamza", "Amine").forEach(name -> {
                InCustomerDTO customer = new InCustomerDTO();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                customer.setPassword("janah");
                customer.setUsername("customer"+name);
                try {
                    bankAccountService.saveCustomer(customer);
                } catch (AppRoleNotFoundException | EmployeeNotFoundException e) {
                    throw new RuntimeException(e);
                }

                try {
                    InEmployeeDTO employee = new InEmployeeDTO();
                    employee.setEmployeeId(UUID.randomUUID().toString());
                    employee.setPassword("janah");
                    employee.setUsername("employee"+name);
                    employee.setActive(true);
                    appUserService.saveNewEmployee(employee);
                } catch (AppRoleNotFoundException | EmployeeNotFoundException e) {
                    throw new RuntimeException(e);
                }

            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, customer.getUserId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, customer.getUserId());
                    List<BankAccountDTO> bankAccountsList = bankAccountService.getAllBankAccounts();
                    for (BankAccountDTO bankAccount : bankAccountsList) {
                        for (int i = 0; i < 10; i++) {
                            String bankAccountID;
                            if (bankAccount instanceof CurrentAccountDTO) {
                                bankAccountID = ((CurrentAccountDTO) bankAccount).getId();
                            }else{
                                bankAccountID = ((SavingAccountDTO) bankAccount).getId();
                            }
                            bankAccountService.credit(bankAccountID, 10000 + Math.random() * 120000, "Credit " + i);
                            bankAccountService.debit(bankAccountID, 1000 + Math.random() * 9000, "Debit " + i);
                        }
                    }
                } catch (CustomerNotFoundException | BankAccountNotFoundException | InsufficientBalanceException e) {
                    e.printStackTrace();
                }
            });
        };
    }

    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                                         BankAccountRepository bankAccountRepository,
                                         AccountOperationRepository accountOperationRepository) {
        return args -> {
            // save a couple of customers
            Stream.of("Yasser", "Yassine", "Hamza", "Amine").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                customerRepository.save(customer);
            });
            // save a couple of bank accounts
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setCustomer(customer);
                currentAccount.setBalance(Math.random() * 1000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setOverDraft(Math.random() * 1000);
                currentAccount.setId(UUID.randomUUID().toString());
                bankAccountRepository.save(currentAccount);
            });
            customerRepository.findAll().forEach(customer -> {
                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setCustomer(customer);
                savingAccount.setBalance(Math.random() * 1000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setInterestRate(Math.random() * 1000);
                savingAccount.setId(UUID.randomUUID().toString());
                bankAccountRepository.save(savingAccount);
            });

            // save a couple of account operations
            bankAccountRepository.findAll().forEach(bankAccount -> {
                AccountOperation accountOperation = new AccountOperation();
                accountOperation.setBankAccount(bankAccount);
                accountOperation.setOperationDate(new Date());
                accountOperation.setAmount(Math.random() * 1000);
                accountOperation.setType(Math.random() > 0.5 ? OperationType.CREDIT : OperationType.DEBIT);
                accountOperationRepository.save(accountOperation);
            });

        };
    }

    //@Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository,
                                        BankAccountRepository bankAccountRepository,
                                        AccountOperationRepository accountOperationRepository){
        return args -> {

            BankAccount bankAccount = bankAccountRepository.
                    findById("0b65ce2e-4c76-4cd7-8b51-890cd1534e11").
                    orElse(null);
            if (bankAccount != null) {
                System.out.println("**********************************************************");
                System.out.println(bankAccount.getId());
                System.out.println(bankAccount.getBalance());
                System.out.println(bankAccount.getStatus());
                System.out.println(bankAccount.getCustomer().getName());
                if (bankAccount instanceof CurrentAccount) {
                    System.out.println(((CurrentAccount) bankAccount).getOverDraft());
                }
                if (bankAccount instanceof SavingAccount) {
                    System.out.println(((SavingAccount) bankAccount).getInterestRate());
                }
                System.out.println(bankAccount.getCreatedAt());
                bankAccount.getAccountOperations().forEach(accountOperation -> {
                    System.out.println("===============================");
                    System.out.println(accountOperation.getId());
                    System.out.println(accountOperation.getAmount());
                    System.out.println(accountOperation.getOperationDate());
                    System.out.println(accountOperation.getType());
                    System.out.println("===============================\n");
                });
                System.out.println("**********************************************************");
            }

        };
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
