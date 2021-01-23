package us.mcstats.serverstats.database.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.mcstats.serverstats.database.entities.Info;

public interface InfoRepository extends MongoRepository<Info, String> {
}
