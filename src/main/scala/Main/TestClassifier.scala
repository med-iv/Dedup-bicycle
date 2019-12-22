package Main

import Main_model.Article
import info.debatty.java.stringsimilarity.{Jaccard, NormalizedLevenshtein}
import smile.classification.Classifier
import org.graphframes.GraphFrame
import org.apache.tinkerpop.gremlin.structure.{Graph, VertexProperty}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource
import org.apache.tinkerpop.gremlin.process.computer.GraphFilter
import org.apache.tinkerpop.gremlin.process.computer.clustering.connected._
import org.apache.tinkerpop.gremlin.process.computer.traversal.step.map.ConnectedComponent
import gremlin.scala._
import org.janusgraph.core.JanusGraphFactory
import org.apache.tinkerpop.gremlin.structure.Column.values
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph

import scala.collection.JavaConverters._


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
    //try {
      for ((key, seq) <- articlesTest) {
        k += 1

        if (seq.length > 1) {
          println(s"${key} = ${k}", seq.length)
          val graph = TinkerGraph.open()
          val g = graph.traversal()
          for (i <- seq.indices) {

            g.addV("myvertex").property("number", i).next()
            //g.tx.commit()
          }


          for (i <- seq.indices) {
            for (j <- i + 1 until seq.length) {
              val feature = Array[Double](
                l.distance(seq(i).title, seq(j).title),
                jaccard.distance(seq(i).authors.mkString(","), seq(j).authors.mkString(",")),
                l.distance(seq(i).year, seq(j).year)
              )

              val answer: Int = if (answersSetTest.get(seq(i).id) == answersSetTest.get(seq(j).id)
                && answersSetTest.get(seq(i).id).isDefined) 1 else 0

              val v1 = g.V().has("myvertex", "number", i).head
              val v2 = g.V().has("myvertex", "number", j).head

              if (classifier.predict(feature) == 1) {
                val edge12 = g.addE("edge1").property("answer",
                  answer).from(v1).to(v2).next()
                //println(edge12)
                //g.tx.commit()
              } else {
                val edge12 = g.addE("edge0").property("answer",
                  answer).from(v1).to(v2).next()
                //println(edge12)
                println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG")
                //g.tx.commit()
              }

              if (answer == 0 && classifier.predict(feature) == 0) {
                TN_c += 1
                //println("TN_c", TN_c)
              } else if (answer == 0 && classifier.predict(feature) == 1) {
                FP_c += 1
                //println("FP_c", FP_c)
              } else if (answer == 1 && classifier.predict(feature) == 0) {
                FN_c += 1
                //println("FN_c", FN_c)
              } else {
                TP_c += 1
                //println("TP_c", TP_c)
              }
            }
          }

         /*
          val res = g.withComputer().V().outE().hasLabel("edge1").bothV().connectedComponent()
            //.`with`(ConnectedComponent.propertyName, "component")
            .dedup()
            .toList.asScala.toList

          */


          val res1 = g.withComputer().V().outE().hasLabel("edge1")
            .bothV().dedup()
            //.V().dedup
            .connectedComponent()
            .group().by(ConnectedComponent.component)
          var res2 = res1.select(values).unfold().toList.asInstanceOf[java.util.List[java.util.List[Vertex]]]
          println(res2)


          val verts = g.V().toList
          println(verts)
          val len = verts.size()
          for (i <- 0 until len) {
            var is_vert = 0
            val vert = verts.get(i)
            for (k1 <- 0 until res2.size()) {
              val res = res2.get(k1)
              for (i1 <- 0 until res.size()) {
                if (vert.id == res.get(i1).id){
                  is_vert = 1
                }
              }
            }
            if (is_vert == 0) {
              res2.add(java.util.Arrays.asList(vert))
            }
          }

          println(res2)

          //val comps: Map[VertexProperty[String], List[Vertex]]= res.groupBy(_.property(ConnectedComponent.propertyName))
          //println(comps)
          //if (comps.size > 1) {
            //println("Big size")
          //}



          for (k1 <- 0 until res2.size()) {
            val res = res2.get(k1)
            for (i1 <- 0 until res.size()) {
              for (j1 <- i1 + 1 until res.size()) {
                val edge: Edge = g.V().has("number", res.get(i1).value("number").asInstanceOf[Int])
                  .outE().as("ed")
                  .inV().has("number", res.get(j1).value("number").asInstanceOf[Int]).select("ed")
                  .headOption().asInstanceOf[Option[Edge]]
                  .getOrElse(g.V().has("number", res.get(j1).value("number").asInstanceOf[Int])
                    .outE().as("ed")
                    .inV().has("number", res.get(i1).value("number").asInstanceOf[Int]).select("ed")
                    .head().asInstanceOf[Edge])

                val ed_val: Int = edge.value("answer").asInstanceOf[Int]

                //println("ed_val", ed_val)
                if (ed_val == 0) {
                  FP_g += 1
                  println("FP_g", FP_g)
                } else if (ed_val == 1) {
                  TP_g += 1
                  println("TP_g", TP_g)
                }
              }
            }
          }

          for (i1 <- 0 until res2.size()) {
            val tmp1 = res2.get(i1)
            for (j1 <-  i1 + 1 until res2.size) {
              val tmp2 = res2.get(j1)
              for (i2 <-0 until tmp1.size()) {
                for (j2 <- 0 until tmp2.size()){
                  val edge: Edge = g.V().has("number", tmp1.get(i2).value("number").asInstanceOf[Int])
                    .outE().as("ed")
                    .inV().has("number", tmp2.get(j2).value("number").asInstanceOf[Int]).select("ed")
                    .headOption().asInstanceOf[Option[Edge]]
                    .getOrElse(g.V().has("number", tmp2.get(j2).value("number").asInstanceOf[Int])
                      .outE().as("ed")
                      .inV().has("number", tmp1.get(i2).value("number").asInstanceOf[Int]).select("ed")
                      .head().asInstanceOf[Edge])

                  val ed_val: Int = edge.value("answer").asInstanceOf[Int]
                  if (ed_val == 1) {
                    FN_g += 1
                    println("FN_g", FN_g)
                  } else if (ed_val == 0){
                    TN_g += 1
                    println("TN_g", TN_g)
                  }
                }
              }
            }
          }
          graph.close()
        }
      }
   // } catch {
     // case e: => println(e.toString)
    //}
    println(s"TP_c = ${TP_c}")
    println(s"FP_c = ${FP_c}")
    println(s"TN_c = ${TN_c}")
    println(s"FN_c = ${FN_c}")
    println(s"TP_g = ${TP_g}")
    println(s"FP_g = ${FP_g}")
    println(s"TN_g = ${TN_g}")
    println(s"FN_g = ${FN_g}")
    val prec_c = TP_c / (TP_c + FP_c)
    val rec_c = TP_c / (TP_c + FN_c)
    val F1_c = 2 * (prec_c * rec_c) / (prec_c + rec_c)
    println(s"precision_c = ${prec_c}")
    println(s"recall_c = ${rec_c}")
    println(s"F1_c = ${F1_c}")
    val prec_g = TP_g / (TP_g + FP_g)
    val rec_g = TP_g / (TP_g + FN_g)
    val F1_g = 2 * (prec_g * rec_g) / (prec_g + rec_g)
    println(s"precision_g = ${prec_g}")
    println(s"recall_g = ${rec_g}")
    println(s"F1_g = ${F1_g}")
  }

}
