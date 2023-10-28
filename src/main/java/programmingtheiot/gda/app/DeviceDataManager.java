/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.app;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

import programmingtheiot.gda.connection.CloudClientConnector;
import programmingtheiot.gda.connection.CoapServerGateway;
import programmingtheiot.gda.connection.IPersistenceClient;
import programmingtheiot.gda.connection.IPubSubClient;
import programmingtheiot.gda.connection.IRequestResponseClient;
import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.gda.connection.RedisPersistenceAdapter;
import programmingtheiot.gda.connection.SmtpClientConnector;
import programmingtheiot.gda.system.SystemPerformanceManager;

/**
 * Shell representation of class for student implementation.
 *
 */
public class DeviceDataManager implements IDataMessageListener
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(DeviceDataManager.class.getName());
	
	// private var's
	
	private boolean enableMqttClient = true;
	private boolean enableCoapServer = false;
	private boolean enableCloudClient = false;
	private boolean enableSmtpClient = false;
	private boolean enablePersistenceClient = false;
	private ScheduledExecutorService schedExecSvc = null;
	
	private IActuatorDataListener actuatorDataListener = null;
	private IPubSubClient mqttClient = null;
	private IPubSubClient cloudClient = null;
	private IPersistenceClient persistenceClient = null;
	private IRequestResponseClient smtpClient = null;
	private CoapServerGateway coapServer = null;
	private int pollRate = ConfigConst.DEFAULT_POLL_CYCLES;


	private boolean isStarted = false;

private boolean enableSystemPerf = false;

private SystemPerformanceManager sysPerfMgr = null;
private Runnable taskRunner = null;
	
	// constructors
	
	public DeviceDataManager()
{
	super();
	
	ConfigUtil configUtil = ConfigUtil.getInstance();
	
	this.enableMqttClient =
		configUtil.getBoolean(
			ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
	
	this.enableCoapServer =
		configUtil.getBoolean(
			ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_COAP_SERVER_KEY);
	
	this.enableCloudClient =
		configUtil.getBoolean(
			ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_CLOUD_CLIENT_KEY);
	
	this.enablePersistenceClient =
		configUtil.getBoolean(
			ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_PERSISTENCE_CLIENT_KEY);
	
	initManager();
}
	
	public DeviceDataManager(
		boolean enableMqttClient,
		boolean enableCoapClient,
		boolean enableCloudClient,
		boolean enableSmtpClient,
		boolean enablePersistenceClient)
	{
		super();
		
		initConnections();
	}
	
	
	// public methods
	
	@Override
	public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data) {
        if (data != null) {
            _Logger.info("Handling actuator response: " + data.getName());
            
            // Log a debug message for handleIncomingDataAnalysis
            _Logger.fine("handleIncomingDataAnalysis called for actuator response.");
            
            // For now, this will be empty. You may implement this later.
            // this.handleIncomingDataAnalysis(resourceName, data);
            
            if (data.hasError()) {
                _Logger.warning("Error flag set for ActuatorData instance.");
            }
            
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg) {
        if (msg != null) {
            _Logger.info("Handling incoming generic message: " + msg);
            
            // Log a debug message for handleIncomingDataAnalysis
            _Logger.fine("handleIncomingDataAnalysis called for generic message.");
            
            // For now, this will be empty. You may implement this later.
            // this.handleIncomingDataAnalysis(resourceName, data);
            
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data) {
        if (data != null) {
            _Logger.info("Handling sensor message: " + data.getName());
            
            // Log a debug message for handleIncomingDataAnalysis
            _Logger.fine("handleIncomingDataAnalysis called for sensor message.");
            
            // For now, this will be empty. You may implement this later.
            // this.handleIncomingDataAnalysis(resourceName, data);
            
            if (data.hasError()) {
                _Logger.warning("Error flag set for SensorData instance.");
            }
            
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data) {
        if (data != null) {
            _Logger.info("Handling system performance message: " + data.getName());
            
            // Log a debug message for handleIncomingDataAnalysis
            _Logger.fine("handleIncomingDataAnalysis called for system performance message.");
            
            // For now, this will be empty. You may implement this later.
            // this.handleIncomingDataAnalysis(resourceName, data);
            
            if (data.hasError()) {
                _Logger.warning("Error flag set for SystemPerformanceData instance.");
            }
            
            return true;
        } else {
            return false;
        }
    }
	public void setActuatorDataListener(String name, IActuatorDataListener listener)
	{
	}
	
	public void startManager() {
		if (this.sysPerfMgr != null) {
			this.sysPerfMgr.startManager();
		}
	
		if (this.mqttClient != null) {
			if (this.mqttClient.connectClient()) {
				_Logger.info("Successfully connected MQTT client to broker.");
	
				// Add necessary subscriptions
	
				// TODO: read this from the configuration file
				int qos = ConfigConst.DEFAULT_QOS;
	
				// TODO: check the return value for each and take appropriate action
				this.mqttClient.subscribeToTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, qos);
				this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, qos);
				this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, qos);
				this.mqttClient.subscribeToTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, qos);
			} else {
				_Logger.severe("Failed to connect MQTT client to broker.");
	
				// TODO: take appropriate action
			}
		}
	}
	

	public void stopManager()
{
	if (this.sysPerfMgr != null) {
		this.sysPerfMgr.stopManager();
	}
	
	if (this.mqttClient != null) {
		// add necessary un-subscribes
		
		// TODO: check the return value for each and take appropriate action
		this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE);
		this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE);
		this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE);
		this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE);
		
		if (this.mqttClient.disconnectClient()) {
			_Logger.info("Successfully disconnected MQTT client from broker.");
		} else {
			_Logger.severe("Failed to disconnect MQTT client from broker.");
			
			// TODO: take appropriate action
		}
	}
}
	private void initManager() {
        ConfigUtil configUtil = ConfigUtil.getInstance();

        this.enableSystemPerf = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SYSTEM_PERF_KEY);

        if (this.enableSystemPerf) {
            this.sysPerfMgr = new SystemPerformanceManager();
            this.sysPerfMgr.setDataMessageListener(this);
        }

        if (this.enableMqttClient) {
            // Initialize and create a new instance of MqttClientConnector
            this.mqttClient = new MqttClientConnector();

            // TODO: Set other configurations and connect to MQTT broker if needed
            // Example: this.mqttClient.setDataMessageListener(this);
        }
	}
	
	// private methods
	
	/**
	 * Initializes the enabled connections. This will NOT start them, but only create the
	 * instances that will be used in the {@link #startManager() and #stopManager()) methods.
	 * 
	 */

	 // Private method for handling incoming data analysis (empty for now)
	 private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, Object data) {
        // Log a debug message indicating this method has been called
        _Logger.fine("handleIncomingDataAnalysis called for resource: " + resourceName.toString());
        
        // For now, this will be empty. You may implement this later.
        // This method will eventually publish back to the CDA using MQTT or CoAP (or attempt both).
    }

    // Private method for handling upstream transmission (empty for now)
    private boolean handleUpstreamTransmission(ResourceNameEnum resourceName, String jsonData, int qos) {
        // Log a message indicating this method has been called
        _Logger.fine("handleUpstreamTransmission called for resource: " + resourceName.toString());
        
        // For now, this will be empty. You will implement this later.
        // This method will eventually publish to the cloud service.
        // NOTE: For the qos value, we'll discuss this more in Part III - Connectivity. You can ignore it for now.
        return true;
    }
	private void initConnections()
	{
	}

	
}
