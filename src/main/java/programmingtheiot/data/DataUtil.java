/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */

package programmingtheiot.data;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.Gson;

/**
 * Shell representation of class for student implementation.
 *
 */
public class DataUtil {
	// static

	private static final DataUtil _Instance = new DataUtil();

	/**
	 * Returns the Singleton instance of this class.
	 * 
	 * @return ConfigUtil
	 */
	public static final DataUtil getInstance() {
		return _Instance;
	}

	// private var's

	// constructors

	/**
	 * Default (private).
	 * 
	 */
	private DataUtil() {
		super();
	}

	// public methods

	public String actuatorDataToJson(ActuatorData data) {
		String jsonData = null;

		if (data != null) {
			Gson gson = new Gson();
			jsonData = gson.toJson(data);
		}

		return jsonData;
	}

	public ActuatorData jsonToActuatorData(String jsonData) {
		ActuatorData data = null;

		if (jsonData != null && jsonData.trim().length() > 0) {
			Gson gson = new Gson();
			data = gson.fromJson(jsonData, ActuatorData.class);
		}

		return data;
	}

	public String systemPerformanceDataToJson(SystemPerformanceData sysPerfData) {
			
		String jsonData = null;

		if (sysPerfData != null) {
			Gson converter = new Gson();
			jsonData = converter.toJson(sysPerfData);
		}

		return jsonData;
	}

	public SystemPerformanceData jsonToSystemPerformanceData(String jsonData) {
		SystemPerformanceData sysPerfData = null;

		if (jsonData != null && jsonData.trim().length() > 0) {
			Gson converter = new Gson();
			sysPerfData = converter.fromJson(jsonData, SystemPerformanceData.class);
		}

		return sysPerfData;
	}

	public String systemStateDataToJson(SystemStateData sysStateData) {
		String jsonData = null;

		if (sysStateData != null) {
			Gson gson = new Gson();
			jsonData = gson.toJson(sysStateData);
		}

		return jsonData;
	}

		public SystemStateData jsonToSystemStateData(String jsonData) {

		SystemStateData sysPerfData = null;

		if (jsonData != null && jsonData.trim().length() > 0) {
			Gson converter = new Gson();
			sysPerfData = converter.fromJson(jsonData, SystemStateData.class);
		}

		return sysPerfData;
	}


	public String sensorDataToJson(SensorData sysStateData) {
		String jsonData = null;

		if (sysStateData != null) {
			Gson gson = new Gson();
			jsonData = gson.toJson(sysStateData);
		}

		return jsonData;
	}

	public SensorData jsonToSensorData(String jsonData) {
		SensorData sysPerfData = null;

		if (jsonData != null && jsonData.trim().length() > 0) {
			Gson converter = new Gson();
			sysPerfData = converter.fromJson(jsonData, SensorData.class);
		}

		return sysPerfData;
	}



	

}
