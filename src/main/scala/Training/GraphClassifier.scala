package Training

import Main_model.Article
import smile.classification.Classifier
import smile.validation.cv




class GraphClassifier extends Classifier[Array[Double]] {
  override def predict(x: Array[Double]): Int = 1


}


class GraphClassifierClaster extends Classifier[Array[Double]] {
  override def predict(x: Array[Double]): Int = 1
}

object CV {
  def myCv(classifier: Classifier[Array[Double]], k: Int, articles: Map[String, Seq[Article]]) = {

  }
}
