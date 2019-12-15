name := "Dedup-bycicle"

version := "0.1"

scalaVersion := "2.11.11"


resolvers += "central" at "http://repo1.maven.org/maven2/"

//csv
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.6"

// String distances
//libraryDependencies += "com.github.vickumar1981" %% "stringdistance" % "1.1.2"
libraryDependencies += "info.debatty" % "java-string-similarity" % "1.2.1"

//config
libraryDependencies += "com.typesafe" % "config" % "1.4.0"

//smile - Machine learning
libraryDependencies += "com.github.haifengl" %% "smile-scala" % "1.5.3"

resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"

libraryDependencies += "graphframes" % "graphframes" % "0.7.0-spark2.4-s_2.11"

libraryDependencies += "com.michaelpollmeier" %% "gremlin-scala" % "3.4.4.2"

libraryDependencies += "org.janusgraph" % "janusgraph-core" % "0.2.0"