using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using InteractiveSpace;
using Google.ProtocolBuffers;

namespace AnnotoPenReceiver
{
    class Program
    {
        static void Main(string[] args)
        {
            TcpClient client = new TcpClient();
            client.Connect("localhost", 65432);

            NetworkStream stream = client.GetStream();
            CodedInputStream codedStream = CodedInputStream.CreateInstance(stream);

            while (client.Connected)
            {
                int msgSize = 0;
                codedStream.ReadInt32(ref msgSize);

                int oldLimit = codedStream.PushLimit(msgSize);
                Message msg = Message.ParseFrom(codedStream);
                codedStream.PopLimit(oldLimit);

                Console.WriteLine(msg.ToString());
            }
        }
    }
} 
