package drone;

public class Timer {
	private static long ffmpegstartTime = 0;
	private static long ffmpegfinishTime = 0;
	private static Timer timer = null;

	private Timer() {

	}

	public static Timer getInstance() {
		if (timer == null) {
			timer = new Timer();
		}
		return timer;

	}

	public long getStartTime() {
		return ffmpegstartTime;
	}

	public void setStartTime(long ffmpegstartTime) {
		Timer.ffmpegstartTime = ffmpegstartTime;
	}

	public long getFinishTime() {
		return ffmpegstartTime;
	}

	public void setFinishTime(long ffmpegfinishTime) {
		Timer.ffmpegfinishTime = ffmpegfinishTime;
	}

	public long getServiceTime() {
		long time = (long) ((ffmpegfinishTime - ffmpegstartTime) / 1000.0);
		return time;
	}
}
