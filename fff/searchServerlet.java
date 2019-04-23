

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import searcher.*;

@WebServlet(name = "GetServlet",
urlPatterns = {"/get.html"})
public class searchServerlet extends HttpServlet {
       File file;
       FileReader fr;
       BufferedReader br;
 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String keyword=request.getParameter("text");
		Url_power[] temp_list=new Url_power[1000];
		int num=0;
		Find_word find=new Find_word();  //调用程序连接集群
		find.run(keyword);
		try{
			file=new File("C:\\Users\\Administrator\\Desktop\\a.txt");
			fr=new FileReader(file);
			br=new BufferedReader(fr);
		}catch (IOException e){
			e.printStackTrace();
		}
		String s;
		int standard=(int)(keyword.length()/2)+1;  //设置展示的匹配度的最小值
		while((s=br.readLine())!=null){
			if(Integer.parseInt(s.split(" ")[0])>=standard){
				temp_list[num]=new Url_power(s.split(" ")[1],Integer.parseInt(s.split(" ")[0]));
				num++;
			}
		}
		br.close();
		fr.close();
		Url_power[] list=new Url_power[num];
		for(int i=0;i<num;i++){
			list[i]=temp_list[i];
		}
		
		Url_power[] urllist=Qsort.quickSort(list);  //利用快排对结果进行排序
		request.setAttribute("urls",urllist);
		request.getRequestDispatcher("/showurl.jsp").forward(request,response);
		
	}

}
