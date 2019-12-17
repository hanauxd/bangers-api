package lk.apiit.eirlss.bangerandco.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "userDocument")
public class UserDocument {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "documentPath")
    private String documentPath;

    @Column(name = "documentType")
    private String documentType;
}
