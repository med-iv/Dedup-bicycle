package Main

import java.io.File

import Main_model.Article
import com.github.tototoshi.csv.CSVReader
import com.typesafe.config.ConfigFactory
import info.debatty.java.stringsimilarity.{Jaccard, NormalizedLevenshtein}


//XXX Говнокод

object GetDataset {
  val conf = ConfigFactory.load()

  def getScholar() = {

    val (path1: String, path2: String, pathAnswers: String) =
      (conf.getConfig("DBLP-Scholar").getString("DBLP1"),
        conf.getConfig("DBLP-Scholar").getString("Scholar"),
        conf.getConfig("DBLP-Scholar").getString("Answers"))


    val reader1 = CSVReader.open(new File(path1))
    val reader2 = CSVReader.open(new File(path2))
    val readerAnswers = CSVReader.open(new File(pathAnswers))

    val d1 = reader1.allWithHeaders()
    val d2 = reader2.allWithHeaders()

    val ans = readerAnswers.all()

    var resSeq: Seq[Article] = Seq() // последовательность id статей, как они в табличке идут



    def mapToArticle(d: List[Map[String, String]]): Map[String, Article] = {
      var res: Map[String, Article] = Map()
      for (row <- d) {
        resSeq :+= Article(id = row("id"),
          title = row("title"),
          authors = row("authors").split(","),
          year = row("year"),
          blockingKey = row("title").
            replaceAll("""[^\p{IsAlphabetic}|\p{IsDigit}]""", "").toUpperCase())

        res += (row("id") -> Article(id = row("id"),
          title = row("title"),
          authors = row("authors").split(","),
          year = row("year"),
          blockingKey = row("title").
            replaceAll("""[^\p{IsAlphabetic}|\p{IsDigit}]""", "").toUpperCase()))
      }
      res
    }



    val res1: Map[String, Article] = mapToArticle(d1)
    val res2: Map[String, Article] = mapToArticle(d2)

    //var initFN: Int = 0


    var res_ans: Map[String, String] = Map()
    res_ans += ans(1)(1) -> ans(1)(0)
    res_ans += ans(1)(0) -> ans(1)(0)
    for (i <-2 until ans.length) {
      //initFN += {if (ans(i)(0) == ans(i - 1)(0) && res2(ans(i)(1)).blockingKey != res2(ans(i - 1)(1)).blockingKey) 1 else 0}
      res_ans += (ans(i)(1) -> ans(i)(0))
      res_ans += (ans(i)(0) -> ans(i)(0))

    }






    (res1, res2, res_ans, resSeq, 2390)
  }


  def getACM() = {

    val (path1: String, path2: String, pathAnswers: String) =
      (conf.getConfig("DBLP-ACM").getString("ACM"),
        conf.getConfig("DBLP-ACM").getString("DBLP2"),
        conf.getConfig("DBLP-ACM").getString("Answers"))


    val reader1 = CSVReader.open(new File(path1))
    val reader2 = CSVReader.open(new File(path2))
    val readerAnswers = CSVReader.open(new File(pathAnswers))

    val d1 = reader1.allWithHeaders()
    val d2 = reader2.allWithHeaders()

    val ans = readerAnswers.all()

    var featuresTrain: Array[Array[Double]] = Array()
    var answersTrain: Array[Int] = Array()

    val l = new NormalizedLevenshtein()
    val jaccard = new Jaccard()

    var resSeq: Seq[Article] = Seq() // последовательность id статей, как они в табличке идут
    var res_ans: Map[String, String] = Map()



    def mapToArticle(d: List[Map[String, String]]): Map[String, Article] = {
      var res: Map[String, Article] = Map()
      for (row <- d) {
        resSeq :+= Article(id = row("id"),
          title = row("title"),
          authors = row("authors").split(","),
          year = row("year"),
          blockingKey = row("title").
            replaceAll("""[^\p{IsAlphabetic}|\p{IsDigit}]""", "").toUpperCase())


        res += (row("id") -> Article(id = row("id"),
          title = row("title"),
          authors = row("authors").split(","),
          year = row("year"),
          blockingKey = row("title").
            replaceAll("""[^\p{IsAlphabetic}|\p{IsDigit}]""", "").toUpperCase()))
      }
      res
    }



    val res1: Map[String, Article] = mapToArticle(d1) // ACM - слева
    val res2: Map[String, Article] = mapToArticle(d2)



    for (i <-1 until ans.length - 1) {
      //res_ans += (ans(i)(1) -> ans(i)(0))
      //res_ans += (ans(i)(0) -> ans(i)(0))
      val article1: Article = res1(ans(i)(1))
      //println(i, article1.title)
      val article2: Article = res2(ans(i)(0))
      featuresTrain :+= Array[Double](
        l.distance(article1.title, article2.title),
        jaccard.distance(article1.authors.mkString(","), article2.authors.mkString(",")),
        l.distance(article1.year, article2.year)
      )
      answersTrain :+= 1
      val article3: Article = res1(ans(i + 1)(1))
      featuresTrain :+= Array[Double](
        l.distance(article3.title, article2.title),
        jaccard.distance(article3.authors.mkString(","), article2.authors.mkString(",")),
        l.distance(article3.year, article2.year)
      )
      answersTrain :+= 0
    }


    (//res1, res2, res_ans, resSeq,
      featuresTrain, answersTrain)
  }
}
