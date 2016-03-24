resolvers ++= Seq(
  Resolver.url("commbank-releases-ivy", new URL("http://commbank.artifactoryonline.com/commbank/ext-releases-local-ivy"))(Patterns("[organization]/[module]_[scalaVersion]_[sbtVersion]/[revision]/[artifact](-[classifier])-[revision].[ext]")),
  "commbank-releases" at "http://commbank.artifactoryonline.com/commbank/ext-releases-local"
)

val uniformVersion = "1.8.0-20160301041523-74946e2"

addSbtPlugin("au.com.cba.omnia" % "uniform-core"       % uniformVersion)

addSbtPlugin("au.com.cba.omnia" % "uniform-dependency" % uniformVersion)

addSbtPlugin("au.com.cba.omnia" % "uniform-thrift"     % uniformVersion)

addSbtPlugin("au.com.cba.omnia" % "uniform-assembly"   % uniformVersion)

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
