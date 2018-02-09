package cs455.overlay.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

import cs455.overlay.wireFormats.Event;
import cs455.overlay.wireFormats.EventFactory;

public class TCPReceiver implements Runnable {
	//Wrapper class for the receiving socket for each messaging
	private Socket socket;
	private DataInputStream din;
	private BlockingQueue<Event> events;

	public TCPReceiver(Socket socket, BlockingQueue<Event> events) throws IOException {
		this.socket = socket;
		din = new DataInputStream(socket.getInputStream());
		this.events = events;
	}
	
	public void run() {
		int dataLength;
		while (socket != null) {
			try {
				dataLength = din.readInt();
				byte[] data = new byte[dataLength];
				din.readFully(data, 0, dataLength);
				synchronized(events) {
					events.put(EventFactory.getEvent(data));
				}
			} catch (SocketException se) {
				System.out.println(se.getMessage());
				break;
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage()) ;
				break;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}