package lk.apiit.eirlss.bangerandco.insurance.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "viewfraud")
public class Fraud {
    @Id
    private String id;
    private String name;
    private String nic;
    private String license;
}
