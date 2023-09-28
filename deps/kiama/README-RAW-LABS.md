TLDR: Just run the build.sh script.

Notes:
- Couldn't get it to build on Graal 21 JDK, so use a more classical one.
- Couldn't get kiama-extras to work but we don't need it.
- Added support for Automatic-Module-Name
- Publish both to Ivy and Maven.
- Added performance patches, which replaces files:
core/scala/org/bitbucket/inkytonik/kiama/relation/Tree.scala
core/scala/org/bitbucket/inkytonik/kiama/rewriting/Cloner.scala
