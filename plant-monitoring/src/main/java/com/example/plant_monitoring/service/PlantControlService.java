package com.example.plant_monitoring.service;

import com.example.plant_monitoring.model.Plant;
import com.example.plant_monitoring.model.SensorData;
import com.example.plant_monitoring.repository.PlantRepository;
import com.example.plant_monitoring.repository.SensorDataRepository;
import org.springframework.stereotype.Service;

@Service
public class PlantControlService {
    private final PlantRepository plantRepo;
    private final SensorDataRepository dataRepo;

    public PlantControlService(PlantRepository plantRepo, SensorDataRepository dataRepo) {
        this.plantRepo = plantRepo;
        this.dataRepo = dataRepo;
    }

    public String checkAndAdjust(Plant plant) {
        StringBuilder actions = new StringBuilder();

        SensorData latest = dataRepo.findTopByPlantIdOrderByTimeStampDesc(plant.getId().toString());
        if (latest == null) {
            return "No sensor data available for " + plant.getName();
        }

        final double tempIncrement = 0.3;
        final double lightIncrement = 0.5;
        final double moistureIncrement = 2.0;

        System.out.println("AUTO ADJUST for " + plant.getName());

        boolean changed = false;

        if (latest.getTemperature() < plant.getOptimalTemperature() - 1) {
            double newTemp = latest.getTemperature() + tempIncrement;
            newTemp = Math.min(newTemp, plant.getOptimalTemperature());
            latest.setTemperature(newTemp);
            actions.append("Heating (+").append(tempIncrement).append("). ");
            System.out.println("Temperature: " + (newTemp - tempIncrement) + " -> " + newTemp);
            changed = true;
        } else if (latest.getTemperature() > plant.getOptimalTemperature() + 1) {
            double newTemp = latest.getTemperature() - tempIncrement;
            newTemp = Math.max(newTemp, plant.getOptimalTemperature());
            latest.setTemperature(newTemp);
            actions.append("Cooling (-").append(tempIncrement).append("). ");
            System.out.println("Temperature: " + (newTemp + tempIncrement) + " -> " + newTemp);
            changed = true;
        }

        if (latest.getLight() < plant.getOptimalLight() - 1) {
            double newLight = latest.getLight() + lightIncrement;
            newLight = Math.min(newLight, plant.getOptimalLight());
            latest.setLight(newLight);
            actions.append("Increasing light (+").append(lightIncrement).append("). ");
            System.out.println("Light: " + (newLight - lightIncrement) + " -> " + newLight);
            changed = true;
        } else if (latest.getLight() > plant.getOptimalLight() + 1) {
            double newLight = latest.getLight() - lightIncrement;
            newLight = Math.max(newLight, plant.getOptimalLight());
            latest.setLight(newLight);
            actions.append("Decreasing light (-").append(lightIncrement).append("). ");
            System.out.println("Light: " + (newLight + lightIncrement) + " -> " + newLight);
            changed = true;
        }

        if (latest.getSoilMoisture() < plant.getOptimalMoistureMin() - 2) {
            double newMoisture = latest.getSoilMoisture() + moistureIncrement;
            newMoisture = Math.min(newMoisture, plant.getOptimalMoistureMin());
            latest.setSoilMoisture(newMoisture);
            actions.append("Watering (+").append(moistureIncrement).append("%). ");
            System.out.println("Moisture: " + (newMoisture - moistureIncrement) + " -> " + newMoisture);
            changed = true;
        }

        if (changed) {
            dataRepo.save(latest);
        }

        if (actions.length() == 0) {
            System.out.println("All parameters within optimal range");
            return "All conditions optimal for " + plant.getName();
        }

        return actions.toString();
    }
}