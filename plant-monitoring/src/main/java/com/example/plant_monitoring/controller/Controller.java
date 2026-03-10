package com.example.plant_monitoring.controller;

import com.example.plant_monitoring.model.Plant;
import com.example.plant_monitoring.repository.PlantRepository;
import com.example.plant_monitoring.repository.SensorDataRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/control")
@CrossOrigin
public class Controller {

    private final PlantRepository plantRepo;
    private final SensorDataRepository dataRepo;

    public Controller(PlantRepository plantRepo, SensorDataRepository dataRepo) {
        this.plantRepo = plantRepo;
        this.dataRepo = dataRepo;
    }

    @GetMapping("/plants")
    public List<Map<String, Object>> getAllPlants() {
        List<Plant> plants = plantRepo.findAll();

        return plants.stream().map(plant -> {
            Map<String, Object> plantData = new HashMap<>();
            plantData.put("id", plant.getId());
            plantData.put("name", plant.getName());
            plantData.put("optimalTemperature", plant.getOptimalTemperature());
            plantData.put("optimalMoistureMin", plant.getOptimalMoistureMin());
            plantData.put("optimalMoistureMax", plant.getOptimalMoistureMax());
            plantData.put("optimalLight", plant.getOptimalLight());
            plantData.put("autoMode", plant.isAutoMode());
            plantData.put("targetTemperature", plant.getTargetTemperature());
            plantData.put("targetLight", plant.getTargetLight());
            plantData.put("targetMoisture", plant.getTargetMoisture());
            plantData.put("currentTemperature", plant.getCurrentTemperature());
            plantData.put("currentSoilMoisture", plant.getCurrentMoisture());
            plantData.put("currentLight", plant.getCurrentLight());
            return plantData;
        }).collect(Collectors.toList());
    }

    @PostMapping("/auto/{id}")
    public String toggleAutoMode(@PathVariable Long id, @RequestParam boolean enable) {
        Plant plant = plantRepo.findById(id).orElse(null);
        if (plant == null) return "Plant not found.";
        plant.setAutoMode(enable);
        if (enable) {
            plant.setTargetTemperature(null);
            plant.setTargetLight(null);
            plant.setTargetMoisture(null);
        }

        plantRepo.save(plant);
        return "Auto mode for " + plant.getName() + " set to " + enable;
    }

    @PostMapping("/temperature/{id}/target")
    public String setTargetTemperature(@PathVariable Long id, @RequestParam double target) {
        Plant plant = plantRepo.findById(id).orElse(null);
        if (plant == null) return "Plant not found.";

        plant.setTargetTemperature(target);
        plantRepo.save(plant);
        return "Target temperature set to " + target + "°C for " + plant.getName();
    }

    @PostMapping("/light/{id}/target")
    public String setTargetLight(@PathVariable Long id, @RequestParam double target) {
        Plant plant = plantRepo.findById(id).orElse(null);
        if (plant == null) return "Plant not found.";

        plant.setTargetLight(target);
        plantRepo.save(plant);
        return "Target light set to " + target + " for " + plant.getName();
    }

    @PostMapping("/water/{id}/target")
    public String setTargetMoisture(@PathVariable Long id, @RequestParam double target) {
        Plant plant = plantRepo.findById(id).orElse(null);
        if (plant == null) return "Plant not found.";

        plant.setTargetMoisture(target);
        plantRepo.save(plant);
        return "Target moisture set to " + target + "% for " + plant.getName();
    }
}