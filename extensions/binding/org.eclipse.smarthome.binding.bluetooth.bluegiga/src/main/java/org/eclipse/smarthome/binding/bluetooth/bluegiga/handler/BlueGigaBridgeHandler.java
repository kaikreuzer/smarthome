/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.bluetooth.bluegiga.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jdt.annotation.DefaultLocation;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.binding.bluetooth.BluetoothAdapter;
import org.eclipse.smarthome.binding.bluetooth.BluetoothAddress;
import org.eclipse.smarthome.binding.bluetooth.BluetoothBindingConstants;
import org.eclipse.smarthome.binding.bluetooth.BluetoothDevice;
import org.eclipse.smarthome.binding.bluetooth.BluetoothDeviceListener;
import org.eclipse.smarthome.binding.bluetooth.BluetoothDiscoveryListener;
import org.eclipse.smarthome.binding.bluetooth.bluegiga.BlueGigaBindingConstants;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zsmartsystems.bluetooth.bluegiga.BlueGigaCommand;
import com.zsmartsystems.bluetooth.bluegiga.BlueGigaEventListener;
import com.zsmartsystems.bluetooth.bluegiga.BlueGigaResponse;
import com.zsmartsystems.bluetooth.bluegiga.BlueGigaSerialHandler;
import com.zsmartsystems.bluetooth.bluegiga.command.attributeclient.BlueGigaAttributeWriteCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.attributeclient.BlueGigaAttributeWriteResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.attributeclient.BlueGigaFindInformationCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.attributeclient.BlueGigaFindInformationResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.attributeclient.BlueGigaReadByGroupTypeCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.attributeclient.BlueGigaReadByGroupTypeResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.attributeclient.BlueGigaReadByHandleCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.attributeclient.BlueGigaReadByHandleResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.connection.BlueGigaConnectionStatusEvent;
import com.zsmartsystems.bluetooth.bluegiga.command.connection.BlueGigaDisconnectCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.connection.BlueGigaDisconnectResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.connection.BlueGigaDisconnectedEvent;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaConnectDirectCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaConnectDirectResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaDiscoverCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaEndProcedureCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaEndProcedureResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaScanResponseEvent;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaSetModeCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaSetModeResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.gap.BlueGigaSetScanParametersCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaAddressGetCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaAddressGetResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaGetConnectionsCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaGetConnectionsResponse;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaGetInfoCommand;
import com.zsmartsystems.bluetooth.bluegiga.command.system.BlueGigaGetInfoResponse;
import com.zsmartsystems.bluetooth.bluegiga.enumeration.BgApiResponse;
import com.zsmartsystems.bluetooth.bluegiga.enumeration.BluetoothAddressType;
import com.zsmartsystems.bluetooth.bluegiga.enumeration.GapConnectableMode;
import com.zsmartsystems.bluetooth.bluegiga.enumeration.GapDiscoverMode;
import com.zsmartsystems.bluetooth.bluegiga.enumeration.GapDiscoverableMode;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * The {@link BlueGigaBridgeHandler} is responsible for interfacing to the BlueGiga Bluetooth adapter.
 * It provides a private interface for {@link BlueGigaBluetoothDevice}s to access the dongle and provides top
 * level adaptor functionality for scanning and arbitration.
 * <p>
 * The handler provides the serial interface to the dongle via the BlueGiga BG-API library.
 * <p>
 * In the BlueGiga dongle, we leave scanning enabled most of the time. Normally, it's just passive scanning, and active
 * scanning is enabled when we want to include new devices. Passive scanning is enough for us to receive beacons etc
 * that are transmitted periodically, and active scanning will get more information which may be useful when we are
 * including new devices.
 *
 * @author Chris Jackson - Initial contribution
 */
@NonNullByDefault({ DefaultLocation.PARAMETER, DefaultLocation.RETURN_TYPE, DefaultLocation.ARRAY_CONTENTS,
        DefaultLocation.TYPE_ARGUMENT, DefaultLocation.TYPE_BOUND, DefaultLocation.TYPE_PARAMETER })
public class BlueGigaBridgeHandler extends BaseBridgeHandler implements BluetoothAdapter, BlueGigaEventListener {

