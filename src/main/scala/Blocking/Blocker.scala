package Blocking

import Main_model.Article

import info.debatty.java.stringsimilarity.NGram

trait Blocker {
  def createBlocks(articles: Seq[Article]): Map[String, Seq[Article]]
}


// Training Selection for Tuning Entity Matching
// Запись попадает не только в один блок
class Blocker_trigram extends Blocker {

  override def createBlocks(articles: Seq[Article]) : Map[String, Seq[Article]] = {
    //val trigram = new NGram(3)
    //
    Map("" -> Seq())
  }
}

class Blocker_standart extends Blocker {
  override def createBlocks(articles: Seq[Article]) : Map[String, Seq[Article]] = {
    articles.groupBy(_.blockingKey)
  }
}