package searcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;

class LogWrite{
private static boolean fileLog = true;
private static String logFileName ="C:\\Users\\Administrator\\Desktop\\a.txt";//指定程序执行结果保存的文件路径
private static boolean end=true;
public static OutputStream getOutputStream() throws IOException{
if(fileLog){
	File file = new File(logFileName);
	if(!file.exists()){	
		file.createNewFile();
		}else{
			if(end==true){
				file.delete();
			}
		}
	return new FileOutputStream(file, true);	
	}else{
		return System.out;
	}	
}
public static void log(String info) throws IOException{
	OutputStream out = getOutputStream();
	out.write(info.getBytes("utf-8"));
	out.flush();
	out.close();//关闭流，否则无法删除文件
	end=false;
	}

public static void end(boolean b){
	end=b;
}





}