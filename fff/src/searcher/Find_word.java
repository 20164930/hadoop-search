package searcher;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


public class Find_word{

    public static class FindMapper extends Mapper<Text, Text, Text, Text>{
    	
    	String word;
    	public void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            word=conf.get("keyword"); //获得外部传入的关键词
        }

    	
        @Override
        protected void map(Text key, Text value, Mapper<Text, Text, Text, Text>.Context context)
                throws IOException, InterruptedException {
            String text=word;       //用户输入的关键字
            Analyzer sca = new SmartChineseAnalyzer( );   //中文分词

            TokenStream ts = sca.tokenStream("field", text);  
            CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);  

            ts.reset();  
            while (ts.incrementToken()) {  
                if(ch.toString().equals(key.toString())){
                    //分割多个url
                    String[] urls=value.toString().split(" ; ");
                    int count=0;
                    for (String url : urls) {
                        String oneurl=url.split("[*]")[0];
                        
                        count=Integer.parseInt(url.split("[*]")[1]);
                        String newvalue=ch.toString()+";"+count;
                        //System.out.println(oneurl+" "+newvalue);
                        context.write(new Text(oneurl),new Text( newvalue));
                    }
                }
            }  
            ts.end();  
            ts.close();  

        }
    }

    public static class FindCombiner extends Reducer<Text, Text, Text, Text>{
        @Override
        protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
                throws IOException, InterruptedException {

            //统计词频
            int sum = 0;
            for (Text value : values) {
                String count=value.toString().split(";")[1];
                sum += Integer.parseInt(count );
            }
            context.write(new Text(String.valueOf(sum)),new Text(key.toString()) );
        }
    }


    public static class FindReducer extends Reducer<Text, Text, Text, Text>{

        @Override
        protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context)
                throws IOException, InterruptedException {

            //生成文档列表
        	
        	
            for (Text text : values) {
                context.write(key, text);
                //写入文件
                LogWrite.log(key+" "+text+"\r\n");
            }
        }

    }
    public  void run(String input) {
    	
    	System.setProperty("hadoop.home.dir","F:\\hadoop-2.7.6");
            try {
                Configuration conf = new Configuration();
                conf.set("keyword",String.valueOf(input));

        		Job job = new Job(conf);
                job.setJarByClass(Find_word.class);

                //实现map函数，根据输入的<key,value>对生成中间结果。
                job.setMapperClass(FindMapper.class);

                job.setMapOutputKeyClass(Text.class);
                job.setMapOutputValueClass(Text.class);
                job.setInputFormatClass(KeyValueTextInputFormat.class);
                job.setCombinerClass(FindCombiner.class);
                job.setReducerClass(FindReducer.class);

                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
                
                Path path = new Path("hdfs://192.168.88.128:9000/result");
                FileSystem fileSystem = path.getFileSystem(conf);
                if (fileSystem.exists(path)) {
                	fileSystem.delete(path, true);// true的意思是，就算output有东西，也一带删除
                }
 
                FileInputFormat.addInputPath(job, new Path("hdfs://192.168.88.128:9000/output/part-r-00000")); //将索引表也传入mapreduce
                FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.88.128:9000/result"));
                job.waitForCompletion(true);
                LogWrite.end(true);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }  catch (IOException e) {
                e.printStackTrace();
            }  catch (ClassNotFoundException e){
            	e.printStackTrace();
            } catch(InterruptedException e){
            	e.printStackTrace();
            }
        }
    
}
