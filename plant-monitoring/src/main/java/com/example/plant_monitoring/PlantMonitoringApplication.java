package com.example.plant_monitoring;

import com.example.plant_monitoring.model.Plant;
import com.example.plant_monitoring.repository.PlantRepository;
import com.example.plant_monitoring.repository.SensorDataRepository;
import com.example.plant_monitoring.service.SimulatedDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@EnableScheduling
public class PlantMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlantMonitoringApplication.class, args);
	}

	@Bean
	CommandLineRunner runOnStartup(SimulatedDataService simulatedService, PlantRepository plantRepo, SensorDataRepository sensorRepo) {
		return new CommandLineRunner() {
			@Override
			@Transactional
			public void run(String[] args) throws Exception {
				System.out.println("Plant Monitoring System Starting");
				System.out.println("Cleaning up old data...");
				sensorRepo.deleteAll();
				plantRepo.deleteAll();
				System.out.println("All old plants and sensor data deleted");

				simulatedService.initializeFromFile(new org.springframework.core.io.ClassPathResource("plant_data.txt").getFile().getAbsolutePath());

				Plant plant9 = new Plant();
				plant9.setName("plant_9");
				plant9.setOptimalTemperature(22.0);
				plant9.setOptimalLight(20.0);
				plant9.setOptimalMoistureMin(20.0);
				plant9.setOptimalMoistureMax(50.0);
				plantRepo.save(plant9);
				System.out.println("Created plant_9 (ID: " + plant9.getId() + ") for Arduino data");

				System.out.println("Startup Complete");
			}
		};
	}
}