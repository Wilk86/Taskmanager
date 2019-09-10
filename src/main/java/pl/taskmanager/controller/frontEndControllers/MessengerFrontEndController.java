package pl.taskmanager.controller.frontEndControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.taskmanager.model.User;
import pl.taskmanager.model.dto.CommentDto;
import pl.taskmanager.model.dto.ContactDto;
import pl.taskmanager.repository.UserRepository;
import pl.taskmanager.service.AutoMailingService;

import java.util.List;
import javax.validation.Valid;

@Controller
public class MessengerFrontEndController {

    private UserRepository userRepository;
    private AutoMailingService autoMailingService;
    @Autowired
    public MessengerFrontEndController(UserRepository userRepository, AutoMailingService autoMailingService) {
        this.userRepository = userRepository;
        this.autoMailingService = autoMailingService;
    }

    @GetMapping("/sendMessage")
    public String sendMessage(
            Model model, Authentication auth
    ){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        // lista wszystkich użytkowników
        List<User> users = userRepository.findAll();
        User me = userRepository.findFirstByEmail(userDetails.getUsername());
        users.remove(me);
        // lista wszystkich użytkowników
        model.addAttribute("users", users);
        // przekazanie do widoku modelu contactDto
        model.addAttribute("contactDto", new ContactDto());
        return "contact";
    }

    @PostMapping("/sendMessage")
    public String sendMessage(
            @ModelAttribute @Valid ContactDto contactDto,
            BindingResult bindingResult,
            Authentication auth,
            Model model
    ){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        if(bindingResult.hasErrors()){
            // lista wszystkich użytkowników
            List<User> users = userRepository.findAll();
            User me = userRepository.findFirstByEmail(userDetails.getUsername());
            users.remove(me);
            model.addAttribute("users", users);
            return "contact";
        }
        // auto mailing
        autoMailingService.sendSimpleMessage(
                contactDto.getEmailTo(),
                "Message from " + userDetails.getUsername(),
                contactDto.getContent() );
        return "redirect:/sendMessage";
    }



}
