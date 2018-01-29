package drone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExhaustInputStreamThread extends Thread implements EndService{
	private Timer timer;
	private InputStream inputStream;
	private BufferedReader reader;
	private boolean isRunning = false;
	private PollingThread pollingThread;
	public ExhaustInputStreamThread(InputStream in) {
		timer = Timer.getInstance();
		this.inputStream = in;
		EndServiceObject.putThread(this);
	}
	@Override
	public void run() {
		String str;
		reader = new BufferedReader(new InputStreamReader(inputStream));
		
		System.out.println("FFMPEG Stream data exhausting");
		try {
			while ((str = reader.readLine()) != null) {
				if (isRunning == false) {
					isRunning = true;
					pollingThread = new PollingThread();
					pollingThread.start();
				}
				System.out.println(str);
				timer.setStartTime(System.currentTimeMillis());
			}
		} catch (IOException e) {			
			Debugger.printMessage(e);
			return;
		}
	}
	public void endService() {
		try {
			if(inputStream != null)
				inputStream.close();
			if(reader != null)
				reader.close();
		} catch (IOException e) {
			Debugger.printMessage(e);
		}		
	}
}
