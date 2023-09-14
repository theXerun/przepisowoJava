package pl.mgromniak.przepisowo.Service;

import pl.mgromniak.przepisowo.Entity.User;
import pl.mgromniak.przepisowo.dto.UserDto;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByUsername(String username);

    List<UserDto> findAllUsers();
}
