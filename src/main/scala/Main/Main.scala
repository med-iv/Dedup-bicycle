package Main

import com.github.tototoshi.csv._
import java.io.{File}


import Preprocessing

object Main extends App {

  val reader1 = CSVReader.open(new File("/home/ivan/Desktop/Курсач_datasets/DBLP-Scholar/DBLP1.csv"))
  val reader2 = CSVReader.open(new File("/home/ivan/Desktop/Курсач_datasets/DBLP-Scholar/Scholar.csv"))
  val readerAnswers = CSVReader.open(new File("/home/ivan/Desktop/Курсач_datasets/DBLP-Scholar/DBLP-Scholar_perfectMapping.csv"))

  val DBLP1 = reader1.allWithHeaders()
  val Scholar = reader2.allWithHeaders()
  val Answers = readerAnswers.allWithHeaders()


  val n: Int = 1000 //  n object pairs are randomly
  //selected among the ones satisfying a given minimal
  //threshold t applying a similarity measure m.
  val treshold: Double = 0.6



}
