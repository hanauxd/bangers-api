package lk.apiit.eirlss.bangerandco.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Utility {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @NotBlank(message = "Utility Type is required.")
    @Column(name = "utility_type")
    private String utilityType;

    private int quantity;

    @OneToMany(mappedBy = "utility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BookingUtility> bookingUtilities = new ArrayList<>();

    public void removeBookingUtility(BookingUtility bookingUtility) {
        bookingUtility.setUtility(null);
        this.bookingUtilities.remove(bookingUtility);
    }
}
