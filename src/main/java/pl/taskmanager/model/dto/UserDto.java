package pl.taskmanager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank                        // NN
    private String name;
    @NotBlank
    private String lastname;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 6)
//    @Pattern(regexp = "[A-Z]{1,}")
    private String password;
    private String password_repeat;

    public UserDto(@NotBlank String name, @NotBlank String lastname, @Email @NotBlank String email, @NotBlank @Size(min = 6) String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }
}
