package us.mcstats.serverstats.database.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.mcstats.serverstats.database.entities.Server;

public interface ServerRepository extends MongoRepository<Server, String> {
    Server findByNameIgnoreCase(String name);
}
