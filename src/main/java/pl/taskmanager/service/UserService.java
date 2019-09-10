package pl.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.taskmanager.model.Role;
import pl.taskmanager.model.User;
import pl.taskmanager.model.dto.UserDto;
import pl.taskmanager.repository.RoleRepository;
import pl.taskmanager.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AutoMailingService autoMailingService;
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, AutoMailingService autoMailingService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.autoMailingService = autoMailingService;
    }

    // wypisz wszystkich użytkowników
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    // rejestracja użytkownika
    public User addUser(UserDto user) throws NoSuchAlgorithmException {
        // utwórz obiekt User
        User registered_user = new User(
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                passwordEncoder.encode(user.getPassword())); // zwraca hash hasła
        registered_user.addRole(roleRepository.getOne(1L));
        // /registrationConfirm/email=x
        // /registrationConfirm/dsadef##!@#!@$!
        registered_user.setConfirmation(shaEncoder("email="+user.getEmail()));
        // link z potwierdzeniem
        autoMailingService.sendSimpleMessage(
                user.getEmail(),
                "TASK MANAGER: confirm your registration",
                "https://taskmanagerpub.herokuapp.com/registrationConfirmed/"+shaEncoder("email="+user.getEmail())
        );
        return userRepository.save(registered_user);
    }
    public String shaEncoder(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        String hash = "";
        for (byte b : encodedhash) {
            hash += b;
        }
        return hash.replaceAll("-","");
    }

    public void confirmedRegistration(String registration_hash){
        User user = userRepository.findFirstByConfirmation(registration_hash);
        if(user != null){
            System.out.println("POTWIERDZONE");
            user.setIsActivated(true);
            userRepository.save(user);
        }else{
            System.out.println("NIC");
        }

    }

    // logowanie użytkownika
    public String loginUser(String email, String password){
        User user = userRepository.findFirstByEmailAndPassword(email,password);
        if(user == null){
            return "błąd logowania";
        }
        return "zarejestrowano: " + user.toString();
    }
    // metoda zwracająca obiekt roli po id roli
    public Role getRoleById(Long id){
        return roleRepository.getOne(id);
    }
    // metoda pobierająca Usera po adresie email
    public User getUserByEmail(String email){
        return userRepository.findFirstByEmail(email);
    }
    public User getUserById(Long user_id){
        return userRepository.getOne(user_id);
    }

}
