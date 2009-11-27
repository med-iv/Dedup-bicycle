package Main_model

case class Article(id: String,
                   title: String,
                   authors: Seq[String],
                   venue: String,
                   year: String,
                   blockingKey: String)

case class Pair (title_dist: Double,
                  authors_dist: Double,
                  venue_dist: Double,
                  year_dist: Double)