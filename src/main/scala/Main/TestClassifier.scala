package Main

import Main_model.Article
import info.debatty.java.stringsimilarity.{Jaccard, NormalizedLevenshtein}
import smile.classification.Classifier

import org.graphframes.GraphFrame
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource

import org.apache.tinkerpop.gremlin.process
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource
import org.apache.tinkerpop.gremlin.process.computer.GraphFilter
import org.apache.tinkerpop.gremlin.process.computer.clustering.connected._
import org.apache.tinkerpop.gremlin.process.computer.traversal.step.map.ConnectedComponent
import gremlin.scala._
import org.janusgraph.core.JanusGraphFactory


object TestClassifier {
  val l = new NormalizedLevenshtein()
  val jaccard = new Jaccard()

  def test(classifier: Classifier[Array[Double]], articlesTest: Map[String, Seq[Article]], answersSetTest: Map[String, String],
           Init_FN:Int, name: String) =
  {
    var TP_c: Int = 0
    var FP_c: Int = 0
    var TN_c: Int = 0
    var FN_c: Int = Init_FN

    var TP_g: Int = 0
    var FP_g: Int = 0
    var TN_g: Int = 0
    var FN_g: Int = Init_FN

    println(name)

    var k = 0

    for ((key, seq) <- articlesTest) {
      println(s"k = ${k}", seq.length)
      k += 1

      implicit val graph = JanusGraphFactory.open("inmemory")
      val g = graph.traversal.withComputer()
      for (i <- seq.indices) {
        g.addV(i.toString)
      }
      g.tx.commit()
      var featuresBlock: Array[Array[Double]] = Array()
      var answersBlock: Array[Int] = Array()
      for (i <- seq.indices) {
        for(j <- i + 1 until seq.length) {
          println(i, j)
          val feature = Array[Double](
            l.distance(seq(i).title, seq(j).title),
            jaccard.distance(seq(i).authors.mkString(","), seq(j).authors.mkString(",")),
            l.distance(seq(i).year, seq(j).year)
          )

          val answer = if (answersSetTest.get(seq(i).id) == answersSetTest.get(seq(j).id)
            && answersSetTest.get(seq(i).id).isDefined) 1 else 0

          val v1 = g.V().hasLabel(i.toString).head
          val v2 = g.V().hasLabel(j.toString).head

          val edge12 = g.addE("edge").from(v1).to(v2).
            property(Key[Int]("correctAnswer"), answer).
            property(Key[Int]("Answer"), classifier.predict(feature))

          if (answer == 0 && classifier.predict(feature) == 0) {
            TN_c += 1
          } else if (answer == 0 && classifier.predict(feature) == 1) {
            FP_c += 1
          } else if (answer == 1 && classifier.predict(feature) == 0) {
            FN_c += 1
          } else if (answer == 1 && classifier.predict(feature) == 1) {
            TP_c += 1
          }
        }
      }
      g.tx.commit()

      import scala.collection.JavaConverters._
      val res = g.V().connectedComponent().
        `with`(ConnectedComponent.propertyName, "component")
      .toList.asScala.toList


      for (elem <- res) println(elem.valueMap)

    }
  }

}
