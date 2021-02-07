package us.mcstats.serverstats.database.repository;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import us.mcstats.serverstats.database.entities.Timeseries;

import java.time.LocalDateTime;

public interface TimeseriesRepository extends MongoRepository<Timeseries, Timeseries.CompositeKey> {
    @DeleteQuery(value="{ '_id.t': {'$lte': ?0 } }")
    Long deleteAllRecordsBefore(LocalDateTime date);

    @DeleteQuery(value="{ '_id.n': ?0 }")
    Long deleteRecordsByServerName(String name);
}
