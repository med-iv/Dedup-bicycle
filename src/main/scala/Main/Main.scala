package Main

import com.github.tototoshi.csv._
import java.io._

import Main_model.Article
import smile.classification.{LogisticRegression, SVM}
import smile.math.kernel.LinearKernel


import info.debatty.java.stringsimilarity.{Levenshtein, Jaccard, Cosine, NormalizedLevenshtein}
import smile.classification.{svm, lda, cart, logit}
import smile.validation.{cv, FMeasure, Accuracy, Recall}
import smile.plot.plot




import java.util.Calendar

object Main extends App {
  //System.setOut(new PrintStream(new FileOutputStream("/home/ivan/Desktop/file.out")))

  println("\nRunning...")
  println(Calendar.getInstance().getTime())
  println()

  val (acmTrain: Map[String, Article], dblp2Train: Map[String, Article],
       answersSetTrain: Map[String, String], articleSeqPreTrain: Seq[Article]) = GetDataset.getACM()


  val (dblp1Test: Map[String, Article], scholarTest: Map[String, Article],
  answersSetTest: Map[String, String], articleSeqTest: Seq[Article], initFN: Int) = GetDataset.getScholar()

  println(s"initFN = ${initFN}")

  println("\nLoading done")
  println(Calendar.getInstance().getTime())
  println()

/*
  val l = new NormalizedLevenshtein()
  val jaccard = new Jaccard()



  var featuresTrain: Array[Array[Double]] = Array()
  var answersTrain: Array[Int] = Array()


  try {
    var i: Int = 0
    for ((key1, article1) <- acmTrain) {
      var j = 0
      for ((key2, article2) <- dblp2Train) {
        featuresTrain :+= Array[Double](
          l.distance(article1.title, article2.title),
          jaccard.distance(article1.authors.mkString(","), article2.authors.mkString(",")),
          l.distance(article1.year, article2.year)
        )
        answersTrain :+= {if (answersSetTrain.get(key1).isDefined && answersSetTrain(key1) == key2) 1 else 0}
        //if (answersTrain.length != featuresTrain.length) println(k)
        //featuresTrain(k).foreach(println(_))
        //println(answersTrain(k))
        //println(featuresTrain.length)
        //println(answersTrain.length)
        j += 1
      }
      //println(s"j:${j}")
      i += 1
      println(s"i:${i}")
    }
  } catch {
    case e => println(e.toString)
  }
  println(featuresTrain.length)
  println(answersTrain.length)

  println("\nAdding features for training done")
  println(Calendar.getInstance().getTime())
  println()



  //////////////////////////////////////////////////////////////////////////////////////

  val logreg: LogisticRegression = logit(featuresTrain, answersTrain)
  println(Calendar.getInstance().getTime())
  println()
  val oos0 = new ObjectOutputStream(new FileOutputStream("/home/ivan/Desktop/logreg.out"))
  oos0.writeObject(logreg)



  val svmachine = svm[Array[Double]](featuresTrain, answersTrain, new LinearKernel(), 0.1)

  val oos1 = new ObjectOutputStream(new FileOutputStream("/home/ivan/Desktop/svm.out"))
  oos1.writeObject(svmachine)

  cv(featuresTrain, answersTrain, 2, new Accuracy, new Recall, new FMeasure) {case (x: Array[Array[Double]], y: Array[Int]) =>
    svm[Array[Double]](x, y, new LinearKernel(), 0.1)}

  //////////////////////////////////////////////////////////////////////////
  /*
  *
  * Validation

  */

 */

  val ois = new ObjectInputStream(new FileInputStream("src/main/resources/svm.out"))
  val svmmachine: SVM[Array[Double]] = ois.readObject.asInstanceOf[SVM[Array[Double]]]


  println(Calendar.getInstance().getTime())
  println()





  val articlesTest: Map[String, Seq[Article]] = articleSeqTest.groupBy(_.blockingKey)


  TestClassifier.test(svmmachine, articlesTest, answersSetTest, initFN, "SVM")

  //TestClassifier.test(svmmachine, articlesTest, answersSetTest, InitFN, "Logit")









  //plot(features.map{x => Array(x(0), x(1))}, answers, svmachine)


}
