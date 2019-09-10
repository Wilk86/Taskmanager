package pl.taskmanager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProjectDto {
    @NotBlank
    private String acronim;
    @NotBlank
    @Column(columnDefinition = "text")
    private String description;
//    @NotBlank
@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateStart;
//    @NotBlank
@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateStop;

    public ProjectDto() {
        this.dateStart = LocalDate.now();
        this.dateStop = LocalDate.now();
    }
}
