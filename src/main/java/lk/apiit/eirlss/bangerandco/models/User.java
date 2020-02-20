package lk.apiit.eirlss.bangerandco.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class User {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @NotBlank(message = "Role is required.")
    private String role;

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dob;

    @NotBlank(message = "Phone number is required.")
    private String phone;

    @Email(message = "A valid email is required")
    @NotBlank(message = "Email is required.")
    @Column(unique = true, updatable = false)
    private String email;

    @NotBlank(message = "Password is required.")
    @JsonIgnore
    private String password;

    private boolean blacklisted;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserDocument> documents = new ArrayList<>();

    public void removeBooking(Booking booking) {
        booking.setUser(null);
        this.bookings.remove(booking);
    }

    public void removeUserDocument(UserDocument document) {
        document.setUser(null);
        this.documents.remove(document);
    }

    public int getAge() {
        Calendar dob = Calendar.getInstance();
        dob.setTime(this.dob);
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        int years = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (dob.get(Calendar.DAY_OF_YEAR) > now.get(Calendar.DAY_OF_YEAR)) {
            years--;
        }
        return years;
    }
}
