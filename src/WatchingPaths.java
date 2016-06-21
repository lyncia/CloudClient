import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class WatchingPaths {
	
	private String S = "C:/environment/temp/";
	public void subDirList(String source, String file_name){
		try{
			BufferedWriter writer= new BufferedWriter(new FileWriter(source + file_name));
			subDirList(source, writer);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	
	}
	
	
	public void subDirList(String source, BufferedWriter writer){
		File dir = new File(source); 
		File[] fileList = dir.listFiles();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/hh/mm/ss");
		Long lastModified;
		Date date;
		String r_path;
		char[] buffer = new char[257];
		try{
			for(int i = 0 ; i < fileList.length ; i++){
				File file = fileList[i];
				lastModified=file.lastModified();
				date = new Date(lastModified);
				
				if(file.isFile()){
   
					System.out.println("\t File Name = " + file.getName());
					r_path=file.getPath().substring(S.length(),file.getPath().length());
					r_path=r_path.replace("\\", "/");
					buffer = r_path.toCharArray();
					
					System.out.println(r_path+ " " + sdf.format(date)+System.getProperty("line.separator"));
					writer.write("1 ./root/");
					writer.write(buffer);
					for(int k=0;k<248-buffer.length;k++)
						writer.write(" ");
					writer.write(" " + sdf.format(date)+ " ");
					writer.flush();
				}else if(file.isDirectory()){
					/*System.out.println("Directory Name = " + file.getName());
					r_path=file.getPath().substring(S.length(),file.getPath().length());
					r_path=r_path.replace("\\", "/");
					buffer = r_path.toCharArray();
					System.out.println(r_path+ " " + sdf.format(date)+System.getProperty("line.separator"));
					writer.write("0 ./root/");
					writer.write(buffer);
					for(int k=0;k<248-buffer.length;k++)
						writer.write(" ");
					writer.write(" " + sdf.format(date)+ " ");
					writer.flush();
					
					subDirList(file.getCanonicalPath().toString(),writer); 
					System.out.println("directory end");
					*/
				}
				
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	
	
	public int watchPath(String source, String fName) throws IOException 
	{
		   FileSystem fs = FileSystems.getDefault();
		   
           Path watchPath = fs.getPath(source);
           WatchService watchService = fs.newWatchService();


           watchPath.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);

           while (true) {
                  try {
                        WatchKey changeKey = watchService.take();
                       
                        List<WatchEvent<?>> watchEvents = changeKey.pollEvents();

                        for (WatchEvent<?> watchEvent : watchEvents) {
                               // Ours are all Path type events:
                               WatchEvent<Path> pathEvent = (WatchEvent<Path>) watchEvent;

                               Path path = pathEvent.context();
                               WatchEvent.Kind<Path> eventKind = pathEvent.kind();

                               System.out.println(eventKind + " for path: " + path);
                              
                               if(eventKind.toString().equals("ENTRY_CREATE"))
                               {
                            	   fName=path.toString().substring(source.length(), path.toString().length());
                            	   return 1;
                               }
                               else if(eventKind.toString().equals("ENTRY_DELETE"))
                               {
                            	   fName=path.toString().substring(source.length(), path.toString().length());
                            	   return 2;
                            	   
                               }
                               else if(eventKind.toString().equals("ENTRY_MODIFY"))
                               {
                            	   fName=path.toString();
                            	   return 3;
                               }
                         }

                        changeKey.reset(); // Important!

                  } catch (InterruptedException e) {
                        e.printStackTrace();
                  }
           }
		
	}
}
 
 