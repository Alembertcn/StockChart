toLocal=${1:-"false"}
if [ "$toLocal" == "true" ]; then
  ./gradlew :lib:publishToMavenLocal && ./gradlew :module_chart:publishToMavenLocal
else
  ./gradlew :lib:publish && ./gradlew :module_chart:publish
fi
