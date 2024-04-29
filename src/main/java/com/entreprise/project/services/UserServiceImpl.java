package com.entreprise.project.services;

import com.entreprise.project.detailsServices.UserInfoDetails;
import com.entreprise.project.entities.Tache;
import com.entreprise.project.entities.Utilisateur;
import com.entreprise.project.entities.dto.CreateEmployeeDTO;
import com.entreprise.project.entities.dto.CreateEntrepriseDTO;
import com.entreprise.project.enums.Role;
import com.entreprise.project.exceptions.DataNotFoundException;
import com.entreprise.project.exceptions.UserAlreadyExistException;
import com.entreprise.project.repositories.TacheRepository;
import com.entreprise.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService , UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TacheRepository tacheRepository;
    @Override
    public List<Utilisateur> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Utilisateur createEntreprise(CreateEntrepriseDTO user) {
        Utilisateur entreprise = userRepository.findByUserName(user.getUserName()).orElse(null);
        System.out.println(entreprise);
        if(entreprise != null){
            throw new UserAlreadyExistException("Le Username "+user.getUserName()+" déjà existe");
        }
        Utilisateur user1 = new Utilisateur();
        user1.setUserName(user.getUserName());
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        user1.setEmail(user.getEmail());
        user1.setPhoneNumber(user.getPhoneNumber());
        user1.setHiringDate(LocalDate.now());
        user1.setRole(Role.ENTREPRISE);
        return userRepository.save(user1);
    }

    @Override
    public Utilisateur createEmploye(Long idEntreprise, CreateEmployeeDTO user) {
        Utilisateur entreprise = userRepository.findById(idEntreprise)
                .orElseThrow(() -> new DataNotFoundException("Entreprise not found with id: " + idEntreprise));
        Utilisateur employee = userRepository.findByUserName(user.getUserName()).orElse(null);
        if(employee != null){
            throw new UserAlreadyExistException("Le Username "+user.getUserName()+" déjà existe");
        }

        Utilisateur user1 = new Utilisateur();
        user1.setUserName(user.getUserName());
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        user1.setEmail(user.getEmail());
        user1.setPhoneNumber(user.getPhoneNumber());
        user1.setHiringDate(LocalDate.now());
        user1.setEntreprise(entreprise);
        user1.setRole(Role.Employe);
        return userRepository.save(user1);
    }
    @Override
    public List<Utilisateur> getEmployesByEntrepriseId(Long idEntreprise) {
        return userRepository.findByEntreprise_Id(idEntreprise);
    }
    @Override
    public Utilisateur updateUser(Long userId, CreateEmployeeDTO employeeDTO) {
        Optional<Utilisateur> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {
            Utilisateur existingUtilisateur = existingUser.get();

            existingUtilisateur.setUserName(employeeDTO.getUserName());
            existingUtilisateur.setFirstName(employeeDTO.getFirstName());
            existingUtilisateur.setLastName(employeeDTO.getLastName());
            existingUtilisateur.setPassword(employeeDTO.getPassword());
            existingUtilisateur.setEmail(employeeDTO.getEmail());
            existingUtilisateur.setPhoneNumber(employeeDTO.getPhoneNumber());

            return userRepository.save(existingUtilisateur);
        } else {

            throw new DataNotFoundException("User not found with id: " + userId);
        }
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Utilisateur findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Utilisateur> userInfo = userRepository.findByUserName(username);
        if(userInfo.isPresent()){
            if(userInfo.get().getRole().equals(Role.ADMIN)){
                List<Utilisateur> onlyEntreprises = this.getAllUsers().stream()
                        .filter(utilisateur -> utilisateur.getRole().equals(Role.ENTREPRISE))
                        .toList();
                return new UserInfoDetails(userInfo.get(),onlyEntreprises,null,null);
            }else if(userInfo.get().getRole().equals(Role.ENTREPRISE)){
                List<Utilisateur> onlyEmployees = this.getAllUsers().stream()
                        .filter(utilisateur -> utilisateur.getRole().equals(Role.Employe))
                        .filter(utilisateur -> utilisateur.getEntreprise().getId().equals(userInfo.get().getId()))
                        .toList();
                List<Tache> createdTaches = tacheRepository.findByCreateur_Id(userInfo.get().getId());
                return new UserInfoDetails(userInfo.get(),onlyEmployees,createdTaches,null);
            }else{
                List<Tache> tachesByEmployee = tacheRepository.findByAssigne_Id(userInfo.get().getId());
                return new UserInfoDetails(userInfo.get(),null,null,tachesByEmployee);
            }
        }else {
            throw new UsernameNotFoundException("cet utilisateur n'existe pas");
        }
    }
}
