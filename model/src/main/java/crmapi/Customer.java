package crmapi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    private String photo;

    @JsonIgnore 
    @CreatedBy
    private Long createdBy;

    @JsonIgnore 
    @LastModifiedBy
    private Long modifiedBy;

    private Customer() { } // JPA only

    public Customer(final String name, final String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Customer(final String name, final String surname,final String photo) {
        this.name = name;
        this.surname = surname;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Long getCreatedBy() {
        return createdBy;
    }
    public Long getModifiedBy() {
        return modifiedBy;
    }
}

