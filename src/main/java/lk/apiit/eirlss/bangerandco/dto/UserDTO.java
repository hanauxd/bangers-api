package lk.apiit.eirlss.bangerandco.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    private String role;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dob;
    private String phone;
    private String email;
    private String password;
}
