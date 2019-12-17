package lk.apiit.eirlss.bangerandco.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @Column
    private String id;

    @Column(name = "status")
    private String status;

    @Column(name = "pickupDate")
    private Date pickupDate;

    @Column(name = "dropDate")
    private Date dropDate;

    @Column(name = "active")
    private boolean active;

    @Column(name = "lateDrop")
    private boolean lateDrop;

    @Column(name = "extended")
    private boolean extended;

    @Column(name = "extendedDate")
    private Date extendedDate;
}
