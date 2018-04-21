package crmapi;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByName(String name);
    Optional<Customer> findById(Long id);

    // Customer findBycreatedBy(Long id);
    // Customer findByModifiedBy(Long id);

}
