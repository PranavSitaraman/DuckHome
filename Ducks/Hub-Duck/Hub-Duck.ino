#include <MamaDuck.h>

#ifdef SERIAL_PORT_USBVIRTUAL
#define Serial SERIAL_PORT_USBVIRTUAL
#endif

#include "BluetoothSerial.h"
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to enable it
#endif

BluetoothSerial SerialBT;
MamaDuck duck;

void setup()
{
  std::string deviceId("MAMA0001");
  std::vector<byte> devId;
  devId.insert(devId.end(), deviceId.begin(), deviceId.end());
  duck.setDeviceId(devId);
  duck.setupSerial();
  duck.setupRadio();
  duck.onReceiveDuckData(handleDuckData);
  SerialBT.begin("MamaDuck1"); 
}

void loop()
{
  if (SerialBT.available())
  {
    String text = SerialBT.readString();
    duck.sendData(topics::status, text.c_str());
  }
  duck.run();
}

void handleDuckData(std::vector<byte> packetBuffer)
{
  CdpPacket packet = CdpPacket(packetBuffer);
  std::string payload(packet.data.begin(), packet.data.end());
  uint8_t buffer[256];
  memcpy(buffer, payload.c_str(), payload.length());
  SerialBT.write(buffer, payload.length());
}