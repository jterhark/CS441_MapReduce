import CsvGrapher.CSVMapper
import org.scalatest.FunSuite
import com.typesafe.config.ConfigFactory
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Mapper
import org.slf4j.LoggerFactory

class Tests extends FunSuite {
  test("Config should load"){
    val config = ConfigFactory.load()
    assert(config!=null)
  }

  test("Logger should load"){
    val logger = LoggerFactory.getLogger(classOf[Tests])
    assert(logger!=null)
  }

}
