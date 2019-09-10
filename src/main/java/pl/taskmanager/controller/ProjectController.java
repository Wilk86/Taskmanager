package pl.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.taskmanager.model.Project;
import pl.taskmanager.model.Task;
import pl.taskmanager.model.dto.ProjectDto;
import pl.taskmanager.model.dto.TaskDto;
import pl.taskmanager.service.ProjectService;

import java.time.LocalDate;

@RequestMapping("/rest")
@RestController
public class ProjectController {
    private ProjectService projectService;
    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    @PostMapping("/project/{acronim}&{description}&{dateStart}&{dateStop}")
    public Project createNewProject(
            @PathVariable String acronim, @PathVariable String description,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateStart,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateStop
            ){
        return projectService.createProject(new ProjectDto(acronim, description, dateStart,dateStop));
    }
    @PutMapping("/project/update/{project_id}&{dateStop}")
    public Project changeProjectDeadline(
            @PathVariable Long project_id,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateStop
    ){
        return projectService.updateProjectStopDate(project_id,dateStop);
    }
    @PostMapping("/task/create/{title}&{message}&{dateStart}&{interval}&{project_id}")
    public Task addTaskToProject(
            @PathVariable String title,
            @PathVariable String message,
            @PathVariable Integer interval,
            @PathVariable Long project_id){
        return projectService.createTask(new TaskDto(
                title,message,interval),project_id);
    }
    @DeleteMapping("/task/delete/{task_id}")
    public String deleteTaskById(
            @PathVariable Long task_id){
        return "Usunięto: " + projectService.removeTask(task_id);
    }
    @DeleteMapping("/project/delete/{project_id}")
    public String deleteProjectById(
            @PathVariable Long project_id){
        return "Usunięto " + projectService.removeProjectRecursively(project_id);
    }
}
