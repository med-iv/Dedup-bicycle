name := "Dedup-bycicle"

version := "0.1"

scalaVersion := "2.12.10"


resolvers += "central" at "http://repo1.maven.org/maven2/"

//csv
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.6"

// String distances
//libraryDependencies += "com.github.vickumar1981" %% "stringdistance" % "1.1.2"
//libraryDependencies += "info.debatty" % "java-string-similarity" % "1.2.1"


//smile - Machine learning
libraryDependencies += "com.github.haifengl" %% "smile-scala" % "1.5.3"

resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"
