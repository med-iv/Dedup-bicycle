package Main_model

case class Article( id: String,
                    title: String,
                    authors: Seq[String],
                    venue: String,
                    year: String,
                    blockingKey: String)
