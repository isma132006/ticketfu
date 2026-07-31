package com.ismael.ticketfu.service;




import com.ismael.ticketfu.dto.request.RegisterRequestDto;
import com.ismael.ticketfu.dto.response.UserResponse;
import com.ismael.ticketfu.entity.Role;
import com.ismael.ticketfu.entity.Users;
import com.ismael.ticketfu.exception.ResourceNotFoundException;
import com.ismael.ticketfu.repository.UserRepository;
import com.ismael.ticketfu.security.JWTService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {


    private final JWTService jwtService;


    public final UserRepository userRepository;


    public final AuthenticationManager authenticationManager;


    private final PasswordEncoder passwordEncoder ;
    @Transactional
    public UserResponse register(RegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "El usuario " + request.getEmail() + " ya existe");
        }

        Users user = new Users();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // El servidor decide estos valores
        user.setRole(Role.ROLE_CUSTOMER);
        user.setIsEnabled(true);

        user = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());

        return response;
    }
    @Transactional
    public String verificar(Users user) {
        // authenticationManager lanzará BadCredentialsException si la contraseña o usuario no coinciden
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        if (authentication.isAuthenticated()) {
            return jwtService.generarToken(user.getUsername());
        }

        throw new RuntimeException("Error en la autenticación");
    }

    //METODOS CRUD QUE DEBEN IR::::S


    @Transactional
    public List<Users> getAll(){
        return userRepository.findAll();
    }

    public Users getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id está vacío");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la usuario con id: " + id));
    }
    @Transactional
    public Users update(Long id, Users user){
        if (id == null || user == null){
            throw  new IllegalArgumentException("Parametros nulos");
        }else {
            Users nuevo = userRepository.findById(id).orElseThrow(()
                    -> new ResourceNotFoundException("usuario no encontrada"));
            nuevo.setFirstName(user.getFirstName());
            nuevo.setLastName(user.getLastName());
            nuevo.setEmail(user.getEmail());
            nuevo.setPhoneNumber(user.getPhoneNumber());
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                nuevo.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            return  userRepository.save(nuevo);
        }
    }
    @Transactional
    public void delete(Long id){
        if (id== null){
            throw  new IllegalArgumentException("EL id esta vacia");
        }
        if(!userRepository.existsById(id)){
            throw  new ResourceNotFoundException("No existe el usuario con ese ID");
        }
        userRepository.deleteById(id);
    }
    @Transactional
    public List<Users> createAll(List<Users> users) {
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("La lista de usuarios no puede estar vacía");
        }

        // Encriptamos las contraseñas de cada usuario de la lista antes de guardar
        users.forEach(u -> u.setPassword(passwordEncoder.encode(u.getPassword())));

        return userRepository.saveAll(users);
    }
}