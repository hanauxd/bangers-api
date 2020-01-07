package lk.apiit.eirlss.bangerandco.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    @NotBlank(message = "Role is required.")
    private String role;

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @NotBlank(message = "Date of birth is required.")
    private String dob;

    @NotBlank(message = "Phone number is required.")
    private String phone;

    @NotBlank(message = "Email is required.")
    @Email(message = "A valid email is required")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    private boolean newUser;

    private boolean blacklisted;
}
