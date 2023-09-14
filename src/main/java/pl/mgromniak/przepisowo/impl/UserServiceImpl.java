package pl.mgromniak.przepisowo.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.mgromniak.przepisowo.Entity.Fridge;
import pl.mgromniak.przepisowo.Entity.Role;
import pl.mgromniak.przepisowo.Entity.User;
import pl.mgromniak.przepisowo.Repository.FridgeRepository;
import pl.mgromniak.przepisowo.Repository.RoleRepository;
import pl.mgromniak.przepisowo.Service.UserService;
import pl.mgromniak.przepisowo.dto.UserDto;
import pl.mgromniak.przepisowo.Repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private FridgeRepository fridgeRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           FridgeRepository fridgeRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.fridgeRepository = fridgeRepository;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        Fridge fridge = new Fridge();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFridge(fridge);

        Role role = roleRepository.findByName("ROLE_ADMIN");
        if(role == null){
            role = checkRoleExist();
        }
        user.setRoles(List.of(role));
        fridgeRepository.save(fridge);
        userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String email) {
        return userRepository.findUserByUsername(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        return userDto;
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }
}
