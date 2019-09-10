package pl.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employee")
@Data                   // gettery i settery
@AllArgsConstructor     // konstruktor z wsztystkimi agrumentami
@NoArgsConstructor      // konstruktor domyślny
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employee_id;
    // dane wprowadzane przez użytkownika
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
    // konstruktor do rejestracji użytkownika
    public User(@NotBlank String name, @NotBlank String lastname, @Email @NotBlank String email, @NotBlank @Size(min = 6) @Pattern(regexp = "[A-Z]{1,}") String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }
    // dane generowane automatycznie
    private LocalDateTime registration_datetime = LocalDateTime.now();
    private Boolean isActivated = false;
    private String confirmation;


    // realacja n:m user to role
    @ManyToMany
    @JoinTable(     // adnotacja złączająca tabele na posdstawie id
            name = "employee_role",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();
    // metoda do dodawania roli
    public void addRole(Role role){
        this.roles.add(role);
    }




}
