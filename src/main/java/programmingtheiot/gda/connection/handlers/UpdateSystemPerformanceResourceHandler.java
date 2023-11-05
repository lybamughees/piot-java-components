package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SystemPerformanceData;

public class UpdateSystemPerformanceResourceHandler extends CoapResource {

    private static final Logger _Logger =
        Logger.getLogger(UpdateSystemPerformanceResourceHandler.class.getName());

    public UpdateSystemPerformanceResourceHandler(String resourceName) {
        super(resourceName);
    }

    private IDataMessageListener dataMsgListener = null;

    public void setDataMessageListener(IDataMessageListener listener) {
        if (listener != null) {
            this.dataMsgListener = listener;
        }
    }

    @Override
    public void handlePUT(CoapExchange context) {
        // Initialize the response code with NOT_ACCEPTABLE by default
        ResponseCode code = ResponseCode.NOT_ACCEPTABLE;
    
        // Accept the request, indicating it is being handled
        context.accept();
    
        if (this.dataMsgListener != null) {
            try {
                // Extract JSON data from the request payload
                String jsonData = new String(context.getRequestPayload());
    
                // Convert JSON data to a SystemPerformanceData instance
                SystemPerformanceData sysPerfData = DataUtil.getInstance().jsonToSystemPerformanceData(jsonData);
    
                // TODO: You can perform additional checks here, such as checking MID or caching the previous update if needed.
    
                // Delegate the data handling to this.dataMsgListener
                this.dataMsgListener.handleSystemPerformanceMessage(
                    ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, sysPerfData);
    
                // Set the response code to CHANGED to indicate a successful update
                code = ResponseCode.CHANGED;
            } catch (Exception e) {
                // Handle exceptions, log an error message, and set the response code to BAD_REQUEST
                _Logger.warning("Failed to handle PUT request. Message: " + e.getMessage());
                code = ResponseCode.BAD_REQUEST;
            }
        } else {
            // Log an info message if there is no callback listener, and set the response code to CONTINUE
            _Logger.info("No callback listener for request. Ignoring PUT.");
            code = ResponseCode.CONTINUE;
        }
    
        // Prepare a response message
        String msg = "Update system perf data request handled: " + super.getName();
    
        // Respond to the request with the appropriate response code and message
        context.respond(code, msg);
    }
}
