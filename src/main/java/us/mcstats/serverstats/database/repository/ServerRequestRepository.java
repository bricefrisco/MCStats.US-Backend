package us.mcstats.serverstats.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.mcstats.serverstats.database.entities.ServerRequest;

public interface ServerRequestRepository extends JpaRepository<ServerRequest, Integer> {
}
