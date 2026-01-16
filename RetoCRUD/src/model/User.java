package model;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Check;

/**
 *
 * @author ema
 */

@Entity
@Table(name = "user_")
@Check(constraints = "CARD_NUMBER REGEXP '^[A-Z]{2}[0-9]{22}$'")
public class User extends Profile implements Serializable
{
    @Column(name = "gender", length = 40)
    private String gender;
    @Column(name = "card_number", length = 24)
    private String cardNumber;

    public User(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname)
    {
        super(username, password, email, name, telephone, surname);
        this.gender = gender;
        this.cardNumber = cardNumber;
    }

    public User()
    {
        super();
        this.gender = "";
        this.cardNumber = "";
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    @Override
    public String show()
    {
        return "User{" + "gender=" + gender + ", cardNumber=" + cardNumber + "}";
    }

    @Override
    public String toString()
    {
        return show();
    }
}
