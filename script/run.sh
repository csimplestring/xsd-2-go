

 xsd=$1
 xjc -quiet $xsd
 cd ./generated
 javac *.java
 cd ..
 java -jar j2go.jar ./generated/ generated
