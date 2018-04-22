package crmapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotNull;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
//import javax.persistence.OneToMany;
// import java.util.HashSet;
// import java.util.Set;

@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String username;



    private String password;

    private Boolean isAdmin;

    private Account() { } // JPA only

    public Account(final String username, final String password, final Boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin= isAdmin;

    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    void setUsername(String user){
         this.username= user;
    }

    void setPassword(String passwd){
    this.password= passwd;
    }

    void setAdminState(Boolean isadmin){
         this.isAdmin= isadmin;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getIsAdmin(){
        return isAdmin;
    }



    
}
