import java.lang
import java.text.SimpleDateFormat
import java.util.{Date, StringTokenizer}

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.slf4j.LoggerFactory


object CsvGrapher extends App {

  class CSVMapper extends Mapper[Object, Text, Text, Text]{
    private val authorOne = new Text()
    private val authorTwo = new Text()

    //value = comma separated list of authors sharing an article
    override def map(key: Object, value: Text, context: Mapper[Object, Text, Text, Text]#Context): Unit = {
      val authors: Array[String] = value.toString.split(',')
      val a = 0
      val b = 0

      //loop through authors creating a link between each author
      for(a<-authors.indices){
        for(b<-a + 1 until authors.length){
          authorOne.set(authors{a}.replace("\"", ""))
          authorTwo.set(authors{b}.replace("\"", ""))
          context.write(authorOne, authorTwo)
        }
      }

      //if only one author create a link with himself/herself
      if(authors.length==1){
        authorOne.set(authors{0}.replace("\"", ""))
        authorTwo.set(authors{0}.replace("\"", ""))
      }
    }
  }

  class CSVReducer extends Reducer[Text, Text, Text, Text]{

    //writes each line to file in csv format
    override def reduce(key: Text, values: lang.Iterable[Text], context: Reducer[Text, Text, Text, Text]#Context): Unit = {
      values.forEach(x=>context.write(key, x))
    }
  }

  val logger = LoggerFactory.getLogger(CsvGrapher.getClass)

  logger.info("Started")
  val conf = new Configuration()
  conf.set("mapred.textoutputformat.separator",",") //generate csv
  val job = Job.getInstance(conf, "CsvGrapher")

  //setup classes
  job.setJarByClass(CsvGrapher.getClass)
  job.setMapperClass(classOf[CSVMapper])
  job.setCombinerClass(classOf[CSVReducer])
  job.setReducerClass(classOf[CSVReducer])
  job.setOutputKeyClass(classOf[Text])
  job.setOutputValueClass(classOf[Text])

  //setup paths
  FileInputFormat.addInputPath(job, new Path(args(0)))
  FileOutputFormat.setOutputPath(job, new Path(args(1)))
//  FileOutputFormat.setOutputPath(job, new Path(s"/user/admin/${new SimpleDateFormat("yyyyMMddHHmm'.txt'").format(new Date())}"))

  //wait
  if (job.waitForCompletion(true)) 0 else 1
  logger.info("Done")
}
