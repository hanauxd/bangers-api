package lk.apiit.eirlss.bangerandco.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "booking_utility")
public class BookingUtility {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "booking", referencedColumnName = "id")
    @JsonIgnore
    private Booking booking;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "utility", referencedColumnName = "id")
    private Utility utility;

    public BookingUtility(Booking booking, Utility utility) {
        this.booking = booking;
        this.utility = utility;
    }
}
