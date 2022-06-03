package me.janah.ebankingbackend.repositories;

import me.janah.ebankingbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findByUsername(String username);
    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:name%")
    List<Customer> searchCustomers(@Param("name") String name);
}
