package lk.apiit.eirlss.bangerandco.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reported_license")
public class ReportedLicense {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    @Column(name = "report_id")
    private String reportId;
    private String license;
    private String status;

    public ReportedLicense(String reportId, String license, String status) {
        this.reportId = reportId;
        this.license = license;
        this.status = status;
    }
}
