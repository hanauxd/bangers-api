package lk.apiit.eirlss.bangerandco.models;

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
public class Vehicle {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @NotBlank(message = "License number is required.")
    private String license;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "brand is required.")
    private String brand;

    @NotBlank(message = "Model is required.")
    private String model;

    @NotBlank(message = "fuelType is required.")
    private String fuelType;

    @NotBlank(message = "transmissionType is required.")
    private String transmissionType;

    @NotBlank(message = "category is required.")
    private String category;

    @NotBlank(message = "size is required.")
    private String size;

    private double price;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    public void removeBooking(Booking booking) {
        booking.setVehicle(null);
        this.bookings.remove(booking);
    }
}
