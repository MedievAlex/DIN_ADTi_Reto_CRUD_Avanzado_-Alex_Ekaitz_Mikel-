package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author ema
 */

@Entity
@Table(name = "admin_")
public class Admin extends Profile implements Serializable
{
    @Column(name = "current_account", length = 40)
    private String currentAccount;

    public Admin(String currentAccount, String username, String password, String email, String name, String telephone, String surname)
    {
        super(username, password, email, name, telephone, surname);
        this.currentAccount = currentAccount;
    }

    public Admin()
    {
        super();
        this.currentAccount = "";
    }

    public String getCurrentAccount() { return currentAccount; }
    public void setCurrentAccount(String currentAccount) { this.currentAccount = currentAccount; }

    @Override
    public String show()
    {
        return "Admin{" + "currentAccount=" + currentAccount + "}";
    }
    
    @Override
    public String toString()
    {
        return show();
    }
}
