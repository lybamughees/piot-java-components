package programmingtheiot.gda.connection.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.network.interceptors.MessageTracer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.gda.connection.handlers.GenericCoapResourceHandler;

public class GetActuatorCommandResourceHandler extends CoapResource
        implements IActuatorDataListener {
    // static

    // logging infrastructure - should already be defined, although you'll need
    // to update the class name as shown below
    private static final Logger _Logger = Logger.getLogger(GetActuatorCommandResourceHandler.class.getName());

    // params

    private ActuatorData actuatorData = null;

    public GetActuatorCommandResourceHandler(String resourceName) {
        super(resourceName);

        // set the resource to be observable
        super.setObservable(true);
    }

    // ... add constructor and method implementations here ...

    public boolean onActuatorDataUpdate(ActuatorData data) {
        if (data != null && this.actuatorData != null) {
            this.actuatorData.updateData(data);

            // notify all connected clients
            super.changed();

            _Logger.fine("Actuator data updated for URI: " + super.getURI() + ": Data value = "
                    + this.actuatorData.getValue());

            return true;
        }

        return false;
    }

    @Override
    public void handleGET(CoapExchange context) {
        // Log that the GET request was received
        _Logger.info("GET request received for resource: " + super.getName());

        // Accept the request
        context.accept();

        // TODO: Convert the locally stored ActuatorData to JSON using DataUtil
        String jsonData = DataUtil.getInstance().actuatorDataToJson(this.actuatorData);

        // Set the content type as JSON
        context.respond(ResponseCode.CONTENT, jsonData, MediaTypeRegistry.APPLICATION_JSON);
    }

}