    private final Logger logger = LoggerFactory.getLogger(BlueGigaBridgeHandler.class);

    // The serial port.
    private SerialPort serialPort;

    // The serial port input stream.
    private InputStream inputStream;

    // The serial port output stream.
    private OutputStream outputStream;

    // The BlueGiga API handler
    private BlueGigaSerialHandler bgHandler;

    // The maximum number of connections this interface supports
    private int maxConnections = 0;

    private final int passiveScanInterval = 0x40;
    private final int passiveScanWindow = 0x08;

    private final int activeScanInterval = 0x40;
    private final int activeScanWindow = 0x20;

    // Our BT address
    private BluetoothAddress address;

    // Map of Bluetooth devices known to this bridge.
    // This is all devices we have heard on the network - not just things bound to the bridge
    private final Map<BluetoothAddress, BluetoothDevice> devices = new HashMap<>();

    // Map of open connections
    private final Map<Integer, BluetoothAddress> connections = new HashMap<Integer, BluetoothAddress>();

    // Set of discovery listeners
    protected final Set<BluetoothDiscoveryListener> discoveryListeners = new CopyOnWriteArraySet<>();

    // List of device listeners
    protected final ConcurrentHashMap<BluetoothAddress, BluetoothDeviceListener> deviceListeners = new ConcurrentHashMap<BluetoothAddress, BluetoothDeviceListener>();

    public BlueGigaBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public ThingUID getUID() {
        // being a BluetoothAdapter, we use the UID of our bridge
        return getThing().getUID();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // No commands supported for the bridge
    }

    @Override
    public void initialize() {
        final BlueGigaBridgeHandler me = this;
        final String portId = (String) getConfig().get(BlueGigaBindingConstants.CONFIGURATION_PORT);

        if (portId == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Serial port must be configured!");
            return;
        }
        openSerialPort(portId, 115200);
        bgHandler = new BlueGigaSerialHandler(inputStream, outputStream);
        // Create and send the reset command to the dongle
        bgHandler.addEventListener(me);
        bgHandler.addEventListener(me);

        updateStatus(ThingStatus.UNKNOWN);

        scheduler.submit(() -> {
            // Stop any procedures that are running
            bgStopProcedure();

            // Close all transactions
            BlueGigaCommand command; // = new BlueGigaResetCommand();
            command = new BlueGigaGetConnectionsCommand();
            BlueGigaGetConnectionsResponse connectionsResponse = (BlueGigaGetConnectionsResponse) bgHandler
                    .sendTransaction(command);
            if (connectionsResponse != null) {
                maxConnections = connectionsResponse.getMaxconn();
            }

            // Close all connections so we start from a known position
            for (int connection = 0; connection < maxConnections; connection++) {
                bgDisconnect(connection);
            }

            // Get our Bluetooth address
            command = new BlueGigaAddressGetCommand();
            BlueGigaAddressGetResponse addressResponse = (BlueGigaAddressGetResponse) bgHandler
                    .sendTransaction(command);
            if (addressResponse != null) {
                address = new BluetoothAddress(addressResponse.getAddress());
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }

            command = new BlueGigaGetInfoCommand();
            BlueGigaGetInfoResponse infoResponse = (BlueGigaGetInfoResponse) bgHandler.sendTransaction(command);

            // Set mode to non-discoverable etc.
            // Not doing this will cause connection failures later
            bgSetMode();

            // Start passive scan
            bgStartScanning(false, passiveScanInterval, passiveScanWindow);

            Map<String, String> properties = editProperties();
            properties.put(BluetoothBindingConstants.PROPERTY_MAXCONNECTIONS, Integer.toString(maxConnections));
            properties.put(Thing.PROPERTY_FIRMWARE_VERSION,
                    String.format("%d.%d", infoResponse.getMajor(), infoResponse.getMinor()));
            properties.put(Thing.PROPERTY_HARDWARE_VERSION, Integer.toString(infoResponse.getHardware()));
            properties.put(BlueGigaBindingConstants.PROPERTY_PROTOCOL,
                    Integer.toString(infoResponse.getProtocolVersion()));
            properties.put(BlueGigaBindingConstants.PROPERTY_LINKLAYER, Integer.toString(infoResponse.getLlVersion()));
            updateProperties(properties);
        });
    }

