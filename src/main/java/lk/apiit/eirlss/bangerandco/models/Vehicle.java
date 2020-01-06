package lk.apiit.eirlss.bangerandco.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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

    @NotBlank(message = "Model is required.")
    private String model;

    @NotBlank(message = "Brand is required.")
    private String brand;

    @NotBlank(message = "Category is required.")
    private String category;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}
