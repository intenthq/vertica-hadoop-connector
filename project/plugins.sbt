resolvers ++= Seq(
  "bigtoast-github" at "http://bigtoast.github.com/repo/",
  "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases"
)

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")