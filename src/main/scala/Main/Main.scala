package Main

import com.github.tototoshi.csv._
import java.io._

import Main_model.Article
import smile.classification.{Classifier, LogisticRegression, SVM}
import smile.math.kernel.LinearKernel
import info.debatty.java.stringsimilarity.{Cosine, Jaccard, JaroWinkler, Levenshtein, NormalizedLevenshtein}
import smile.classification.{adaboost, cart, gbm, lda, logit, randomForest, svm}
import smile.validation.{Accuracy, FMeasure, Recall, cv}
import smile.plot.plot
import java.util.Calendar

object Main extends App {
  //System.setOut(new PrintStream(new FileOutputStream("/home/ivan/Desktop/file.out")))

  println("\nRunning...")
  println(Calendar.getInstance().getTime())
  println()

  var featuresTrain: Array[Array[Double]] = Array()
  var answersTrain: Array[Int] = Array()

  val (acmTrain: Map[String, Article], dblp2Train: Map[String, Article],
       answersSetTrain: Map[String, String], articleSeqPreTrain: Seq[Article],
        featuresTrain1: Array[Array[Double]], answersTrain1: Array[Int]) = GetDataset.getACM()


  val (dblp1Test: Map[String, Article], scholarTest: Map[String, Article],
  answersSetTest: Map[String, String], articleSeqTest: Seq[Article], initFN: Int) = GetDataset.getScholar()

  println(s"initFN = ${initFN}")

  println("\nLoading done")
  println(Calendar.getInstance().getTime())
  println()



  val l = new Levenshtein()
  val jaccard = new Jaccard()
  val jarowink = new JaroWinkler()



  try {
    var i: Int = 0
    for ((key1, article1) <- acmTrain) {
      for ((key2, article2) <- dblp2Train) {
        featuresTrain :+= Array[Double](
        //l.distance(article1.title, article2.title),
         // l.distance(article1.authors.sorted.mkString(","), article2.authors.sorted.mkString(",")),
          l.distance(article1.year, article2.year),
          //l.distance(article1.venue, article2.venue),
          jaccard.distance(article1.authors.mkString(","), article2.authors.mkString(",")),
          //jarowink.distance(article1.title, article2.title),
          jarowink.distance(article1.authors.sorted.mkString(","), article2.authors.sorted.mkString(",")),
          jarowink.distance(article1.year, article2.year)
          //jarowink.distance(article1.venue, article2.venue)
        )
        answersTrain :+= {if (answersSetTrain.get(key1).isDefined && answersSetTrain(key1) == key2) 1 else 0}
      }
    }
  } catch {
    case e => println(e.toString)
  }

  println(featuresTrain.length)
  println(answersTrain.length)

  println("\nAdding features for training done")
  println(Calendar.getInstance().getTime())
  println()


/*  val logreg: LogisticRegression = logit(featuresTrain, answersTrain)
  println("Logreg training done")
  println(Calendar.getInstance().getTime())
  println()


  val oos0 = new ObjectOutputStream(new FileOutputStream("/home/ivan/Desktop/logreg.out"))
  oos0.writeObject(logreg)
*/

/*
  val svmachine = svm[Array[Double]](featuresTrain, answersTrain, new LinearKernel(), 0.1)
  println("SVM training done")
  println(Calendar.getInstance().getTime())
  println()

   val oos1 = new ObjectOutputStream(new FileOutputStream("/home/ivan/Desktop/svm.out"))
   oos1.writeObject(svmachine)

*/

  //cv(featuresTrain, answersTrain, 2, new Accuracy, new Recall, new FMeasure) {case (x: Array[Array[Double]], y: Array[Int]) =>
    //svm[Array[Double]](x, y, new LinearKernel(), 0.1)}



  //val ois0 = new ObjectInputStream(new FileInputStream("src/main/resources/svm.out"))
  //val svmachine: SVM[Array[Double]] = ois0.readObject.asInstanceOf[SVM[Array[Double]]]

  //val ois1 = new ObjectInputStream(new FileInputStream("src/main/resources/logreg.out"))
  //val logreg: LogisticRegression = ois1.readObject.asInstanceOf[LogisticRegression]

  val am_trees = 2100

  val forest =  randomForest(featuresTrain, answersTrain, ntrees = am_trees)

  println("Random forest training done")
  println(Calendar.getInstance().getTime())
  println()
  val oos2 = new ObjectOutputStream(new FileOutputStream("/home/ivan/Desktop/forest.out"))
  oos2.writeObject(forest)
 



  val articlesTest: Map[String, Seq[Article]] = articleSeqTest.groupBy(_.blockingKey)

  //TestClassifier.test(logreg, articlesTest, answersSetTest, initFN, "Logreg")
  //println(Calendar.getInstance().getTime())
  //println()

  TestClassifier.test(forest, articlesTest, answersSetTest, initFN, "forest")
  println(Calendar.getInstance().getTime())
  println()


  //TestClassifier.test(svmachine, articlesTest, answersSetTest, initFN, "SVM")
  //println(Calendar.getInstance().getTime())
  //println()


  val ada = adaboost(featuresTrain, answersTrain, ntrees = am_trees)

  println("Ada training done")
  println(Calendar.getInstance().getTime())
  println()

  TestClassifier.test(ada, articlesTest, answersSetTest, initFN, "ada")
  println(Calendar.getInstance().getTime())
  println()

    val gbt = gbm(featuresTrain, answersTrain, ntrees = am_trees)

  println("gbt training done")
  println(Calendar.getInstance().getTime())
  println()

  TestClassifier.test(gbt, articlesTest, answersSetTest, initFN, "gbt")
  println(Calendar.getInstance().getTime())
  println()



  //plot(features.map{x => Array(x(0), x(1))}, answers, svmachine)


}
