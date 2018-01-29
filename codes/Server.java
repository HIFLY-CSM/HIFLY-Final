package drone;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server implements Runnable{
	private ServerSocket serverSocket;
	private Socket socket;
	private final int SERVER_PORT = 10123;
	private final int UDP_PORT = SERVER_PORT+1;
	public Server() {
		init();
		new Thread(this).start();//Server Thread Start!
		System.out.println("Server Start!");
	}
	public void init(){
		try {			   
			 	serverSocket = new ServerSocket(SERVER_PORT);
			 	//make ServerSocket;
		} catch (IOException e) {
			Debugger.printMessage(e);
		}				
	}
	public void run(){
		
		while(true){
			try {
				System.out.println("Listen!");			
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress() + "  " + socket.getLocalAddress() + " hello!");
				StreamingVideo streamingVideo = new StreamingVideo(socket, UDP_PORT);
				streamingVideo.start();// FFMPEG Process Start
			} catch (Exception e) {
				Debugger.printMessage(e);
			}
		}
	}

}
