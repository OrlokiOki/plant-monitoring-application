package com.example.plant_monitoring.service;

import com.example.plant_monitoring.model.Plant;
import com.example.plant_monitoring.model.SensorData;
import com.example.plant_monitoring.repository.PlantRepository;
import com.example.plant_monitoring.repository.SensorDataRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSyncService {

    private final PlantRepository plantRepo;
    private final SensorDataRepository sensorRepo;

    public DataSyncService(PlantRepository plantRepo, SensorDataRepository sensorRepo) {
        this.plantRepo = plantRepo;
        this.sensorRepo = sensorRepo;
    }

    @Scheduled(fixedRate = 5000)
    public void syncSensorDataToPlants() {
        List<Plant> plants = plantRepo.findAll();

        for (Plant plant : plants) {
            SensorData latest = sensorRepo.findTopByPlantIdOrderByTimeStampDesc(
                    plant.getId().toString()
            );

            if (latest != null) {
                plant.setCurrentTemperature(latest.getTemperature());
                plant.setCurrentLight(latest.getLight());
                plant.setCurrentMoisture(latest.getSoilMoisture());

                plantRepo.save(plant);
            }
        }
    }

    public void syncPlant(Long plantId) {
        Plant plant = plantRepo.findById(plantId).orElse(null);
        if (plant == null) return;

        SensorData latest = sensorRepo.findTopByPlantIdOrderByTimeStampDesc(
                plantId.toString()
        );

        if (latest != null) {
            plant.setCurrentTemperature(latest.getTemperature());
            plant.setCurrentLight(latest.getLight());
            plant.setCurrentMoisture(latest.getSoilMoisture());

            plantRepo.save(plant);
        }
    }
}