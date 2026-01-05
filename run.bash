
echo "compile"
javac -classpath . animations/*.java backgrounds/*.java gui/*.java main/*.java sprites/*.java universes/*.java utilities/*.java backgrounds/*.java  -d ./bin

echo "run Main.java"
java -classpath ./bin Main
