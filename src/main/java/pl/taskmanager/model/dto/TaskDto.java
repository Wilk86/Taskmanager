package pl.taskmanager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    @NotBlank(message = "Title must be not blank")
    private String title;
    @NotBlank(message = "Message must be not blank")
    @Column(columnDefinition = "text")
    private String message;
//    @NotBlank
    @Column(name = "task_interval")
    private Integer interval;
}
