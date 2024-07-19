#include <string>
#include <arduino-timer.h>
#include <MamaDuck.h>

#ifdef SERIAL_PORT_USBVIRTUAL
#define Serial SERIAL_PORT_USBVIRTUAL
#endif

String sendActuatorPosition();
bool runActuator(void *);

MamaDuck duck;
auto timer = timer_create_default();
const int INTERVAL_MS = 10000;
volatile int position = 1;
std::string deviceId("THERMOST");

void setup()
{
  std::vector<byte> devId;
  devId.insert(devId.end(), deviceId.begin(), deviceId.end());
  duck.setDeviceId(devId);
  duck.setupSerial();
  duck.setupRadio();
  duck.onReceiveDuckData(handleDuckData);
  timer.every(INTERVAL_MS, runActuator);
}

void handleDuckData(std::vector<byte> packetBuffer)
{
  timer.tick<void>();
  CdpPacket packet = CdpPacket(packetBuffer);
  std::string payload(packet.data.begin(), packet.data.end());
  if (payload.find(deviceId) != std::string::npos)
  {
    position = stoi(payload.substr(payload.find(deviceId) + deviceId.length()));
  }
}

void loop()
{
  timer.tick();
  duck.run();
}

String sendActuatorPosition()
{
  return String(position);
}

bool runActuator(void *)
{
  String message = "A " + String(deviceId.c_str()) + " " + sendActuatorPosition();
  int err = duck.sendData(topics::status, message.c_str());
  if (err == DUCK_ERR_NONE) return true;
  return false;
}