package pl.mgromniak.przepisowo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mgromniak.przepisowo.Entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
