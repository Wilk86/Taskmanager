package pl.taskmanager.controller.frontEndControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.taskmanager.model.Comment;
import pl.taskmanager.model.Project;
import pl.taskmanager.model.Task;
import pl.taskmanager.model.User;
import pl.taskmanager.model.dto.CommentDto;
import pl.taskmanager.model.dto.ProjectDto;
import pl.taskmanager.model.dto.TaskDto;
import pl.taskmanager.model.enums.TaskStatus;
import pl.taskmanager.service.AutoMailingService;
import pl.taskmanager.service.LoginService;
import pl.taskmanager.service.ProjectService;
import pl.taskmanager.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProjectControllerFrontEnd {

    private ProjectService projectService;
    private LoginService loginService;
    private UserService userService;
    private AutoMailingService autoMailingService;

    @Autowired
    public ProjectControllerFrontEnd(ProjectService projectService, LoginService loginService, UserService userService, AutoMailingService autoMailingService) {
        this.projectService = projectService;
        this.loginService = loginService;
        this.userService = userService;
        this.autoMailingService = autoMailingService;
    }

    @GetMapping("/projects")
    public String projects(Model model, Authentication auth){
        model.addAttribute("no_comments", projectService.countAllComments());
        model.addAttribute("percent",projectService.percentOfClosedTasks());
        List<Project> projectsList = projectService.getAllProjects();
        Long taskNo = projectService.countTasks();
        model.addAttribute("projectsList",projectsList);
        model.addAttribute("taskNo",taskNo);
        model.addAttribute("projectDto", new ProjectDto());
        model.addAttribute("logged_email", loginService.getLoginFromCredentials(auth));
        model.addAttribute("isAdmin", loginService.isAdmin(auth));
        return "projects";
    }
    @PostMapping("/addProject")
    public String addProject(
               @ModelAttribute @Valid ProjectDto projectDto,
               BindingResult bindingResult,
               Model model
    ){
        List<Project> projectsList = projectService.getAllProjects();
        Long taskNo = projectService.countTasks();
        model.addAttribute("projectsList", projectsList);
        model.addAttribute("taskNo", taskNo);
        // jeżeli występują błędy w formularzu
        if(bindingResult.hasErrors()) {
            return "projects";
        }
        // czy dateStart jest > now()
        if(projectDto.getDateStart().isBefore(LocalDate.now())){
            System.out.println("BŁĄD: Data rozpoczęcia przed datą aktualną");
            model.addAttribute("dateStartValid","date start is before now");
            return "projects";
        }
        // czy dateStart < dateStop
        if(projectDto.getDateStart().isAfter(projectDto.getDateStop())){
            System.out.println("BŁĄD: Data rozpoczęcia za datą zakończenia");
            model.addAttribute("dateStopValid","date stop is before date start");
            return "projects";
        }
        // gdy jest wszystko ok -> zapisujemy projekt do DB
            projectService.createProject(projectDto);
            return "redirect:/projects";
        }


    @GetMapping("/projects&delete&{project_id}")
    public String deleteProject(@PathVariable Long project_id){
        projectService.removeProjectRecursively(project_id);
        // przekierowanie na url
        return "redirect:/projects";
    }
    @GetMapping("/project&{project_id}")
    public String showProjectDetails(@PathVariable Long project_id, Model model) {
        model.addAttribute("no_comments",projectService.countCommentsInProject(project_id));
        model.addAttribute("percent",projectService.percentOfClosedTasksInProject(project_id));
        model.addAttribute("tasks",projectService.getTasksInProject(project_id));
        Project project = projectService.getProjectById(project_id);
        model.addAttribute("project",project);
        model.addAttribute("taskDto",new TaskDto());
//        System.out.println("List of tasks:" + project.getTasks());
        return "project";
    }
    @PostMapping("/addTask&{project_id}")
    public String addTaskToProject(
            @ModelAttribute @Valid TaskDto taskDto,
            BindingResult bindingResult,
            @PathVariable Long project_id,
            Model model){
        if(bindingResult.hasErrors()){
            Project project = projectService.getProjectById(project_id);
            model.addAttribute("project",project);
//            model.addAttribute("taskDto",new TaskDto());
            return "project";
        }
        // dodaj taska do DB
        projectService.createTask(taskDto,project_id);
        return "redirect:/project&"+project_id;
    }
    @GetMapping("task&delete&{task_id}")
    public String deleteTask(@PathVariable Long task_id){
        Task task = projectService.removeTask(task_id);
        return "redirect:/project&"+ task.getProject().getProject_id();
    }
    // metoda odwołująca się do widoku wybranego zadania
    @GetMapping("/task&{task_id}")
    public String selectedTask(
            @PathVariable Long task_id,
            Model model,
            Authentication auth){
        model.addAttribute("no_comments", projectService.countCommentsInTask(task_id));
        // Lista komentarzy wybranego taska
        List<Comment> comments = projectService.getAllCommentsByTaskId(task_id);
        // sortowanie komentarzy po dacie dodania od najnowszych do najstarszych
        Collections.sort(comments, Comparator.comparing(Comment::getDate_added).reversed());
        model.addAttribute("comments", comments);
        // Obiekt dto do formularza dodawania komentarzy
        model.addAttribute("commentDto", new CommentDto());
        // wydobycie z bazy danych szukanego taska
        Task task = projectService.getTaskById(task_id);
        model.addAttribute("task", task);
        // przekazanie do wydoku listy użytkowników do przypisanie do tasków
        List<User> allUsers = userService.getAllUsers();
        allUsers.removeAll(task.getUsers());
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("addedUser", new User());
        // przekazanie tablicy z statusami
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("isAdmin", loginService.isAdmin(auth));
        return "task";
    }

    @PostMapping("/addUserToTask&{task_id}")
    public String addUserToTask(
            @PathVariable Long task_id,
            @ModelAttribute Task task,
            @ModelAttribute User user
    ){
        System.out.println("Added user: " + user);
        // dodanie wybranego usera do listy tasków
        projectService.addUserToTask(userService.getUserByEmail(user.getEmail()),task_id);
        autoMailingService.sendSimpleMessage(
                user.getEmail(),
                "Dodano do zadania",
                "Zostałeś dodany do zadania: "+ projectService.getTaskById(task_id)
                );
        return "redirect:/task&"+task_id;
    }
    @GetMapping("/deleteUserFromTask&{task_id}&{user_id}")
    public String deleteUserFromTask(
            @PathVariable Long task_id, @PathVariable Long user_id){
        Task task = projectService.getTaskById(task_id);
        User user = userService.getUserById(user_id);
        // usunięcie usera z listy userów w obiekcie task
        projectService.deleteUserFromTaskUsersList(user,task);
        autoMailingService.sendSimpleMessage(
                user.getEmail(),
                "Usunięto z zadania",
                "Zostałeś usunięty z zadania: "+ task.getTitle() + " " +
                        "w projekcie " + task.getProject().getAcronim()
        );
        return "redirect:/task&"+task_id;
    }
    @PostMapping("/updateTask&{task_id}")
    public String updateTaskStatusAndInterval(
            @PathVariable Long task_id,
            @ModelAttribute Task task
    ){
        projectService.updateTaskStatusAndInterval(
                task_id,
                task.getInterval(),
                task.getTaskStatus());
        return "redirect:/task&"+task_id;
    }
    @PostMapping("/addCommentToTask&{task_id}")
    public String createComment(
            @PathVariable Long task_id,
            @ModelAttribute @Valid CommentDto commentDto, BindingResult bindingResult,
            Authentication auth,
            Model model
            ){
        if(bindingResult.hasErrors()){
            // Lista komentarzy wybranego taska
            List<Comment> comments = projectService.getAllCommentsByTaskId(task_id);
            // sortowanie komentarzy po dacie dodania od najnowszych do najstarszych
            Collections.sort(comments, Comparator.comparing(Comment::getDate_added).reversed());
            model.addAttribute("comments", comments);
            // Obiekt dto do formularza dodawania komentarzy
            // model.addAttribute("commentDto", new CommentDto());
            // wydobycie z bazy danych szukanego taska
            Task task = projectService.getTaskById(task_id);
            model.addAttribute("task", task);
            // przekazanie do wydoku listy użytkowników do przypisanie do tasków
            List<User> allUsers = userService.getAllUsers();
            allUsers.removeAll(task.getUsers());
            model.addAttribute("allUsers", allUsers);
            model.addAttribute("addedUser", new User());
            // przekazanie tablicy z statusami
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("isAdmin", loginService.isAdmin(auth));
            return "task";
        }
        // zapisujemy komantarz do DB
        User user = userService.getUserByEmail(loginService.getLoginFromCredentials(auth));
        projectService.createComment(
                commentDto,
                projectService.getTaskById(task_id),
                user.getName() + " " + user.getLastname());
        return "redirect:/task&"+task_id;
    }

}
