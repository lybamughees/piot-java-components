package programmingtheiot.gda.connection;

import programmingtheiot.common.DefaultDataMessageListener;

public class runner {
	
	public static void main(String[] args)
	{
		CoapServerGateway csg = new CoapServerGateway(); 
		csg.setDataMessageListener(new DefaultDataMessageListener());	
		csg.startServer();
	}

}
