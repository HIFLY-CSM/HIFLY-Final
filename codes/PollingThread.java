package drone;


public class PollingThread extends Thread implements EndService {	
	private Timer timer;
	public PollingThread() {
		timer = Timer.getInstance();
		EndServiceObject.putThread(this);
	}
	@Override 
	public void run() {
		while (true) {
			try {
				sleep(1000);
				timer.setFinishTime(System.currentTimeMillis());
				long time = timer.getServiceTime();
				System.out.println(time);
				if (time > 15 && timer.getFinishTime() > 0) {
					System.out.println(timer);
					EndServiceObject.endService();
					System.out.println("exit2");
					return;
				}

			} catch (Exception e) {
				System.out.println("time is out");
				Debugger.printMessage(e);
				return;
			}
		}
	}
	public void endService() {
		if(this != null && this.isAlive())
			this.interrupt();
	}

}
