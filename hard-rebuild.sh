cd utils
rm -rf target/
sbt clean publishLocal

cd ../client
rm -rf target/
sbt clean publishLocal

cd ../snapi-frontend
rm -rf target/
sbt clean publishLocal

cd ../snapi-truffle
rm -rf target/
sbt clean publishLocal

cd ../snapi-client
rm -rf target/
sbt clean publishLocal
