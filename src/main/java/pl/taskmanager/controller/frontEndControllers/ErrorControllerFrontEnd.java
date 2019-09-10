package pl.taskmanager.controller.frontEndControllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorControllerFrontEnd implements ErrorController {
    @Override
    public String getErrorPath() {
        return "/error";
    }
    @GetMapping("/error")
    public String errorPage(){
        return "404";
    }
    @GetMapping("loginError")
    public String loginError(){
        return "loginError";
    }
}
