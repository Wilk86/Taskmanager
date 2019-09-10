package pl.taskmanager.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.taskmanager.model.Role;

import java.util.Set;

@Service
public class LoginService {
    // metoda zwracająca login zalogowanego użytkownika
    public String getLoginFromCredentials(Authentication auth){
        if(!auth.equals(null)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            return userDetails.getUsername();
        }
        return null;
    }
    // metoda sprawdzająca czy zalogowany użytkownik jest administratorem
    public boolean isAdmin(Authentication auth){
        if(!auth.equals(null)){
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) userDetails.getAuthorities();
            if(authorities.toString().contains("ROLE_ADMIN")){
                return true;
            }
            return false;
        }
        return false;
    }

}
