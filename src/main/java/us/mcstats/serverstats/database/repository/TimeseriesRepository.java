package us.mcstats.serverstats.database.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import us.mcstats.serverstats.database.entities.Timeseries;

public interface TimeseriesRepository extends MongoRepository<Timeseries, Timeseries.CompositeKey> {
}
