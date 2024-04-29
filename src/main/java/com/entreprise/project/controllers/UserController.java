package com.entreprise.project.controllers;

import com.entreprise.project.detailsServices.JwtService;
import com.entreprise.project.detailsServices.UserInfoDetails;
import com.entreprise.project.entities.Utilisateur;
import com.entreprise.project.entities.dto.*;
import com.entreprise.project.enums.Role;
import com.entreprise.project.exceptions.DataNotFoundException;
import com.entreprise.project.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    public ResponseLogin Login (@RequestBody AuthRequest authRequest){
        UserDetails user = userService.loadUserByUsername(authRequest.getUserName());
        if(!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())){
            throw new DataNotFoundException("Invalid Password");
        }else{
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
            if(authenticate.isAuthenticated()){
                return new ResponseLogin(jwtService.generateToken(authRequest.getUserName()));
            }else {
                throw new DataNotFoundException("Invalid username or password");
            }
        }

    }
    @GetMapping(path= "/getAll")
    public List<Utilisateur> getAll(){
        return userService.getAllUsers();
    }

    @GetMapping(path= "/getAllEntreprises")
    public List<Utilisateur> getAllEntreprises(){
        return userService.getAllUsers()
                .stream()
                .filter(utilisateur -> utilisateur.getRole().equals(Role.ENTREPRISE))
                .toList();
    }
    @GetMapping("/getEmployeesByEntrepriseId/{idEntreprise}")
    public ResponseEntity<List<Utilisateur>> getEmployesByEntrepriseId(@PathVariable Long idEntreprise) {
        List<Utilisateur> employes = userService.getEmployesByEntrepriseId(idEntreprise);
        return ResponseEntity.ok(employes);
    }
    @GetMapping(path= "/getAllEmployees")
    public List<Utilisateur> getAllEmployees(){
        return userService.getAllUsers()
                .stream()
                .filter(utilisateur -> utilisateur.getRole().equals(Role.Employe))
                .toList();
    }

    @GetMapping(path= "/getUser/{id}")
    public ResponseEntity<Utilisateur> getUserById(@PathVariable Long id){
        Utilisateur utilisateur= userService.findById(id);
        return (utilisateur== null)
                ? new ResponseEntity<Utilisateur>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<Utilisateur>(utilisateur,HttpStatus.OK);
    }
    @PostMapping(path = "/createEntreprise")
    public Utilisateur addUser(@RequestBody CreateEntrepriseDTO user){
        return userService.createEntreprise(user);
    }
    @PutMapping(path = "/updateUser/{id}")
    public Utilisateur updateUser(@PathVariable Long id,@RequestBody CreateEmployeeDTO user){
        return userService.updateUser(id,user);
    }
    @PostMapping("/createEmployee/{idEntreprise}")
    public ResponseEntity<Utilisateur> createEmploye(@PathVariable Long idEntreprise, @RequestBody CreateEmployeeDTO user) {
        Utilisateur employe = userService.createEmploye(idEntreprise, user);
        return new ResponseEntity<>(employe, HttpStatus.CREATED);
    }
    @DeleteMapping(path = "/deleteUser/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }

    @GetMapping("/api/user")
    public UserDTO getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userName = jwtService.extractUserName(token);
        UserDetails userDetails = userService.loadUserByUsername(userName);
        UserInfoDetails userInfoDetails = (UserInfoDetails) userDetails;
        UserDTO utilisateur = new UserDTO();

        utilisateur.setId(userInfoDetails.getId());
        utilisateur.setUserName(userInfoDetails.getUsername());
        utilisateur.setEmail(userInfoDetails.getEmail());
        utilisateur.setPhoneNumber(userInfoDetails.getPhoneNumber());
        utilisateur.setFirstName(userInfoDetails.getFirstName());
        utilisateur.setLastName(userInfoDetails.getLastName());
        utilisateur.setRole(userInfoDetails.getRole());
        utilisateur.setHiringDate(userInfoDetails.getHiringDate());
        utilisateur.setUsers(userInfoDetails.getUsers());
        utilisateur.setTachesassined(userInfoDetails.getAssigneetaches());
        utilisateur.setEntreprise(userInfoDetails.getEntreprise());
        utilisateur.setTachescreated(userInfoDetails.getCreatedtaches());

        return utilisateur;
    }
}
