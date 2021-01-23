package us.mcstats.serverstats.database.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.mcstats.serverstats.database.entities.User;

public interface UserRepository extends MongoRepository<User, String> {
    User getUserByEIgnoreCase(String email);
}
