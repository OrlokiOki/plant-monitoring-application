package com.example.plant_monitoring.repository;

import com.example.plant_monitoring.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    SensorData findTopByOrderByTimeStampDesc();

    SensorData findTopByPlantIdOrderByTimeStampDesc(String plantId);

    List<SensorData> findByPlantId(String plantId);
}