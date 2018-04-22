package crmapi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.persistence.EntityListeners;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    private String photo="";

    // @JsonIgnore 
    @CreatedBy
    private String createdBy;

    // @JsonIgnore 
    @LastModifiedBy
    private String modifiedBy;

    private Customer() { } // JPA only

    public Customer(final String name, final String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Customer(final String name, final String surname,final String photo) {
        this.name = name;
        this.surname = surname;
        this.photo= photo;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name= name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname= surname;
    }

    public String getCreatedBy() {
        return createdBy;
    }
    public String getModifiedBy() {
        return modifiedBy;
    }

    public String getPhoto() {
        return photo;
    }
    void setPhoto(String photo){
        this.photo= photo;
    }
}



