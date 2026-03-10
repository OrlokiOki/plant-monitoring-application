package com.example.plant_monitoring.service;

import com.example.plant_monitoring.model.Plant;
import com.example.plant_monitoring.model.SensorData;
import com.example.plant_monitoring.repository.PlantRepository;
import com.example.plant_monitoring.repository.SensorDataRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimulatedDataService {

    private final PlantRepository plantRepo;
    private final SensorDataRepository sensorRepo;

    private List<Map<String, Double>> datasetQueue;
    private int currentDatasetIndex = 0;
    private boolean initialized = false;

    public SimulatedDataService(PlantRepository plantRepo, SensorDataRepository sensorRepo) {
        this.plantRepo = plantRepo;
        this.sensorRepo = sensorRepo;
        this.datasetQueue = new ArrayList<>();
    }

    public void initializeFromFile(String filePath) {
        if (initialized) {
            return;
        }

        System.out.println("Initializing Simulated Plants");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Map<String, Double> currentDataset = new HashMap<>();
            int plantCount = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 4) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                try {
                    String plantName = parts[0].trim();
                    double temperature = Double.parseDouble(parts[1].trim());
                    double light = Double.parseDouble(parts[2].trim());
                    double moisture = Double.parseDouble(parts[3].trim());

                    currentDataset.put(plantName + "_temp", temperature);
                    currentDataset.put(plantName + "_light", light);
                    currentDataset.put(plantName + "_moisture", moisture);

                    plantCount++;

                    if (plantCount == 8) {
                        datasetQueue.add(new HashMap<>(currentDataset));
                        currentDataset.clear();
                        plantCount = 0;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing line: " + line + " - " + e.getMessage());
                    continue;
                }
            }
            createSimulatedPlants();

            if (!datasetQueue.isEmpty()) {
                sendCurrentDataset();
            }

            initialized = true;
            reader.close();

        } catch (IOException e) {
            System.err.println("Error reading simulated data file: " + e.getMessage());
        }
    }

    @Transactional
    private void createSimulatedPlants() {
        for (int i = 1; i <= 8; i++) {
            String plantName = "plant_" + i;
            Plant existing = plantRepo.findByName(plantName);
            if (existing == null) {
                Plant plant = new Plant();
                plant.setName(plantName);
                plant.setOptimalTemperature(22.0);
                plant.setOptimalLight(20.0);
                plant.setOptimalMoistureMin(28.0);
                plant.setOptimalMoistureMax(38.0);
                plantRepo.save(plant);
                System.out.println("Created simulated plant: " + plantName);
            } else {
                System.out.println("Plant already exists: " + plantName);
            }
        }
    }

    @Scheduled(fixedRate = 600000)
    public void sendScheduledData() {
        if (!initialized || datasetQueue.isEmpty()) {
            return;
        }

        sendCurrentDataset();
    }

    @Transactional
    private void sendCurrentDataset() {
        Map<String, Double> dataset = datasetQueue.get(currentDatasetIndex);

        System.out.println("Sending Dataset " + (currentDatasetIndex + 1) + " / " + datasetQueue.size() + " ===");

        for (int i = 1; i <= 8; i++) {
            String plantName = "plant_" + i;
            Plant plant = plantRepo.findByName(plantName);
            if (plant == null) {
                System.out.println("Plant " + plantName + " not found, skipping");
                continue;
            }

            Double temp = dataset.get(plantName + "_temp");
            Double light = dataset.get(plantName + "_light");
            Double moisture = dataset.get(plantName + "_moisture");

            if (temp != null && light != null && moisture != null) {
                SensorData sensorData = new SensorData();
                sensorData.setPlantId(plant.getId().toString());
                sensorData.setTemperature(temp);
                sensorData.setLight(light);
                sensorData.setSoilMoisture(moisture);
                sensorRepo.save(sensorData);

                System.out.println(plantName + " (ID: " + plant.getId() + ") - Temp: " + temp + ", Light: " + light + ", Moisture: " + moisture + "%");
            }
        }
        currentDatasetIndex = (currentDatasetIndex + 1) % datasetQueue.size();

        if (currentDatasetIndex == 0) {
            System.out.println("Looping back to first datase");
        }
    }
}