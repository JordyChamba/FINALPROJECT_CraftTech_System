/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (BACKEND)
 */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Model.User;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Invalid credentials. Please provide a username and password.");
        }

        User user = userService.findByUsername(loginRequest.getUsername());
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok("Login successful. Welcome: [ " + loginRequest.getUsername() + " ]");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password.");
        }
    }
}
