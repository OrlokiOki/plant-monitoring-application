package com.example.plant_monitoring.service;

import com.example.plant_monitoring.model.Plant;
import com.example.plant_monitoring.repository.PlantRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoMonitor {
    private final PlantRepository plantRepo;
    private final PlantControlService controlService;

    public AutoMonitor(PlantRepository plantRepo, PlantControlService controlService) {
        this.plantRepo = plantRepo;
        this.controlService = controlService;
    }

    @Scheduled(fixedRate = 60000)
    public void monitorPlants() {
        List<Plant> plants = plantRepo.findAll();
        for (Plant plant : plants) {
            if (plant.isAutoMode()) {
                System.out.println("\nAUTO MODE Processing: " + plant.getName());
                String result = controlService.checkAndAdjust(plant);
                System.out.println("AUTO MODE " + result);
            }
        }
    }
}