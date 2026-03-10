package com.example.plant_monitoring.model;

import jakarta.persistence.*;

@Entity
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private double optimalLight;
    private double optimalTemperature;
    private double optimalMoistureMin;
    private double optimalMoistureMax;

    private boolean autoMode = false;

    private Double targetTemperature;
    private Double targetLight;
    private Double targetMoisture;
    private Double currentTemperature;
    private Double currentLight;
    private Double currentMoisture;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOptimalTemperature() {
        return optimalTemperature;
    }

    public void setOptimalTemperature(double optimalTemperature) {
        this.optimalTemperature = optimalTemperature;
    }

    public double getOptimalMoistureMin() {
        return optimalMoistureMin;
    }

    public void setOptimalMoistureMin(double optimalMoistureMin) {
        this.optimalMoistureMin = optimalMoistureMin;
    }

    public double getOptimalMoistureMax() {
        return optimalMoistureMax;
    }

    public void setOptimalMoistureMax(double optimalMoistureMax) {
        this.optimalMoistureMax = optimalMoistureMax;
    }

    public double getOptimalLight() {
        return optimalLight;
    }

    public void setOptimalLight(double optimalLight) {
        this.optimalLight = optimalLight;
    }

    public boolean isAutoMode() {
        return autoMode;
    }

    public void setAutoMode(boolean autoMode) {
        this.autoMode = autoMode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTargetTemperature() {
        return targetTemperature;
    }

    public void setTargetTemperature(Double targetTemperature) {
        this.targetTemperature = targetTemperature;
    }

    public Double getTargetLight() {
        return targetLight;
    }

    public void setTargetLight(Double targetLight) {
        this.targetLight = targetLight;
    }

    public Double getTargetMoisture() {
        return targetMoisture;
    }

    public void setTargetMoisture(Double targetMoisture) {
        this.targetMoisture = targetMoisture;
    }

    public Double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(Double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public Double getCurrentLight() {
        return currentLight;
    }

    public void setCurrentLight(Double currentLight) {
        this.currentLight = currentLight;
    }

    public Double getCurrentMoisture() {
        return currentMoisture;
    }

    public void setCurrentMoisture(Double currentMoisture) {
        this.currentMoisture = currentMoisture;
    }
}