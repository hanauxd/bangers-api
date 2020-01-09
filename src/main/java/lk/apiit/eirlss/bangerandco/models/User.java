package lk.apiit.eirlss.bangerandco.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
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

    @NotBlank(message = "Date of birth is required.")
    private String dob;

    @NotBlank(message = "Phone number is required.")
    private String phone;

    @Email(message = "A valid email is required")
    @NotBlank(message = "Email is required.")
    @Column(unique = true, updatable = false)
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    private boolean blacklisted;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
}
