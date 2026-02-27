package com.luciano.calculoImplicito.repository;

import com.luciano.calculoImplicito.model.Simulation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SimulationRepository extends MongoRepository<Simulation, String> {
}