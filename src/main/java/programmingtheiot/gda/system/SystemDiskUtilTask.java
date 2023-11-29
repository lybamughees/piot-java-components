package programmingtheiot.gda.system;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import java.util.logging.Logger;
import programmingtheiot.common.ConfigConst;

public class SystemDiskUtilTask extends BaseSystemUtilTask {
	
	public SystemDiskUtilTask()
	{
		super(ConfigConst.NOT_SET, ConfigConst.DEFAULT_TYPE_ID);
	}
	
	
	// public methods
	
	@Override
	public float getTelemetryValue(){
		
		File root = new File("/");
	
		float space = ((float)root.getTotalSpace() /1073741824) - ((float)root.getUsableSpace()/1073741824);
	
		return space;
	}
}
