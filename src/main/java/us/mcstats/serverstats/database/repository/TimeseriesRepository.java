package us.mcstats.serverstats.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import us.mcstats.serverstats.database.entities.Timeseries;
import us.mcstats.serverstats.models.timeseries.TimeseriesDto;

import java.sql.Timestamp;
import java.util.List;

public interface TimeseriesRepository extends JpaRepository<Timeseries, Timeseries.CompositeId> {
    Long deleteAllByIdServerName(String serverName);

    @Query(value = "SELECT time_bucket((INTERVAL '1 second' * :interval), time) AS t," +
            "ceil(avg(num_online_players)) AS o " +
            "FROM timeseries " +
            "WHERE server_name = :serverName " +
            "AND time <= :lt AND time >= :gt " +
            "GROUP BY t " +
            "ORDER BY t", nativeQuery = true)
    List<TimeseriesDto> selectBetween(
            @Param("serverName") String serverName,
            @Param("lt") Timestamp lt,
            @Param("gt") Timestamp gt,
            @Param("interval") Integer interval
    );
}
