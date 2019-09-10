package pl.taskmanager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.taskmanager.model.User;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDto {
    @NotBlank
    private String content;
//    @NotBlank
    private String emailTo;
}
