package me.janah.ebankingbackend.repositories;

import me.janah.ebankingbackend.entities.AccountOperation;
import me.janah.ebankingbackend.entities.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findByBankAccountId(String bankAccount_id);
    Page<AccountOperation> findByBankAccountIdOrderByIdDesc(String bankAccount_id, Pageable pageable);
}
