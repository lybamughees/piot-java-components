/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.gda.app.GatewayDeviceApp;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemMemUtilTask extends BaseSystemUtilTask
{
	// constructors
	private static final Logger _Logger =
			Logger.getLogger(GatewayDeviceApp.class.getName());
	/**
	 * Default.
	 * 
	 */
	
	public SystemMemUtilTask()
	{
		super(ConfigConst.NOT_SET, ConfigConst.DEFAULT_TYPE_ID);
	}
	
	
	// public methods
	
	@Override
	public float getTelemetryValue()
	{
		MemoryUsage memUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		
		_Logger.fine("Mem used: " + (double) memUsage.getUsed() + "; Mem Max: " + (double) memUsage.getMax());
		
		double memUtil = ((double) memUsage.getUsed() / (double) memUsage.getMax()) * 100.0d;
		
		return (float) memUtil;
	}
	
}
