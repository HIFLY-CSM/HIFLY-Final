package drone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import java.net.Socket;

public class StreamingVideo extends Thread implements EndService{

	private MessageSocketThread messageSocketThread;
	private ExhaustInputStreamThread exhaustInputStreamThread;
	
	private Process ffmpegProcess;
	private final String ffmpegPath = "ffmpeg";
	private final String protocol = "udp://@";
	private final String FFMPEG_STREAMING_SERVER = "http://192.168.0.9";// ffmpegProcess

	private final int FFMPEG_STREAMING_SERVER_PORT = 12390;
	private final String FFMPEG_STREAMING_SERVER_NAME = "feed1";
	private String androidAddress;
	private int androidPort;
	
	public StreamingVideo(Socket socket, int port) {
		this.androidAddress = socket.getInetAddress().getHostAddress(); 
		this.androidPort = port;
		try {
			messageSocketThread = new MessageSocketThread(socket);
			messageSocketThread.start();
			System.out.println("wait1!");
			messageSocketThread.sendMsg("port:" + androidPort);// send to Android about UDP Port
			System.out.println("write!");
			EndServiceObject.putThread(this);
			System.out.println(androidAddress + " " + androidPort + " is come!");
		

		} catch (Exception e) {
			Debugger.printMessage(e);
		}
	}

	@Override
	public void run() {
		try {
			List<String> commands = new ArrayList<String>();
			commands.add(ffmpegPath);
			commands.add("-i");
			commands.add(protocol + androidAddress + ":" + androidPort);
			commands.add("-threads");
			commands.add("8");
			commands.add("-cpu-used");
			commands.add("5");
			commands.add("-deadline");
			commands.add("realtime");
			commands.add("-framerate");
			commands.add("24");
			commands.add("-preset");
			commands.add("ultrafast");
			commands.add("-an");
			commands.add(FFMPEG_STREAMING_SERVER + ":" + FFMPEG_STREAMING_SERVER_PORT + "/"
					+ FFMPEG_STREAMING_SERVER_NAME + ".ffm");

			for (int i = 0; i < commands.size(); i++) {
				System.out.println(commands.get(i) + " ");
			}
			System.out.println();
			ProcessBuilder processBuilder = new ProcessBuilder(commands);

			processBuilder.redirectErrorStream(true);// error to print(stderr > stdout)

			ffmpegProcess = processBuilder.start();
			exhaustInputStreamThread = new ExhaustInputStreamThread(ffmpegProcess.getInputStream());// exhaustInputStream about FFMPEG data
			exhaustInputStreamThread.start();
			ffmpegStreamingCheck();// Send I-Frame to FFSERVER by UDP --> Maybe Change
			System.out.println("ffmpegStreamingCheck() Start!!");
		} catch (Exception e) {
			System.out.println("ffmpegStreamingStart");
			endService();
			Debugger.printMessage(e);
			return;

		}
	}

	public void ffmpegStreamingCheck() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				while (true) {
					if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
						try {
							sleep(1000);
							System.out.println("send!");
							messageSocketThread.sendMsg("FFMPEG_START!");

						} catch (Exception e) {
							System.out.println("ffmpegStreamingCheck");
							endService();
							e.printStackTrace();
							return;
						}
						return;
					}
					long elapsedTime = System.currentTimeMillis() - startTime;
					// System.out.println(elapsedTime + "걸림");
					if (elapsedTime > 10000) {
						System.out.println("10000 over");
						endService();
						return;
					}

				}

			}
		}).start();
	}	
	public void endService() {
		System.out.println("end");
		if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
			System.out.println("ffmpeg destroy!");
			ffmpegProcess.destroyForcibly();
		}
		if(this != null && this.isAlive())
			this.interrupt();
/*		if(exhaustInputStreamThread != null && exhaustInputStreamThread.isAlive())
			exhaustInputStreamThread.endService();
		if(messageSocketThread != null && messageSocketThread.isAlive())
			messageSocketThread.endService();*/
/*		if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
			System.out.println("ffmpeg destroy!");
			ffmpegProcess.destroyForcibly();
		}
		if (pollingThread != null && pollingThread.isAlive()) {
			pollingThread.interrupt();
		}
		if (dataThread != null && dataThread.isAlive()) {
			dataThread.interrupt();
		}*/
		/*if (socket != null && socket.isConnected()) {
			try {
				System.out.println("socket disconnected!");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		System.out.println(new Date());
	}
}