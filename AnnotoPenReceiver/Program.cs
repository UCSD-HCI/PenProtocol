using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using InteractiveSpace;
using Google.ProtocolBuffers;

namespace AnnotoPen
{
    class Program
    {
        static void Main(string[] args)
        {
            AnnotoPenReceiver receiver = AnnotoPenReceiver.NewInstance("128.54.43.76", 65432);
            

            /*TcpClient client = new TcpClient();
            client.Connect("128.54.5.95", 65432);

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
            }*/
        }
    }
} 
