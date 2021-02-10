package us.mcstats.serverstats.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import us.mcstats.serverstats.database.entities.Server;

public interface ServerRepository extends JpaRepository<Server, Integer> {
    Server findByNameIgnoreCase(String name);

    @Query(value = "SELECT SUM(online_players) FROM servers", nativeQuery = true)
    Long getTotalPlayersOnline();
}
