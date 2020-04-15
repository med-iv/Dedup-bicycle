package Main

import com.github.tototoshi.csv._
import java.io._

import smile.classification.{Classifier, GradientTreeBoost}
import smile.classification.gbm
import smile.validation.{Accuracy, FMeasure, Recall, cv}

import com.github.tototoshi.csv.CSVReader

object Main extends App {
  
  val dblpReader = CSVReader.open(new File("DBLP_features.csv"))
  val (featureNamesDblp, dblpFeatures) = dblpReader.allWithOrderedHeaders()

  val coraReader = CSVReader.open(new File("cora_features.csv"))
  val (featureNamesCora, coraFeatures) = coraReader.allWithOrderedHeaders()

  assert(featureNamesDblp == featureNamesCora)

  val dataset: List[Map[String, String]] = dblpFeatures ::: coraFeatures

  val x = dataset.map(row => 
                      Array(row("edit_authors").toDouble,
                            row("edit_venue").toDouble,
                            row("edit_year").toDouble,
                            row("jaro_winkler_authors").toDouble,
                            row("jaccard_authors").toDouble,
                            row("jaccard_venue").toDouble,
                            row("jaccard_year").toDouble,
                            row("diff_year").toDouble)).toArray

  val y = dataset.map(row => row("answer").toInt).toArray

  val f1 = new FMeasure()
  val res = cv(x, y, 5, f1){case (x, y) => gbm(x, y, ntrees =  250, shrinkage = 0.3)}
  
  
  val gbt = gbm(x, y, ntrees = 250, shrinkage = 0.3)
  val oos = new ObjectOutputStream(new FileOutputStream("/home/ivan/Dedup-bycicle/gbt.out"))
  oos.writeObject(gbt)
}