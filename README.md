# DuckHome

DuckHome is an open-source home automation app using the ClusterDuck Protocol that allows you to communicate with DuckLink devices to receive data from sensors and share data with actuators.
DuckLinks, or Ducks, are simple Internet-of-Things devices that use the ClusterDuck Protocol firmware and sync with others nearby to cover large areas as a mesh network. 

### Using the app

#### 1. Build a ClusterDuck Network.

You will need to create a simple ClusterDuck Network to use the app and interface with your Ducks.
Use a special [Hub-Duck](https://github.com/PranavSitaraman/DuckHome/blob/main/Ducks/Hub-Duck/Hub-Duck.ino) to serve as the central hub of your home automation network.
Set up [Sensor-Ducks](https://github.com/PranavSitaraman/DuckHome/blob/main/Ducks/Sensor-Duck/Sensor-Duck.ino) and [Actuator-Ducks](https://github.com/PranavSitaraman/DuckHome/blob/main/Ducks/Actuator-Duck/Actuator-Duck.ino) as appropriate for each of your peripherals, giving them unique identifiers starting with either an 'S' or an 'A' respectively.

#### 2. Install the App.

Use Android Studio to generate an APK and install the app.

#### 3. Connect the App to your Hub-Duck.

1. Go to your Bluetooth settings and pair to your Hub-Duck for the first time.
2. Open the DuckHome app.
3. Press the "SEARCH" button at the top left of the screen.
4. Select the Hub-Duck you previously paired to.
5. Press the "CONNECT" button at the top right of the screen.

#### 4. Use the App to Control your Ducks.

Wait for Sensor-Ducks and Actuator-Ducks to appear as they are recognized by the Hub-Duck.
Adjust the target position of Actuator-Ducks as necessary.