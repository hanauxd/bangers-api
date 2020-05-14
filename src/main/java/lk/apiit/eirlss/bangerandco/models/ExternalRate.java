package lk.apiit.eirlss.bangerandco.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "external_rate")
public class ExternalRate {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    private String vehicle;
    private double rate;
    private String ourVehicleId;

    public ExternalRate(String vehicle, double rate, String ourVehicleId) {
        this.vehicle = vehicle;
        this.rate = rate;
        this.ourVehicleId = ourVehicleId;
    }
}
