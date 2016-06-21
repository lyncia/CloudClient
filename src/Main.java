import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.lang.*;

public class Main {
//
	public String serverIP = "163.180.173.156";
	public Socket so;
	public int buf_size = 1024;
	public int port = 1234;

	public WatchingPaths wp;
	public String source = "C:/environment/temp/";
	public String first_send = "init.txt";
	public String first_receive = "init_server.txt";

	private InputStream _in;
	private DataInputStream _dis;
	private OutputStream _out;
	private DataOutputStream _dos;

	public Main() throws Exception {
		wp = new WatchingPaths();
		
		try {
			
			so = new Socket(serverIP, port);
			System.out.println("server connected");
			wp.subDirList(source, first_send);
		
			_in = so.getInputStream();
			_dis = new DataInputStream(_in);
			_out = so.getOutputStream();
			_dos = new DataOutputStream(_out);
			_in = so.getInputStream();
			_dis = new DataInputStream(_in);
			_out = so.getOutputStream();
			_dos = new DataOutputStream(_out);
			/*
			byte b[] = new byte [1024];
			
			int leng= _dis.readInt();
			_dis.read(b,0,leng);
			String ss=b.toString();
			System.out.println(b);
			System.out.println(ss);
			*/
			// init.txt sending
			String fName =  first_send;
			sendFile(fName);
			System.out.println("first file sent..");

			// list receiving
			 
			String[] get_files;
			long[] get_files_time;
			int get_FDnum; // num of directory + file
			int get_Fnum = 0; // num of files

			String[] give_files;
			int give_FDnum;
			int give_Fnum = 0;

			int isF;

			// get receive list
			get_FDnum = _dis.readInt();
			get_files = new String[get_FDnum];
			get_files_time = new long[get_FDnum];
			//String fn = new String ();
			
			for (int i = 0; i < get_FDnum; i++) {
				//isF = _dis.readInt();
				isF=1;
				if (isF == 0) // is directory -> make directory
				{
					/*fn = _dis.readUTF();
					fn = fn.substring(7, fn.length());
					fName = source + fn;
					File f;
					f = new File(fName);
					f.mkdirs();

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss");
					String newLastModified = _dis.readUTF();
					Date newDate = sdf.parse(newLastModified);
					f.setLastModified(newDate.getTime());
					*/

				} else if (isF == 1) // is File -> add to receive list
				{
					//fn = "";
					//fn += _dis.readUTF();
					//int len ;
					//byte[] fn = new byte[256];
					//len = _dis.read(fn);
					//System.out.println(fn);
					//String ss= fn.toString();
					//ss = ss.substring(5,len);
					String ss =  i + ".txt";
					ss.replace("/","\\");
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss");
					//String newLastModified = _dis.readUTF();
					//Date newDate = sdf.parse(newLastModified);
					//get_files_time[get_Fnum] = newDate.getTime();
					//receiveFile(source+get_files[i], get_files_time[i]);
					receiveFile(ss);
					get_Fnum++;
				}

			}

			// get send list
			give_FDnum = _dis.readInt();
			give_files = new String[get_FDnum];
			for (int i = 0; i < give_FDnum; i++) {
				isF = _dis.read();
				if (isF == 0) // is directory -> make directory : skip
				{
					continue;
				} else if (isF == 1) // is File -> add to send list
				{
					give_files[get_Fnum] = _dis.readUTF();
					get_Fnum++;
				}

			}
			

			// receive files
			for (int i = 0; i < get_Fnum; i++) {
				receiveFile(source+get_files[i], get_files_time[i]);
			}
			// send files
			for (int i = 0; i < give_Fnum; i++) {
				_dos.writeUTF(give_files[i]);
				sendFile(source+give_files[i]);
			}
			// _dos.close(); _bis.close(); _fis.close(); so.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public static void main(String[] args) throws IOException {
		Main m;
		try {
			m = new Main();
			//m.event();
			m.receiveFile("cat.jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void event() {
		String fName= new String();

		try {
			while (true) {
				int e = wp.watchPath(source, fName);
				_dos.write(e);
				_dos.writeUTF(fName);
				sendFile(source+fName);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean sendFile(String fName) {

		try {
			int len;

			byte[] data = new byte[buf_size];
			data = new byte[buf_size];
			
			File f = new File(source + fName);
			if(!f.exists())
			{_dos.writeInt(0); _dos.writeByte(0) ;return false;}
			System.out.println("file sending...");

			FileInputStream _fis = new FileInputStream(f);
			BufferedInputStream _bis = new BufferedInputStream(_fis);
			//_dos.writeBytes(String.valueOf(f.length()));
			int l =(int)f.length();
			ByteBuffer b = ByteBuffer.allocate(Integer.SIZE / 8);
			b.putInt(l);
			b.order(ByteOrder.LITTLE_ENDIAN);
			_dos.write(b.array(),0,4);
			
			_dos.flush();
			System.out.println("f.length(): "+(int)f.length());
			System.out.println(longToBytes(f.length()));
			while ((len = _bis.read(data)) != -1) {
				_dos.write(data, 0, len);
				_dos.flush();
			}

			// dataOutputStream.write();
			/*
			 * _dos.close(); _bis.close(); _fis.close(); _so.close();
			 * ss.close();
			 */
			System.out.println("File sent!");
			System.out.println("Sent file size: " + f.length());
			return true;

		} catch (SocketException ee) {
			ee.printStackTrace();
			System.out.println("data connection expire");
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

	private boolean receiveFile(String fName) {

		try {
			// fName = "C:/environment/temp/test.docx";

			System.out.println(fName);
			
			File f = new File(source + fName);
			if(f.exists())
			{
				f.delete();
				f.createNewFile();
			}
			else {
				f.createNewFile();
			}

			FileOutputStream _fos = new FileOutputStream(f);
			BufferedOutputStream _bos = new BufferedOutputStream(_fos);

			System.out.println(fName + " is created");

			int len;
			int total = _dis.readInt();
			System.out.println("total: " + total);
			byte[] data = new byte[buf_size];
			while ((len = _dis.read(data)) != -1) {
				System.out.println(len);
				_bos.write(data, 0, len);
				_bos.flush();
				total -= len;
				
				if(total==0)
					break;
			}

			/*
			 * int size= _dis.readInt(); byte[] data= new byte[size];
			 * _dis.read(data); _bos.write(data);
			 */
			/*
			 * _dos.close(); _out.close(); _dis.close(); _in.close();
			 * _so.close(); ss.close();
			 */
			System.out.println("File received");
			System.out.println("Received file size: " + f.length());
			return true;

		} catch (SocketException ee) {
			System.out.println("data connection expire");
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private boolean receiveFile(String fName, long modified) {

		try {
			// fName = "C:/environment/temp/test.docx";

			System.out.println(fName);
			File f = new File(fName);
			if (!f.createNewFile()) {
				f = new File(fName);
				f.createNewFile();
			}

			FileOutputStream _fos = new FileOutputStream(f);
			BufferedOutputStream _bos = new BufferedOutputStream(_fos);

			System.out.println(fName + "is created");

			int len;
			int total = _dis.read();

			byte[] data = new byte[buf_size];
			while ((len = _dis.read(data)) != -1) {
				_bos.write(data, 0, len);
				_bos.flush();
				total -= len;
				if(total<=0)
					break;
			}

			f.setLastModified(modified);

			/*
			 * int size= _dis.readInt(); byte[] data= new byte[size];
			 * _dis.read(data); _bos.write(data);
			 */
			/*
			 * _dos.close(); _out.close(); _dis.close(); _in.close();
			 * _so.close(); ss.close();
			 */
			System.out.println("File received");
			System.out.println("Received file size:  " + f.length());
			return true;

		} catch (SocketException ee) {
			System.out.println("data connection expire");
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

	public long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.put(bytes);
		buffer.flip();// need flip
		return buffer.getLong();
	}

}