package programmingtheiot.gda.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.core.server.resources.ResourceObserver;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.gda.connection.handlers.GenericCoapResourceHandler;
import programmingtheiot.gda.connection.handlers.GetActuatorCommandResourceHandler;
import programmingtheiot.gda.connection.handlers.UpdateSystemPerformanceResourceHandler;
import programmingtheiot.gda.connection.handlers.UpdateTelemetryResourceHandler;

public class CoapServerGateway {
	// static
	private static final Logger _Logger = Logger.getLogger(CoapServerGateway.class.getName());

	// params
	private CoapServer coapServer = null;
	private IDataMessageListener dataMsgListener = null;

	// constructors
	public CoapServerGateway() {
		super();
		this.dataMsgListener = dataMsgListener;
		initServer();
	}

	// public methods
	public void addResource(ResourceNameEnum resourceType, String endName, Resource resource) {
		if (resourceType != null && resource != null) {
			createAndAddResourceChain(resourceType, endName, resource);
		}
	}

	public boolean hasResource(String name) {
		return false;
	}

	public void setDataMessageListener(IDataMessageListener listener) {
		if (listener != null) {
			this.dataMsgListener = listener;
		}
	}

	public boolean startServer() {
		try {
			if (this.coapServer != null) {
				this.coapServer.start();
				for (Endpoint ep : this.coapServer.getEndpoints()) {
					ep.addInterceptor(new MessageTracer());
				}
				return true;
			} else {
				_Logger.warning("CoAP server START failed. Not yet initialized.");
			}
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to start CoAP server.", e);
		}
		return false;
	}

	public boolean stopServer() {
		try {
			if (this.coapServer != null) {
				this.coapServer.stop();
				return true;
			} else {
				_Logger.warning("CoAP server STOP failed. Not yet initialized.");
			}
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to stop CoAP server.", e);
		}
		return false;
	}

	// private methods
	private Resource createResourceChain(ResourceNameEnum resource, String endName, Resource resourceHandler) {
		Resource res = null;
		if (endName != null && !endName.isEmpty()) {
			Resource parent = this.coapServer.getRoot();
			String[] names = endName.split("/");
			for (String name : names) {
				if (parent.getChild(name) == null) {
					parent.add(new GenericCoapResourceHandler(name));
				}
				parent = parent.getChild(name);
			}
			parent.add(resourceHandler);
			res = parent;
		} else {
			res = this.coapServer.getRoot();
			res.add(resourceHandler);
		}
		return res;
	}

	private void initServer(ResourceNameEnum... resources) {
		this.coapServer = new CoapServer();

		initDefaultResources();
	}

	private void initDefaultResources() {
		// initialize pre-defined resources
		GetActuatorCommandResourceHandler getActuatorCmdResourceHandler = new GetActuatorCommandResourceHandler(
				ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE.getResourceType());

		if (this.dataMsgListener != null) {
			this.dataMsgListener.setActuatorDataListener(null, getActuatorCmdResourceHandler);
		}

		addResource(ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, null, getActuatorCmdResourceHandler);

		UpdateTelemetryResourceHandler updateTelemetryResourceHandler = new UpdateTelemetryResourceHandler(
				ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceType());

		updateTelemetryResourceHandler.setDataMessageListener(this.dataMsgListener);

		addResource(
				ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, null, updateTelemetryResourceHandler);

		UpdateSystemPerformanceResourceHandler updateSystemPerformanceResourceHandler = new UpdateSystemPerformanceResourceHandler(
				ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceType());

		updateSystemPerformanceResourceHandler.setDataMessageListener(this.dataMsgListener);

		addResource(
				ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, null, updateSystemPerformanceResourceHandler);
	}

	private Resource createAndAddResourceChain(ResourceNameEnum resourceType, String endName,
			Resource resourceHandler) {
		if (endName != null && !endName.isEmpty()) {
			Resource parent = this.coapServer.getRoot();
			String[] names = endName.split("/");
			for (String name : names) {
				if (parent.getChild(name) == null) {
					parent.add(new GenericCoapResourceHandler(name));
				}
				parent = parent.getChild(name);
			}
			parent.add(resourceHandler);
			return parent;
		} else {
			Resource res = this.coapServer.getRoot();
			res.add(resourceHandler);
			return res;
		}
	}

}
