package com.ProjectCC.dero.service;

import com.ProjectCC.dero.dto.UserDTO;
import com.ProjectCC.dero.exceptions.UserNotFoundException;
import com.ProjectCC.dero.model.User;
import com.ProjectCC.dero.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private UserNotFoundException userNotFoundException;


    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public ResponseEntity<UserDTO> findByEmail(String email) {
        String newe = email + ".com";
        Optional<User> opt = Optional.ofNullable(this.userRepository.findByEmail(newe));
        User user = opt.get();
        return new ResponseEntity<>(modelMapper.map(user, UserDTO.class), HttpStatus.OK);
    }

    public ResponseEntity<UserDTO> findById(Long id) {
        try {
            Optional<User> opt = this.userRepository.findById(id);
            User user;
            if (opt.isPresent())
                user = opt.get();
            else {
                throw userNotFoundException;
            }
            return new ResponseEntity<>(modelMapper.map(user, UserDTO.class), HttpStatus.FOUND);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<UserDTO> edit(UserDTO userDTO) {
        Optional<User> opt = this.userRepository.findById(userDTO.getId());
        User user = opt.get();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setAddress(userDTO.getAddress());
        user.setCity(userDTO.getCity());
        user.setCountry(userDTO.getCountry());
        user.setJmbg(userDTO.getJmbg());
        user.setTelephone(userDTO.getTelephone());
        this.userRepository.save(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteById(Long id) {
        this.userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
