package Main

import Main_model.Article
import info.debatty.java.stringsimilarity.{Jaccard, NormalizedLevenshtein}
import smile.classification.Classifier

import org.graphframes.GraphFrame

object TestClassifier {
  val l = new NormalizedLevenshtein()
  val jaccard = new Jaccard()

  def test(classifier: Classifier[Array[Double]], articlesTest: Map[String, Seq[Article]], answersSetTest: Map[String, String],
           Init_FN:Int, name: String) =
  {
    val TP_c: Int = 0
    val FP_c: Int = 0
    val TN_c: Int = 0
    val FN_c: Int = Init_FN

    val TP_g: Int = 0
    val FP_g: Int = 0
    val TN_g: Int = 0
    val FN_g: Int = Init_FN


    val TP_l: Int = 0
    val FP_l: Int = 0
    val TN_l: Int = 0
    val FN_l: Int = Init_FN
    println(name)



    for ((key, seq) <- articlesTest) {
      var featuresBlock: Array[Array[Double]] = Array()
      var answersBlock: Array[Int] = Array()
      for (i <- seq.indices) {
        for(j <- i + 1 until seq.length) {
          //println(i, j)
          featuresBlock :+= Array[Double](
            l.distance(seq(i).title, seq(j).title),
            jaccard.distance(seq(i).authors.mkString(","), seq(j).authors.mkString(",")),
            l.distance(seq(i).year, seq(j).year)
          )

          answersBlock :+= (if (answersSetTest.get(seq(i).id) == answersSetTest.get(seq(j).id)
            && answersSetTest.get(seq(i).id).isDefined) 1 else 0)


        }
      }
    }
  }

}
