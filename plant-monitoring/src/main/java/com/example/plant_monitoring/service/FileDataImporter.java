package com.example.plant_monitoring.service;

import com.example.plant_monitoring.model.Plant;
import com.example.plant_monitoring.model.SensorData;
import com.example.plant_monitoring.repository.PlantRepository;
import com.example.plant_monitoring.repository.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
public class FileDataImporter {

    private final SensorDataRepository sensorRepo;
    private final PlantRepository plantRepo;

    public FileDataImporter(SensorDataRepository sensorRepo, PlantRepository plantRepo) {
        this.sensorRepo = sensorRepo;
        this.plantRepo = plantRepo;
    }

    public void importFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length != 4) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                String plantName = parts[0];
                double temp = Double.parseDouble(parts[1]);
                double light = Double.parseDouble(parts[2]);
                double soilMoisture = Double.parseDouble(parts[3]);

                Plant plant = plantRepo.findByName(plantName);
                if (plant == null) {
                    plant = new Plant();
                    plant.setName(plantName);
                    plant.setOptimalLight(15);
                    plant.setOptimalTemperature(20);
                    plant.setOptimalMoistureMin(30);
                    plant.setOptimalMoistureMax(70);
                    plantRepo.save(plant);
                }

                SensorData data = new SensorData();
                data.setPlantId(plant.getId().toString());
                data.setTemperature(temp);
                data.setLight(light);
                data.setSoilMoisture(soilMoisture);
                sensorRepo.save(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}