    @Override
    public void dispose() {
        closeSerialPort();
    }

    private void openSerialPort(final String serialPortName, int baudRate) {
        logger.debug("Connecting to serial port '{}'", serialPortName);
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortName);
            CommPort commPort = portIdentifier.open("org.openhab.binding.zigbee", 2000);
            serialPort = (gnu.io.SerialPort) commPort;
            serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    gnu.io.SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(gnu.io.SerialPort.FLOWCONTROL_RTSCTS_OUT);

            ((CommPort) serialPort).enableReceiveThreshold(1);
            serialPort.enableReceiveTimeout(2000);

            // RXTX serial port library causes high CPU load
            // Start event listener, which will just sleep and slow down event loop
            serialPort.notifyOnDataAvailable(true);

            logger.info("Connected to serial port '{}'.", serialPortName);
        } catch (NoSuchPortException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Port does not exist");
            return;
        } catch (PortInUseException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR,
                    "Serial Error: Port in use");
            return;
        } catch (UnsupportedCommOperationException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                    "Serial Error: Unsupported operation");
            return;
        }

        try {
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            logger.error("Error getting serial streams", e);
        }

        return;
    }

    private void closeSerialPort() {
        try {
            if (serialPort != null) {
                serialPort.enableReceiveTimeout(1);

                inputStream.close();
                outputStream.flush();
                outputStream.close();

                serialPort.close();

                logger.debug("Closed serial port closed.", serialPort.getName());

                serialPort = null;
                inputStream = null;
                outputStream = null;
            }
        } catch (Exception e) {
            logger.error("Error closing serial port.", e);
        }
    }

    @SuppressWarnings({ "unused", "null" })
    @Override
    public void bluegigaEventReceived(@Nullable BlueGigaResponse event) {
        if (event instanceof BlueGigaScanResponseEvent) {
            BlueGigaScanResponseEvent scanEvent = (BlueGigaScanResponseEvent) event;

            // We use the scan event to add any devices we hear to the devices list
            // The device gets created, and then manages itself for discovery etc.
            BluetoothAddress sender = new BluetoothAddress(scanEvent.getSender());
            BlueGigaBluetoothDevice device;
            if (devices.get(sender) == null) {
                logger.debug("BlueGiga adding new device to adaptor {}: {}", address, sender);
                device = new BlueGigaBluetoothDevice(this, new BluetoothAddress(scanEvent.getSender()),
                        scanEvent.getAddressType());
                devices.put(sender, device);
                deviceDiscovered(device);
            }

            return;
        }

        if (event instanceof BlueGigaConnectionStatusEvent) {
            BlueGigaConnectionStatusEvent connectionEvent = (BlueGigaConnectionStatusEvent) event;

            connections.put(connectionEvent.getConnection(), new BluetoothAddress(connectionEvent.getAddress()));
        }

        if (event instanceof BlueGigaDisconnectedEvent) {
            BlueGigaDisconnectedEvent disconnectedEvent = (BlueGigaDisconnectedEvent) event;

            connections.remove(disconnectedEvent.getConnection());
        }
    }

    @Override
    public void scanStart() {
        // Stop the passive scan
        bgStopProcedure();

        // Start a active scan
        bgStartScanning(true, activeScanInterval, activeScanWindow);

        for (BluetoothDevice device : devices.values()) {
            deviceDiscovered(device);
        }
    }

    @Override
    public void scanStop() {
        // Stop the active scan
        bgStopProcedure();

        // Start a passive scan
        bgStartScanning(false, passiveScanInterval, passiveScanWindow);
    }

    @Override
    public BluetoothAddress getAddress() {
        if (address != null) {
            return address;
        } else {
            throw new IllegalStateException("Adapter has not been initialized yet!");
        }
    }

    @SuppressWarnings({ "null", "unused" })
    @Override
    public BluetoothDevice getDevice(BluetoothAddress address) {
        BluetoothDevice device = devices.get(address);
        if (device == null) {
            // This method always needs to return a device, even if we don't currently know about it.
            device = new BlueGigaBluetoothDevice(this, address, BluetoothAddressType.UNKNOWN);
            devices.put(address, device);
        }
        return device;
    }

    /*
     * The following methods provide adaptor level functions for the BlueGiga interface. Typically these methods
     * are used by the device but are provided in the adapter to allow common knowledge and to support conflict
     * resolution.
     */

    /**
     * Connects to a device.
     * <p>
     * If the device is already connected, or the attempt to connect failed, then we return false. If we have reached
     * the maximum number of connections supported by this dongle, then we return false.
     *
     * @param address the device {@link BluetoothAddress} to connect to
     * @param addressType the {@link BluetoothAddressType} of the device
     * @return true if the connection was started
     */
    public boolean bgConnect(BluetoothAddress address, BluetoothAddressType addressType) {
        // Check the connection to make sure we're not already connected to this device
        if (connections.containsValue(address)) {
            return false;
        }

        if (connections.size() == maxConnections) {
            for (int retries = 3; retries > 0; retries--) {
                if (connections.size() == maxConnections) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                } else {
                    break;
                }
            }
            if (connections.size() == maxConnections) {
                logger.debug("BlueGiga: Attempt to connect to {} but no connections available.", address);
                return false;
            }
        }

        bgSetMode();

        // Connect...
        int connIntervalMin = 60;
        int connIntervalMax = 100;
        int latency = 0;
        int timeout = 100;

        BlueGigaConnectDirectCommand connect = new BlueGigaConnectDirectCommand();
        connect.setAddress(address.toString());
        connect.setAddrType(addressType);
        connect.setConnIntervalMin(connIntervalMin);
        connect.setConnIntervalMax(connIntervalMax);
        connect.setLatency(latency);
        connect.setTimeout(timeout);
        BlueGigaConnectDirectResponse connectResponse = (BlueGigaConnectDirectResponse) bgHandler
                .sendTransaction(connect);
        if (connectResponse.getResult() != BgApiResponse.SUCCESS) {
            return false;
        }

        return true;
    }

    /**
     * Close a connection using {@link BlueGigaDisconnectCommand}
     *
     * @param connectionHandle
     * @return
     */
    public boolean bgDisconnect(int connectionHandle) {
        BlueGigaDisconnectCommand command = new BlueGigaDisconnectCommand();
        command.setConnection(connectionHandle);
        BlueGigaDisconnectResponse response = (BlueGigaDisconnectResponse) bgHandler.sendTransaction(command);

        return response.getResult() == BgApiResponse.SUCCESS;
    }

    /**
     * Device discovered. This simply passes the discover information to the discovery service for processing.
     */
    public void deviceDiscovered(BluetoothDevice device) {
        for (BluetoothDiscoveryListener listener : discoveryListeners) {
            listener.deviceDiscovered(device);
        }
    }

    /**
     * Start a read of all primary services using {@link BlueGigaReadByGroupTypeCommand}
     *
     * @param connectionHandle
     * @return true if successful
     */
    public boolean bgFindPrimaryServices(int connectionHandle) {
        logger.debug("BlueGiga FindPrimary: connection {}", connectionHandle);
        BlueGigaReadByGroupTypeCommand command = new BlueGigaReadByGroupTypeCommand();
        command.setConnection(connectionHandle);
        command.setStart(1);
        command.setEnd(65535);
        command.setUuid(UUID.fromString("00002800-0000-0000-0000-000000000000"));
        BlueGigaResponse response = bgHandler.sendTransaction(command);
        return (response instanceof BlueGigaReadByGroupTypeResponse
                && ((BlueGigaReadByGroupTypeResponse) response).getResult() == BgApiResponse.SUCCESS);
    }

    /**
     * Start a read of all characteristics using {@link BlueGigaFindInformationCommand}
     *
     * @param connectionHandle
     * @return true if successful
     */
    public boolean bgFindCharacteristics(int connectionHandle) {
        logger.debug("BlueGiga Find: connection {}", connectionHandle);
        BlueGigaFindInformationCommand command = new BlueGigaFindInformationCommand();
        command.setConnection(connectionHandle);
        command.setStart(1);
        command.setEnd(65535);
        BlueGigaFindInformationResponse response = (BlueGigaFindInformationResponse) bgHandler.sendTransaction(command);

        return response.getResult() == BgApiResponse.SUCCESS;
    }

    /**
     * Read a characteristic using {@link BlueGigaReadByHandleCommand}
     *
     * @param connectionHandle
     * @param handle
     * @return true if successful
     */
    public boolean bgReadCharacteristic(int connectionHandle, int handle) {
        logger.debug("BlueGiga Read: connection {}, handle {}", connectionHandle, handle);
        BlueGigaReadByHandleCommand command = new BlueGigaReadByHandleCommand();
        command.setConnection(connectionHandle);
        command.setChrHandle(handle);
        BlueGigaReadByHandleResponse response = (BlueGigaReadByHandleResponse) bgHandler.sendTransaction(command);

        return response.getResult() == BgApiResponse.SUCCESS;
    }

    /**
     * Write a characteristic using {@link BlueGigaAttributeWriteCommand}
     *
     * @param connectionHandle
     * @param handle
     * @param value
     * @return true if successful
     */
    public boolean bgWriteCharacteristic(int connectionHandle, int handle, int[] value) {
        logger.debug("BlueGiga Write: connection {}, handle {}", connectionHandle, handle);
        BlueGigaAttributeWriteCommand command = new BlueGigaAttributeWriteCommand();
        command.setConnection(connectionHandle);
        command.setAttHandle(handle);
        command.setData(value);
        BlueGigaAttributeWriteResponse response = (BlueGigaAttributeWriteResponse) bgHandler.sendTransaction(command);

        return response.getResult() == BgApiResponse.SUCCESS;
    }

    /*
     * The following methods are private methods for handling the BlueGiga protocol
     */
    private boolean bgStopProcedure() {
        BlueGigaCommand command = new BlueGigaEndProcedureCommand();
        BlueGigaEndProcedureResponse response = (BlueGigaEndProcedureResponse) bgHandler.sendTransaction(command);

        return response.getResult() == BgApiResponse.SUCCESS;
    }

    private boolean bgSetMode() {
        BlueGigaSetModeCommand command = new BlueGigaSetModeCommand();
        command.setConnect(GapConnectableMode.GAP_NON_CONNECTABLE);
        command.setDiscover(GapDiscoverableMode.GAP_NON_DISCOVERABLE);
        BlueGigaSetModeResponse response = (BlueGigaSetModeResponse) bgHandler.sendTransaction(command);

        return response.getResult() == BgApiResponse.SUCCESS;
    }

    /**
     * Starts scanning on the dongle
     *
     * @param active true for active scanning
     */
    private void bgStartScanning(boolean active, int interval, int window) {
        BlueGigaSetScanParametersCommand scanCommand = new BlueGigaSetScanParametersCommand();
        scanCommand.setActiveScanning(active);
        scanCommand.setScanInterval(interval);
        scanCommand.setScanWindow(window);
        bgHandler.sendTransaction(scanCommand);

        BlueGigaDiscoverCommand discoverCommand = new BlueGigaDiscoverCommand();
        discoverCommand.setMode(GapDiscoverMode.GAP_DISCOVER_OBSERVATION);
        bgHandler.sendTransaction(discoverCommand);
    }

    /**
     * Add an event listener for the BlueGiga events
     *
     * @param listener the {@link BlueGigaEventListener} to add
     */
    public void addEventListener(BlueGigaEventListener listener) {
        bgHandler.addEventListener(listener);
    }

    /**
     * Remove an event listener for the BlueGiga events
     *
     * @param listener the {@link BlueGigaEventListener} to remove
     */
    public void removeEventListener(BlueGigaEventListener listener) {
        bgHandler.removeEventListener(listener);
    }

    @Override
    public void addDiscoveryListener(@NonNull BluetoothDiscoveryListener listener) {
        discoveryListeners.add(listener);
    }

    @Override
    public void removeDiscoveryListener(@Nullable BluetoothDiscoveryListener listener) {
        discoveryListeners.remove(listener);
    }

}
