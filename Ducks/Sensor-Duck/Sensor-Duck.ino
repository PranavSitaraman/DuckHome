#include <arduino-timer.h>
#include <string>
#include <CDP.h>

#ifdef SERIAL_PORT_USBVIRTUAL
#define Serial SERIAL_PORT_USBVIRTUAL
#endif

bool sendSensorData();
bool runSensor(void *);

DuckLink duck;
auto timer = timer_create_default();
const int INTERVAL_MS = 10000;
std::string deviceId("SENS0001");

void setup()
{
  std::vector<byte> devId;
  devId.insert(devId.end(), deviceId.begin(), deviceId.end());
  duck.setDeviceId(devId);
  duck.setupSerial();
  duck.setupRadio();
  timer.every(INTERVAL_MS, runSensor);
}

void loop()
{
  timer.tick();
  duck.run();
}

bool sendSensorData()
{
  String message = String(deviceId.c_str()) + " " + String(random(1, 100));
  int err = duck.sendData(topics::status, message.c_str());
  if (err == DUCK_ERR_NONE) return true;
  return false;
}

bool runSensor(void *)
{
  return sendSensorData();
}