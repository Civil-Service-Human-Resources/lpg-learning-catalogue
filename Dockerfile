FROM amazoncorretto:25-alpine

ENV SPRING_PROFILES_ACTIVE production

EXPOSE 9000

ADD environ/wait-for-it.sh .
ADD build/libs/learning-catalogue.jar /data/app.jar

ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.7.10/applicationinsights-agent-3.7.10.jar /opt/appinsights/applicationinsights-agent.jar

CMD java --enable-native-access=ALL-UNNAMED -javaagent:/opt/appinsights/applicationinsights-agent.jar -jar /data/app.jar
