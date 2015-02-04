import java.io.*;

public class cat {
	public static void main(String[] args) throws IOException {
		FileInputStream in = new FileInputStream(FileDescriptor.in);
		FileOutputStream out = new FileOutputStream(FileDescriptor.out);
		int bufSize = 4096;
		byte[] buf = new byte[bufSize];
		int count = 0;
		while ((count = in.read(buf, 0, bufSize)) > -1) {
			out.write(buf, 0, count);
		}
		in.close();
		out.close();
	}
}
