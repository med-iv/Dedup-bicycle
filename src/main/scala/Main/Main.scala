package Main

import com.github.tototoshi.csv._
import java.io._

import Main_model.Article
import smile.classification.{Classifier, LogisticRegression, SVM}
import smile.math.kernel.LinearKernel


import info.debatty.java.stringsimilarity.{Levenshtein, Jaccard, Cosine, NormalizedLevenshtein}
import smile.classification.{svm, lda, cart, logit, randomForest}
import smile.validation.{cv, FMeasure, Accuracy, Recall}
import smile.plot.plot




import java.util.Calendar

object Main extends App {
  //System.setOut(new PrintStream(new FileOutputStream("/home/ivan/Desktop/file.out")))

  println("\nRunning...")
  println(Calendar.getInstance().getTime())
  println()


  val (//acmTrain: Map[String, Article], dblp2Train: Map[String, Article],
       //answersSetTrain: Map[String, String], articleSeqPreTrain: Seq[Article]
        featuresTrain: Array[Array[Double]], answersTrain: Array[Int]) = GetDataset.getACM()


  val (dblp1Test: Map[String, Article], scholarTest: Map[String, Article],
  answersSetTest: Map[String, String], articleSeqTest: Seq[Article], initFN: Int) = GetDataset.getScholar()

  println(s"initFN = ${initFN}")

  println("\nLoading done")
  println(Calendar.getInstance().getTime())
  println()







/*
  try {
    var i: Int = 0
    for ((key1, article1) <- acmTrain) {
      for ((key2, article2) <- dblp2Train) {
        featuresTrain :+= Array[Double](
          l.distance(article1.title, article2.title),
          jaccard.distance(article1.authors.mkString(","), article2.authors.mkString(",")),
          l.distance(article1.year, article2.year)
        )
        answersTrain :+= {if (answersSetTrain.get(key1).isDefined && answersSetTrain(key1) == key2) 1 else 0}
      }
    }
  } catch {
    case e => println(e.toString)
  }
  */
  println(featuresTrain.length)
  println(answersTrain.length)
/*
  println("\nAdding features for training done")
  println(Calendar.getInstance().getTime())
  println()
*/

/*  val logreg: LogisticRegression = logit(featuresTrain, answersTrain)
  println("Logreg training done")
  println(Calendar.getInstance().getTime())
  println()


  val oos0 = new ObjectOutputStream(new FileOutputStream("/home/ivan/Desktop/logreg.out"))
  oos0.writeObject(logreg)
*/


  val svmachine = svm[Array[Double]](featuresTrain, answersTrain, new LinearKernel(), 0.1)
  println("SVM training done")
  println(Calendar.getInstance().getTime())
  println()

   val oos1 = new ObjectOutputStream(new FileOutputStream("/home/ivan/Desktop/svm.out"))
   oos1.writeObject(svmachine)



  //cv(featuresTrain, answersTrain, 2, new Accuracy, new Recall, new FMeasure) {case (x: Array[Array[Double]], y: Array[Int]) =>
    //svm[Array[Double]](x, y, new LinearKernel(), 0.1)}



  //val ois0 = new ObjectInputStream(new FileInputStream("src/main/resources/svm.out"))
  //val svmachine: SVM[Array[Double]] = ois0.readObject.asInstanceOf[SVM[Array[Double]]]

  //val ois1 = new ObjectInputStream(new FileInputStream("src/main/resources/logreg.out"))
  //val logreg: LogisticRegression = ois1.readObject.asInstanceOf[LogisticRegression]


  val forest =  randomForest(featuresTrain, answersTrain, ntrees = 70)

  println("Random forest training done")
  println(Calendar.getInstance().getTime())
  println()
  val oos2 = new ObjectOutputStream(new FileOutputStream("/home/ivan/Desktop/forest.out"))
  oos2.writeObject(forest)
 



  val articlesTest: Map[String, Seq[Article]] = articleSeqTest.groupBy(_.blockingKey)

  //TestClassifier.test(logreg, articlesTest, answersSetTest, initFN, "Logreg")
  //println(Calendar.getInstance().getTime())
  //println()

  //TestClassifier.test(svmachine, articlesTest, answersSetTest, initFN, "SVM")
  //println(Calendar.getInstance().getTime())
  //println()





  TestClassifier.test(forest, articlesTest, answersSetTest, initFN, "forest")
  println(Calendar.getInstance().getTime())
  println()







  //plot(features.map{x => Array(x(0), x(1))}, answers, svmachine)


}
