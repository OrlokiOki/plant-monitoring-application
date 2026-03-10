package com.example.plant_monitoring.service;

import com.example.plant_monitoring.model.Plant;
import com.example.plant_monitoring.model.SensorData;
import com.example.plant_monitoring.repository.PlantRepository;
import com.example.plant_monitoring.repository.SensorDataRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManualModeAdjuster {
    private final PlantRepository plantRepo;
    private final SensorDataRepository dataRepo;
    
    private static final double tempIncrement = 0.3;
    private static final double lightIncrement = 0.5;
    private static final double moistureIncrement = 2.0;

    public ManualModeAdjuster(PlantRepository plantRepo, SensorDataRepository dataRepo) {
        this.plantRepo = plantRepo;
        this.dataRepo = dataRepo;
    }

    @Scheduled(fixedRate = 60000)
    public void adjustTowardsTargets() {
        List<Plant> plants = plantRepo.findAll();

        for (Plant plant : plants) {
            if (!plant.isAutoMode()) {
                processManualAdjustment(plant);
            }
        }
    }

    private void processManualAdjustment(Plant plant) {
        SensorData latest = dataRepo.findTopByPlantIdOrderByTimeStampDesc(plant.getId().toString());
        if (latest == null) return;

        boolean changed = false;

        if (plant.getTargetTemperature() != null) {
            double current = latest.getTemperature();
            double target = plant.getTargetTemperature();

            if (Math.abs(current - target) > 0.1) {
                if (current < target) {
                    latest.setTemperature(Math.min(current + tempIncrement, target));
                } else {
                    latest.setTemperature(Math.max(current - tempIncrement, target));
                }
                changed = true;
                System.out.println("MANUAL " + plant.getName() + " - Temperature: " +
                        current + " → " + latest.getTemperature() + " (target: " + target + ")");
            } else {
                plant.setTargetTemperature(null);
                plantRepo.save(plant);
                System.out.println("MANUAL " + plant.getName() + " - Temperature target reached!");
            }
        }

        if (plant.getTargetLight() != null) {
            double current = latest.getLight();
            double target = plant.getTargetLight();

            if (Math.abs(current - target) > 0.5) {
                if (current < target) {
                    latest.setLight(Math.min(current + lightIncrement, target));
                } else {
                    latest.setLight(Math.max(current - lightIncrement, target));
                }
                changed = true;
                System.out.println("MANUAL " + plant.getName() + " - Light: " +
                        current + " → " + latest.getLight() + " (target: " + target + ")");
            } else {
                plant.setTargetLight(null);
                plantRepo.save(plant);
                System.out.println("MANUAL " + plant.getName() + " - Light target reached!");
            }
        }

        if (plant.getTargetMoisture() != null) {
            double current = latest.getSoilMoisture();
            double target = plant.getTargetMoisture();

            if (Math.abs(current - target) > 0.5) {
                if (current < target) {
                    latest.setSoilMoisture(Math.min(current + moistureIncrement, target));
                } else {
                    latest.setSoilMoisture(Math.max(current - moistureIncrement, target));
                }
                changed = true;
                System.out.println("MANUAL " + plant.getName() + " - Moisture: " +
                        current + " → " + latest.getSoilMoisture() + " (target: " + target + ")");
            } else {
                plant.setTargetMoisture(null);
                plantRepo.save(plant);
                System.out.println("MANUAL " + plant.getName() + " - Moisture target reached!");
            }
        }

        if (changed) {
            dataRepo.save(latest);
        }
    }
}