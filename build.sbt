name := "Dedup-bycicle"

version := "0.1"

scalaVersion := "2.12.1"

//csv
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.6"

resolvers += "central" at "http://repo1.maven.org/maven2/"

// String distances
libraryDependencies += "com.github.vickumar1981" %% "stringdistance" % "1.1.2"