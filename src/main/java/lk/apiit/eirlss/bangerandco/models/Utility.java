package lk.apiit.eirlss.bangerandco.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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
    private String utilityType;

    @NotBlank(message = "Quantity is required.")
    private int quantity;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "booking", nullable = false, referencedColumnName = "id")
    @JsonIgnore
    private Booking booking;
}
