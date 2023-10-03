package programmingtheiot.gda.system;

import programmingtheiot.common.ConfigConst;
import java.io.File;

public class SystemDiskUtilTask extends BaseSystemUtilTask {
    // Add any necessary variables or constructors here

    // Constructors

    public SystemDiskUtilTask() {
        super(ConfigConst.NOT_SET, ConfigConst.DEFAULT_TYPE_ID);
    }

    @Override
    public float getTelemetryValue() {
        // Implement logic to calculate disk utilization
        // You can use the java.io.File class to get disk usage information
        // Example:
        File root = new File("/");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        double diskUtil = ((totalSpace - freeSpace) / (double) totalSpace) * 100.0d;

        return (float) diskUtil;
    }
}
