package drone;

import java.util.Vector;

public class EndServiceObject{
/*	private static ExhaustInputStreamThread exhaustInputStreamThread;
	private static MessageSocketThread messageSocketThread;
	private static PollingThread pollingThread;*/
	private static Vector<EndService> threadVec = new Vector<EndService>();

/*	public static void setExhaustInputStreamThread(ExhaustInputStreamThread exhaustInputStreamThread) {
		EndServiceObject.exhaustInputStreamThread = exhaustInputStreamThread;
	}

	public static void setMessageSocketThread(MessageSocketThread messageSocketThread) {
		EndServiceObject.messageSocketThread = messageSocketThread;
	}

	public static void setPollingThread(PollingThread pollingThread) {
		EndServiceObject.pollingThread = pollingThread;
	}*/
	public static void putThread(EndService endService) {
		threadVec.add(endService);
	}
	public static void endService() {
		
	/*	exhaustInputStreamThread.endService();
		messageSocketThread.endService();
		pollingThread.endService();*/
		for(int i=0; i<threadVec.size(); i++) {
			threadVec.get(i).endService();
		}
	}
}
