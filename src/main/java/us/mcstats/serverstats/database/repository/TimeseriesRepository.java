package us.mcstats.serverstats.database.repository;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import us.mcstats.serverstats.database.entities.Timeseries;

import java.time.LocalDateTime;
import java.util.Date;

public interface TimeseriesRepository extends MongoRepository<Timeseries, Timeseries.CompositeKey> {
    @DeleteQuery(value="{ '_id.t': {'$lte': ?0 } }")
    Long deleteAllRecordsBefore(LocalDateTime date);
}
