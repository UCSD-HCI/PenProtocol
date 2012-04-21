package edu.ucsd.hci.interactivespace.annotopen;

import java.io.IOException;
import java.lang.management.MemoryType;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;

import com.google.protobuf.CodedOutputStream;

import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.Message;
import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.MessageType;

public class BroadcastServer {
	
	ServerSocket serverSocket;
	Thread serverThread;
	
	Socket clientSocket;	//TODO: currently we only support single client
	Thread clientThread;
	CodedOutputStream codedOutputStream;
	
	SynchronousQueue<Message> messageQueue;
	
	volatile boolean isStopRequested;
	
	public BroadcastServer(String host, int port) throws IOException
	{
		messageQueue = new SynchronousQueue<Message>(true);
		
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(host, port));
		System.out.println("AnnotoPen broadcast server listening on " + host + ":" + port);
		
		serverThread = new Thread(serverThreadWorker);
		serverThread.start();
	}
	
	public void issueCustomString(String str) {		
		Message.Builder b = Message.newBuilder();
		b.setType(MessageType.CustomString)
		 .setCustomString(str);
		
		Message msg = b.build();
		messageQueue.add(msg);		
	}
	
	private void disconnectClient() {
		if (isClientAlive()) {
			try {
				clientSocket.close();
			} catch (IOException e) {
			}
			
			System.out.println("Client " + clientSocket.getInetAddress().toString() + " disconnected. ");
			clientSocket = null;
		}
	}
	
	private boolean isClientAlive() {
		return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
	}

	private Runnable serverThreadWorker = new Runnable() {
		@Override
		public void run() {
			while(!isStopRequested) {
				try {
					Socket newClientSocket = serverSocket.accept();
					
					if (isClientAlive()) {
						newClientSocket.close();
						System.out.println("Connection from " + newClientSocket.getInetAddress().toString() + " refused. ");
					}
					
					clientSocket = newClientSocket;
					codedOutputStream = CodedOutputStream.newInstance(clientSocket.getOutputStream());
					clientThread = new Thread(clientThreadWorker);
					clientThread.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			disconnectClient();
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
		}
	};
	
	private Runnable clientThreadWorker = new Runnable() {	
		@Override
		public void run() {
			while (isClientAlive()) {
				Message msg;
				
				try {
					msg = messageQueue.take();
				} catch (InterruptedException e1) {
					disconnectClient();
					break;
				}
				
				try {
					codedOutputStream.writeUInt32NoTag(msg.getSerializedSize());
					msg.writeTo(codedOutputStream);
					
					System.out.println(msg.toString());
				} catch (IOException e) {
					if (!isClientAlive()) {
						break;
					} else {
						e.printStackTrace();
					}
				}	
			}
			
			if (clientSocket != null) {
				System.out.println("Client " + clientSocket.getInetAddress().toString() + " disconnected. ");
			}
		}
	};
}
