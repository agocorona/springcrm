package crmapi;


import org.springframework.data.jpa.repository.JpaRepository;



public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByName(String name);

    // Customer findBycreatedBy(Long id);
    // Customer findByModifiedBy(Long id);

}
