package searcher;

import java.io.IOException;
import java.text.ParseException;
import java.util.Random;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.log4j.BasicConfigurator;

public class Inhbase {
	public static class MapperClass extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {
	
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
		String[] lines = value.toString().replaceAll(" ", "").split(",");
		String rowkey = null;
		
		rowkey = lines[0];//rowkeys生成
		Put put = new Put(rowkey.getBytes());
		
		//将分割后的数据插入表中 
		put.addColumn(Bytes.toBytes("duration"), Bytes.toBytes("duration"), Bytes.toBytes(lines[1]));
		put.addColumn(Bytes.toBytes("title"), Bytes.toBytes("title"), Bytes.toBytes(lines[2]));
		put.addColumn(Bytes.toBytes("release_time"), Bytes.toBytes("release_time"), Bytes.toBytes(lines[3]));
		put.addColumn(Bytes.toBytes("play_volume"), Bytes.toBytes("play_volume"), Bytes.toBytes(lines[4]));
		put.addColumn(Bytes.toBytes("num_of_word"), Bytes.toBytes("num_of_word"), Bytes.toBytes(lines[5]));//
		put.addColumn(Bytes.toBytes("num_of_coin"), Bytes.toBytes("num_of_coin"), Bytes.toBytes(lines[6]));//
		put.addColumn(Bytes.toBytes("num_of_collection"), Bytes.toBytes("num_of_collection"), Bytes.toBytes(lines[7]));
		put.addColumn(Bytes.toBytes("label"), Bytes.toBytes("label"),Bytes.toBytes(lines[8]));//
		put.addColumn(Bytes.toBytes("url"), Bytes.toBytes("url"),Bytes.toBytes(lines[9]));
		
		context.write(new ImmutableBytesWritable(rowkey.getBytes()), put);
		}
	}
	

		public static void main(String[] args)
		throws IOException, InterruptedException, ClassNotFoundException, ParseException {
			
			//BasicConfigurator.configure();
		System.setProperty("hadoop.home.dir","F:\\hadoop-2.7.6");
		long start = System.currentTimeMillis();// 任务开始时间
		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum", "192.168.88.135,192.168.88.132,192.168.88.133");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("zookeeper.znode.parent", "/hbase");
		Job job = Job.getInstance(conf, "import-hbase");
		job.setJarByClass(Inhbase.class);
		job.setMapperClass(MapperClass.class);
		job.setNumReduceTasks(0);
		job.setOutputFormatClass(TableOutputFormat.class);
		job.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, "test:bilibili");
		FileInputFormat.addInputPath(job, new Path("hdfs://192.168.88.128:9000/input"));
		System.out.println(job.waitForCompletion(true) ? 0 : 1);
		long end = System.currentTimeMillis();// 任务结束时间
		System.out.println("用时  " + ((end - start) / 1000) + " s");
		
		}


}
