# DuckHome

DuckHome is an open-source home automation app using the ClusterDuck Protocol. This tool allows you to communicate with DuckLink devices to receive data from sensors and share data with actuators.

DuckLinks, or Ducks, are simple Internet-of-Things devices that use the ClusterDuck Protocol firmware and sync with others nearby to cover large areas as a mesh network. 

### Using the app

#### 1. Build a ClusterDuck Network

You will need to setup a simple ClusterDuck Network to use the app to interface with your Ducks. In addition, you will need a special [BLE Duck](https://github.com/ClusterDuck-Protocol/ClusterDuck-Protocol/tree/master/examples/4.Ble-Duck-App) to serve as the central hub of your home automation network.

#### 2. Use Android Studio to generate an APK and install the app.

#### 3. Connect the App to your BLE Duck

1. Go to your bluetooth settings and pair to your BLE Duck for the first time.
2. Open the DuckHome App
3. Press "SEARCH" at the top left of the screen
4. Select the Duck you previously paired to
5. Press "CONNECT" in the top right of the screen