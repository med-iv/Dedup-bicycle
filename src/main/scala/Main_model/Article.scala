package Main_model

case class Article( id: String,
                    title: String,
                    authors: Seq[String],
                    year: String,
                    blockingKey: String,
                    venue: String)
/* case class Article(
                    id: Id,
                    title: String,
                    authors: Seq[Scientist],
                    abstractText: Option[String] = None,
                    link: Option[String] = None,
                    year: Option[Int] = None)
*/