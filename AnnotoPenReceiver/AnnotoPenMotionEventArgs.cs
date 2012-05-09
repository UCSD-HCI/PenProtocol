using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AnnotoPen
{
    public class AnnotoPenMotionEventArgs : EventArgs
    {
        public float X { get; private set; }
        public float Y { get; private set; }
        public uint TimeStamp { get; private set; }
        public uint Force { get; private set; }
        public string Document { get; private set; }
        public uint Page { get; private set; }

        public AnnotoPenMotionEventArgs(float x, float y, uint timeStamp, uint force, string document, uint page)
        {
            this.X = x;
            this.Y = y;
            this.TimeStamp = timeStamp;
            this.Force = force;
            this.Document = document;
            this.Page = page;
        }
    }
}
