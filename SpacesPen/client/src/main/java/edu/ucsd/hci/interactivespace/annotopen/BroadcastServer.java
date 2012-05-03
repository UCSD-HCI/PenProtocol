package edu.ucsd.hci.interactivespace.annotopen;

import java.io.IOException;
import java.lang.management.MemoryType;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;

import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.Message;
import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.MessageType;
import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.MotionCode;
import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.MotionMessage;
import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.StatusCode;
import edu.ucsd.hci.interactivespace.annotopen.protobuf.AnnotoPenProtocol.StatusMessage;

public class BroadcastServer {
	
	ServerSocket serverSocket;
	Thread serverThread;
	
	Socket clientSocket;	//TODO: currently we only support single client
	Thread clientThread;
	CodedOutputStream codedOutputStream;
	
	LinkedBlockingQueue<Message> messageQueue;
	
	volatile boolean isStopRequested;
	
	public BroadcastServer(String host, int port) throws IOException
	{
		messageQueue = new LinkedBlockingQueue<Message>();
		
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
	
	public void issueCustomBytes(byte[] bytes) {
		Message.Builder b = Message.newBuilder();
		b.setType(MessageType.CustomBytes)
		 .setCustomBytes(ByteString.copyFrom(bytes));
		
		messageQueue.add(b.build());
	}
	
	public void issueStatus(StatusCode status, String message) {
		StatusMessage.Builder statusB = StatusMessage.newBuilder();
		statusB.setStatus(status)
			   .setMessage(message);
		
		Message.Builder b = Message.newBuilder();
		b.setType(MessageType.Status)
		 .setStatus(statusB);
		
		messageQueue.add(b.build());
	}
	
	public void issueMotion(MotionCode motion, float x, float y, int timestamp, int force, String document, int page) {
		MotionMessage.Builder motionB = MotionMessage.newBuilder();
		motionB.setMotion(motion)
		       .setX(x)
		       .setY(y)
		       .setTimestamp(timestamp)
		       .setForce(force)
		       .setDocument(document)
		       .setPage(page);
		
		Message.Builder b = Message.newBuilder();
		b.setType(MessageType.Motion)
		 .setMotion(motionB);
		
		messageQueue.add(b.build());
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
	
	public void join() {
		try {
			serverThread.join();
		} catch (InterruptedException e) {
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
					System.out.println("Connected to " + clientSocket.getInetAddress().toString());
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
				
				msg = messageQueue.poll();
				if (msg == null) {
					Thread.yield();
					continue;
				}
				
				try {
					codedOutputStream.writeUInt32NoTag(msg.getSerializedSize());
					msg.writeTo(codedOutputStream);
					
					codedOutputStream.flush();
					
					System.out.println("Send " + msg.getSerializedSize() + " bytes.");
					System.out.println(msg.toString());
					System.out.println();
				} catch (IOException e) {
					/*if (!isClientAlive()) {
						break;
					} else {
						e.printStackTrace();
					}*/
					break;
				}	
			}
			
			if (clientSocket != null) {
				System.out.println("Client " + clientSocket.getInetAddress().toString() + " disconnected. ");
			}
		}
	};
}
