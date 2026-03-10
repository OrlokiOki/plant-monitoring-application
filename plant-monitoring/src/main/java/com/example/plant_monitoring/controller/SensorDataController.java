package com.example.plant_monitoring.controller;

import org.springframework.web.bind.annotation.*;
import com.example.plant_monitoring.model.SensorData;
import com.example.plant_monitoring.repository.SensorDataRepository;
import java.util.List;

@RestController
@RequestMapping("/api/data")
@CrossOrigin
public class SensorDataController {

    private final SensorDataRepository repo;

    public SensorDataController(SensorDataRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public SensorData receiveData(@RequestBody SensorData data) {
        System.out.println("RECEIVED SENSOR DATA");
        System.out.println("Plant ID: " + data.getPlantId());
        System.out.println("Temperature: " + data.getTemperature());
        System.out.println("Moisture: " + data.getSoilMoisture());
        System.out.println("Light: " + data.getLight());
        System.out.println("Timestamp: " + data.getTimeStamp());

        SensorData saved = repo.save(data);
        System.out.println("Saved to database with ID: " + saved.getId());

        return saved;
    }

    @GetMapping("/latest")
    public SensorData getLatestData() {
        return repo.findTopByOrderByTimeStampDesc();
    }

    @GetMapping
    public List<SensorData> getAllData() {
        return repo.findAll();
    }
}