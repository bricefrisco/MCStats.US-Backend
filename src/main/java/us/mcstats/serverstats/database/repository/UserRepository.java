package us.mcstats.serverstats.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.mcstats.serverstats.database.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByEmailIgnoreCase(String email);
}
