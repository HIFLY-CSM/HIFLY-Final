package drone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class MessageSocketThread extends Thread implements EndService {
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private Socket socket;

	public MessageSocketThread(Socket socket) {
		try {
			this.socket = socket;
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			EndServiceObject.putThread(this);
		} catch (IOException e) {
			Debugger.printMessage(e);
		}
	}

	public void sendMsg(String msg) {
		try {
			dataOutputStream.writeUTF(msg);
		} catch (IOException e) {
			Debugger.printMessage(e);
		}
	}

	@Override
	public void run() {
		while (socket.isConnected()) {
			String msg = null;
			try {
				msg = dataInputStream.readUTF();
				System.out.println(msg + "받음～～～～～～～～");
				if (msg.equals("FinishStreaming")) {
					EndServiceObject.endService();
				}
			} catch (IOException e) {
				Debugger.printMessage(e);
				return;
			}
		}
	}

	public void endService() {
		if(socket!=null && socket.isConnected()) {
			sendMsg("RoomClose");
		}
		try {
			dataInputStream.close();
		} catch (IOException e1) {
			Debugger.printMessage(e1);
		}
		if(this != null && this.isAlive())
			this.interrupt();
		if (socket != null && socket.isConnected()) {
			try {
				System.out.println("socket disconnected!");
				socket.close();
			} catch (IOException e) {
				Debugger.printMessage(e);
			}
		}
	}

}
