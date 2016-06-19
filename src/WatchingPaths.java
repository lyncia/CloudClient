import java.io.*;
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
	
	/*
	public void subDirList(String source){
		File dir = new File(source); 
		File[] fileList = dir.listFiles();
		
		try{
			for(int i = 0 ; i < fileList.length ; i++){
				File file = fileList[i]; 
				if(file.isFile()){
    // 파일이 있다면 파일 이름 출력
					System.out.println("\t 파일 이름 = " + file.getName());
					
				}else if(file.isDirectory()){
					System.out.println("디렉토리 이름 = " + file.getName());
					System.out.println(file.getPath().toString() +System.getProperty("line.separator"));
				
    // 서브디렉토리가 존재하면 재귀적 방법으로 다시 탐색
					subDirList(file.getCanonicalPath().toString()); 
					System.out.println("directory end");
				}
				
			}
			
		}catch(IOException e){
			
		}
		
	}
	*/
	public void subDirList(String source, String file_name){
		try{
			BufferedWriter writer= new BufferedWriter(new FileWriter(file_name));
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
		char[] buffer = new char[256];
		try{
			for(int i = 0 ; i < fileList.length ; i++){
				File file = fileList[i];
				lastModified=file.lastModified();
				date = new Date(lastModified);
				
				if(file.isFile()){
    // 파일이 있다면 파일 이름 출력
					System.out.println("\t 파일 이름 = " + file.getName());
					r_path=file.getPath().substring(source.length(),file.getPath().length());
					buffer = r_path.toCharArray();
					System.out.println(r_path+ " " + sdf.format(date)+System.getProperty("line.separator"));
					writer.write("0 " +buffer + sdf.format(date)+System.getProperty("line.separator"));
					writer.flush();
				}else if(file.isDirectory()){
					System.out.println("디렉토리 이름 = " + file.getName());
					r_path=file.getPath().substring(source.length(),file.getPath().length());
					buffer = r_path.toCharArray();
					System.out.println(r_path+ " " + sdf.format(date)+System.getProperty("line.separator"));
					writer.write("0 " +buffer + sdf.format(date)+System.getProperty("line.separator"));
					writer.flush();
    // 서브디렉토리가 존재하면 재귀적 방법으로 다시 탐색
					subDirList(file.getCanonicalPath().toString(),writer); 
					System.out.println("directory end");
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
                       
                        // 지정된 디렉토리에 변경이되는지 이벤트를 모니터링한다.
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
 
 