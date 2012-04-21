package edu.ucsd.hci.interactivespace.annotopen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;

import com.google.protobuf.CodedInputStream;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Test.Builder tb = Test.newBuilder();
		tb.setId(1)
		.setName("Hello World!");
		
		Test t = tb.build();
		System.out.println(t.toString());
		
		try {
			FileOutputStream fos = new FileOutputStream("test.bin");
			FileInputStream fis = new FileInputStream("test.bin");
			
			t.writeTo(fos);
			t.writeTo(fos);
			
			
			CodedInputStream cis = CodedInputStream.newInstance(fis);
			int limit = cis.pushLimit(t.getSerializedSize());

			Test t2 = Test.parseFrom(cis);
			System.out.println(t2.getId());
			System.out.println(t2.getName());
			
			cis.popLimit(limit);
			limit = cis.pushLimit(t.getSerializedSize());
		
			t2 = Test.parseFrom(cis);
			System.out.println(t2.getId());
			System.out.println(t2.getName());
			
			cis.popLimit(limit);
			
			fos.close();
			fis.close();
			
			t.writeTo(System.out);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
