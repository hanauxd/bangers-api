package lk.apiit.eirlss.bangerandco.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    private String id;
    
    @NotBlank(message = "Role is required.")
    private String role;

    @NotBlank(message = "Name is required.")
    private String fullName;

    @NotBlank(message = "Date of birth is required.")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dob;

    @NotBlank(message = "Phone number is required.")
    private String phone;

    @NotBlank(message = "Email is required.")
    @Email(message = "A valid email is required")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    private boolean blacklisted;

    @NotBlank(message = "Address is required.")
    private String address;

    @NotBlank(message = "NIC is required.")
    private String nic;

    @NotBlank(message = "License number is required.")
    private String license;
}
