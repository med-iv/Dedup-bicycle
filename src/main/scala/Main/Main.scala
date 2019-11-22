package Main

import com.github.tototoshi.csv._
import java.io.File

import Main_model.Article
import com.typesafe.config.ConfigFactory
import smile.classification.SVM
import smile.math.kernel.LinearKernel

import scala.concurrent._
import scala.concurrent.forkjoin._
import ExecutionContext.Implicits.global

//import Preprocessing

object Main extends App {

  val conf = ConfigFactory.load()

  println("Choose datasets:")

  val datasets = Map(0 -> "DBLP-Scholar", 1 -> "DBLP-ACM")
  datasets.foreach{case(key, value) => println(s"[$key] $value")}

  val numb = scala.io.StdIn.readInt()
  println("Running...")

  val (dataset1: Seq[Article], dataset2: Seq[Article], answersSet: Map[String, String]) = {

    val (path1: String, path2: String, pathAnswers: String) = numb match {
      case 0 => (conf.getConfig("DBLP-Scholar").getString("ACM"),
        conf.getConfig("DBLP-Scholar").getString("Scholar"),
        conf.getConfig("DBLP-Scholar").getString("Answers"))
      case _ => ("", "", "")
    }

    val reader1 = CSVReader.open(new File(path1))
    val reader2 = CSVReader.open(new File(path2))
    val readerAnswers = CSVReader.open(new File(pathAnswers))

    val d1 = reader1.allWithHeaders()
    val d2 = reader2.allWithHeaders()

    val ans = readerAnswers.all()

    var res_ans: Map[String, String] = Map()
    for (i <- ans.indices) {
      res_ans += (ans(i)(1) -> ans(i)(0))
    }


    def mapToArticle(d: List[Map[String, String]]): Seq[Article] = {
      d.map {row =>
        Article(id = row("id"),
          title = row("title"),
          authors = row("authors").split(","),
          venue = row("venue"),
          year = row("year"),
          blockingKey = row("title").replaceAll("\\s", "").toLowerCase)
      }
    }

    val res1: Seq[Article] = mapToArticle(d1)
    val res2: Seq[Article] = mapToArticle(d2)


    (res1, res2, res_ans)
  }

  /*val Blocks = {
    import Blocking.Blocker_standart
    val Blocker = new Blocker_standart
    Blocker.createBlocks(dataset2)
  }*/


 // for ((blockingKey, articles) <- Blocks) {
  //val f = Future {
  val articles = dataset2
  import info.debatty.java.stringsimilarity.{Levenshtein, Jaccard}
  import smile.classification.{svm, lda, cart}
  import smile.validation.cv

  val l = new Levenshtein()
  val jaccard = new Jaccard()

  var features: Array[Array[Double]] = Array()
  var answers: Array[Int] = Array()
  for (/*i <- articles.indices*/ i <- 0 until 2000) {
    for (j <- i + 1 until /*articles.length*/ 2000) {
      features :+= /*Main_model.Pair*/Array[Double](
        /*title_dist = */l.distance(articles(i).title, articles(j).title),
        /*authors_dist = */jaccard.distance(articles(i).authors.mkString(","),
          articles(j).authors.mkString(",")),
        /*venue_dist = */l.distance(articles(i).venue, articles(j).venue),
        /*year_dist = */l.distance(articles(i).year, articles(j).year)
      )
      answers :+= (if ( answersSet.get(articles(i).id) == answersSet.get(articles(j).id)
        && answersSet.get(articles(i).id).isDefined) 1 else 0)
    }
  }
  println("graphics")
  import smile.plot._


  //plot(features(), '*')

  println("CV")
  //for (elem <- answers) { println(elem) }
      //val classifier =
  cv(features, answers, 2) {case (x, y) =>
        svm[Array[Double]](x, y, new LinearKernel(), 0.1)}

    //}




  /*



  val n: Int = 1000 //  n object pairs are randomly
  //selected among the ones satisfying a given minimal
  //threshold t applying a similarity measure m.
  val treshold: Double = 0.6

 */

}
