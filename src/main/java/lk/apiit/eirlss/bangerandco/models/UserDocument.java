package lk.apiit.eirlss.bangerandco.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user_document")
public class UserDocument {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;
    private String filename;
    private String type;
    @Column(name = "issue_date")
    private Date issueDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false, referencedColumnName = "id")
    @JsonIgnore
    private User user;

    public UserDocument(String filename, String type, Date issueDate, User user) {
        this.filename = filename;
        this.type = type;
        this.issueDate = issueDate;
        this.user = user;
    }
}
