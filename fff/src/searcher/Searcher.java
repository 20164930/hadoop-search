package searcher;
import java.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class Searcher{

public static class Map extends Mapper<Object,Text,Text,Text>{
	private Text keyInfo = new Text();
	private Text valueInfo = new Text();
	
	public void map(Object key,Text value,Context context) throws IOException,InterruptedException{
		
		Analyzer sca = new SmartChineseAnalyzer( );  //中文分词
		TokenStream ts = sca.tokenStream("field", value.toString());  
		CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);  

		ts.reset();
		while (ts.incrementToken()) {  
  
			String url=value.toString().replace(" ","").split(",")[9];
			String title=value.toString().replace(" ","").split(",")[2];
        // key值为：单词+URL#标题
			keyInfo.set( ch.toString()+"+"+url+"#"+title);
        //词频初始为1
			valueInfo.set("1");
			context.write(keyInfo, valueInfo);
		}  
		ts.end();  
		ts.close();  
		}
	}
	
	public static class Combiner extends Reducer<Text,Text,Text,Text>{
		private Text info = new Text();
		public void reduce(Text key,Iterable<Text>values,Context context) throws IOException, InterruptedException{
			int sum = 0;
			for(Text value:values){
				sum += Integer.parseInt(value.toString());
			}



			//对传进来的key进行拆分，以+为界

			String record = key.toString();
			String[] str = record.split("[+]"); //+为特殊符号，需要加上[]
			//value为： URL#标题*出现次数
			info.set(str[1]+"*"+sum);
			key.set(str[0]);
			context.write(key,info);
		}
	}
	public static class Reduce extends Reducer<Text,Text,Text,Text>{
		private Text result = new Text();
		public void reduce(Text key,Iterable<Text>values,Context context) throws IOException, InterruptedException{
			String value =new String();
			for(Text value1:values){
				value += value1.toString()+" ; ";
			}
			result.set(value);
			context.write(key,result);
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException,InterruptedException {
// TODO Auto-generated method stub
		System.setProperty("hadoop.home.dir","F:\\hadoop-2.7.6");
		Configuration conf = new Configuration();
		Job job = new Job(conf);
		job.setJarByClass(Searcher.class);
		job.setNumReduceTasks(1);//设置reduce的任务数量为1，平常的小测试不需要开辟太多的reduce任务进程
		job.setMapperClass(Map.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setCombinerClass(Combiner.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path("hdfs://192.168.88.128:9000/input"));
		FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.88.128:9000/output"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
