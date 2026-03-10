import serial
import requests
import time
import json


SERIAL_PORT = 'COM3'
BAUD_RATE = 9600

SERVER_URL = 'http://localhost:8080/api/data'

PLANT_ID = '9'

DEFAULT_LIGHT = 50.0

def parse_sensor_data(lines):
    """Parse the sensor data from Arduino serial output"""
    moisture = None
    temperature = None
    
    for i, line in enumerate(lines):
        if "Soil Moisture Value:" in line and i + 1 < len(lines):
            try:
                moisture = float(lines[i + 1].strip())
                moisture = ((1023 - moisture) / 1023) * 100
            except ValueError:
                pass
        
        if "Temperature:" in line and i + 1 < len(lines):
            try:
                temperature = float(lines[i + 1].strip())
            except ValueError:
                pass
    
    return moisture, temperature

def send_to_server(plant_id, temperature, moisture, light):
    """Send sensor data to Spring Boot server"""
    payload = {
        "plantId": plant_id,
        "temperature": temperature,
        "soilMoisture": moisture,
        "light": light
    }
    
    try:
        response = requests.post(SERVER_URL, json=payload)
        if response.status_code == 200:
            print(f"Data sent successfully: {payload}")
        else:
            print(f"Failed to send data. Status code: {response.status_code}")
    except Exception as e:
        print(f"Error sending data: {e}")

def main():
    print(f"Starting Arduino data bridge...")
    print(f"Connecting to Arduino on {SERIAL_PORT}...")
    
    try:
        ser = serial.Serial(SERIAL_PORT, BAUD_RATE, timeout=1)
        time.sleep(2)
        print("Connected to Arduino!")
        
        buffer = []
        last_send_time = time.time()
        send_interval = 3600
        
        while True:
            if ser.in_waiting > 0:
                line = ser.readline().decode('utf-8', errors='ignore').strip()
                if line:
                    print(line)
                    buffer.append(line)
                    if len(buffer) > 10:
                        buffer.pop(0)
                    current_time = time.time()
                    if current_time - last_send_time >= send_interval:
                        moisture, temperature = parse_sensor_data(buffer)
                        
                        if moisture is not None and temperature is not None:
                            send_to_server(PLANT_ID, temperature, moisture, DEFAULT_LIGHT)
                            last_send_time = current_time
                        else:
                            print("Incomplete sensor data, skipping send")
            
            time.sleep(0.1)
    
    except serial.SerialException as e:
        print(f"Error connecting to Arduino: {e}")
        print(f"Available ports might be: COM1, COM3, COM4, etc.")
    except KeyboardInterrupt:
        print("\nStopping data bridge...")
    finally:
        if 'ser' in locals() and ser.is_open:
            ser.close()
            print("Serial connection closed")

if __name__ == "__main__":
    main()