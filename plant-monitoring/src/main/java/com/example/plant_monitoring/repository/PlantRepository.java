package com.example.plant_monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.plant_monitoring.model.Plant;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    Plant findByName(String name);
}