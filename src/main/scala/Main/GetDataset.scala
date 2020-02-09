package Main

import java.io.File

import Main_model.Article
import com.github.tototoshi.csv.CSVReader
import com.typesafe.config.ConfigFactory
import info.debatty.java.stringsimilarity.{Jaccard, JaroWinkler, Levenshtein, NormalizedLevenshtein}


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
          venue = row("venue"),
          blockingKey = row("title").
            replaceAll("""[^\p{IsAlphabetic}|\p{IsDigit}]""", "").toUpperCase())

        res += (row("id") -> Article(id = row("id"),
          title = row("title"),
          authors = row("authors").split(","),
          year = row("year"),
          venue = row("venue"),
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






    (res1, res2, res_ans, resSeq, 0)
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

    val l = new Levenshtein()
    val jaccard = new Jaccard()
    val jarowink = new JaroWinkler()

    var resSeq: Seq[Article] = Seq() // последовательность id статей, как они в табличке идут
    var res_ans: Map[String, String] = Map()



    def mapToArticle(d: List[Map[String, String]]): Map[String, Article] = {
      var res: Map[String, Article] = Map()
      for (row <- d) {
        resSeq :+= Article(id = row("id"),
          title = row("title"),
          authors = row("authors").split(","),
          year = row("year"),
          venue = row("venue"),
          blockingKey = row("title").
            replaceAll("""[^\p{IsAlphabetic}|\p{IsDigit}]""", "").toUpperCase())


        res += (row("id") -> Article(id = row("id"),
          title = row("title"),
          authors = row("authors").split(","),
          year = row("year"),
          venue = row("venue"),
          blockingKey = row("title").
            replaceAll("""[^\p{IsAlphabetic}|\p{IsDigit}]""", "").toUpperCase()))
      }
      res
    }



    val res1: Map[String, Article] = mapToArticle(d1) // ACM - слева
    val res2: Map[String, Article] = mapToArticle(d2)


    res_ans += (ans(ans.length - 1)(1) -> ans(ans.length - 1)(0))
    res_ans += (ans(ans.length - 1)(0) -> ans(ans.length - 1)(0))

    for (i <-1 until ans.length - 1) {
      res_ans += (ans(i)(1) -> ans(i)(0))
      res_ans += (ans(i)(0) -> ans(i)(0))
      val article1: Article = res1(ans(i)(1))
      //println(i, article1.title)
      val article2: Article = res2(ans(i)(0))
      println(article1, article2)
      featuresTrain :+= Array[Double](
        //l.distance(article1.title, article2.title),
        //l.distance(article1.authors.sorted.mkString(","), article2.authors.sorted.mkString(",")),
        l.distance(article1.year, article2.year),
        //l.distance(article1.venue, article2.venue),
        jaccard.distance(article1.authors.sorted.mkString(","), article2.authors.sorted.mkString(",")),
        //jarowink.distance(article1.title, article2.title),
        jarowink.distance(article1.authors.sorted.mkString(","), article2.authors.sorted.mkString(",")),
        jarowink.distance(article1.year, article2.year)
        //jarowink.distance(article1.venue, article2.venue)
      )
      answersTrain :+= 1
      featuresTrain.last.foreach(x => print(s" $x"))
      print(" ")
      println(answersTrain.last)

      val article3: Article = res1(ans(i + 1)(1))
      featuresTrain :+= Array[Double](
        //l.distance(article3.title, article2.title),
        //l.distance(article3.authors.sorted.mkString(","), article2.authors.sorted.mkString(",")),
        l.distance(article3.year, article2.year),
        //l.distance(article3.venue, article2.venue),
        jaccard.distance(article3.authors.sorted.mkString(","), article2.authors.sorted.mkString(",")),
        //jarowink.distance(article3.title, article2.title),
        jarowink.distance(article3.authors.sorted.mkString(","), article2.authors.sorted.mkString(",")),
        jarowink.distance(article3.year, article2.year)
        //jarowink.distance(article3.venue, article2.venue)
      )
      answersTrain :+= 0
      println(article2, article3)
      featuresTrain.last.foreach(x => print(s" $x"))
      print(" ")
      println(answersTrain.last)
      println(article2, article3)

      val article4: Article = res2(ans(i + 1)(0))
      featuresTrain :+= Array[Double](
        //l.distance(article1.title, article4.title),
        //l.distance(article1.authors.sorted.mkString(","), article4.authors.sorted.mkString(",")),
        l.distance(article1.year, article4.year),
        //l.distance(article1.venue, article4.venue),
        jaccard.distance(article1.authors.sorted.mkString(","), article4.authors.sorted.mkString(",")),
        //jarowink.distance(article1.title, article4.title),
        jarowink.distance(article1.authors.sorted.mkString(","), article4.authors.sorted.mkString(",")),
        jarowink.distance(article1.year, article4.year)
        //jarowink.distance(article1.venue, article4.venue)
      )
      answersTrain :+= 0
      println(article1, article4)
      featuresTrain.last.foreach(x => print(s" $x"))
      print(" ")
      println(answersTrain.last)




    }


    (res1, res2, res_ans, resSeq,
      featuresTrain, answersTrain)
  }
}
