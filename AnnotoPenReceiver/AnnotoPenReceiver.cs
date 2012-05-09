using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using Google.ProtocolBuffers;
using InteractiveSpace;
using System.Threading;
using System.Diagnostics;

namespace AnnotoPen
{
    public class AnnotoPenReceiver
    {
        private string host;
        private int port;

        private TcpClient client;
        private NetworkStream clientStream;
        private CodedInputStream codedStream;
        private Thread clientThread;
        private volatile bool isCloseRequested;

        public event EventHandler<AnnotoPenMotionEventArgs> PenDown;
        public event EventHandler<AnnotoPenMotionEventArgs> PenUp;
        public event EventHandler<AnnotoPenMotionEventArgs> PenMove;

        public static AnnotoPenReceiver NewInstance()
        {
            return NewInstance("128.54.43.76", 65432);
        }

        /// <summary>
        /// Return null if connecting failed
        /// </summary>
        /// <param name="host"></param>
        /// <param name="port"></param>
        /// <returns></returns>
        public static AnnotoPenReceiver NewInstance(string host, int port)
        {
            AnnotoPenReceiver res = new AnnotoPenReceiver(host, port);
            if (res.connect())
            {
                return res;
            }
            else
            {
                return null;
            }
        }

        private AnnotoPenReceiver(string host, int port)
        {
            this.host = host;
            this.port = port;
        }

        private bool connect()
        {
            try
            {
                client = new TcpClient();
                client.Connect(host, port);

                clientStream = client.GetStream();
                codedStream = CodedInputStream.CreateInstance(clientStream);

                Trace.WriteLine("Connected to AnntoPen server. ");
            }
            catch (SocketException)
            {
                Trace.WriteLine("Cannot connect to AnntoPen server. ");
                return false;
            }

            clientThread = new Thread(clientThreadWorker);
            clientThread.Start();
            return true;
        }

        public void Close()
        {
            isCloseRequested = true;
        }

        private void clientThreadWorker()
        {
            while (client != null && client.Connected)
            {
                if (isCloseRequested)
                {
                    client.Close();
                }

                int msgSize = 0;

                try
                {
                    codedStream.ReadInt32(ref msgSize);
                }
                catch (Exception)   //SocketException, InvalidProtocolBufferException
                {
                    Trace.WriteLine("AnnotoPen connection lost");
                    break;
                }

                int oldLimit = codedStream.PushLimit(msgSize);
                Message msg = Message.ParseFrom(codedStream);
                codedStream.PopLimit(oldLimit);

                if (msg.Type == MessageType.Motion)
                {
                    AnnotoPenMotionEventArgs e = new AnnotoPenMotionEventArgs(msg.Motion.X, msg.Motion.Y, msg.Motion.Timestamp, msg.Motion.Force, msg.Motion.Document, msg.Motion.Page);
                    if (msg.Motion.Motion == MotionCode.PenDown && PenDown != null)
                    {
                        PenDown(this, e);
                    }
                    else if (msg.Motion.Motion == MotionCode.PenUp && PenUp != null)
                    {
                        PenUp(this, e);
                    }
                    else if (msg.Motion.Motion == MotionCode.PenMove && PenMove != null)
                    {
                        PenMove(this, e);
                    }
                }

                //Console.WriteLine(msg.ToString());
            }
        }


    }
}
