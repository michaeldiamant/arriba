java -Xmx2g \
  -Xms512M \
  -Xss4M \
  -XX:MaxPermSize=512M \
  -jar sbt-launch.jar \
  "$@"
