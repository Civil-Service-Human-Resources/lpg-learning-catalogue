FROM amazoncorretto:25-alpine

ENV SPRING_PROFILES_ACTIVE production

EXPOSE 9000

ADD environ/wait-for-it.sh .
ADD build/libs/learning-catalogue.jar /data/app.jar

# Add AppInsights config and agent jar
ADD lib/AI-Agent.xml /opt/appinsights/AI-Agent.xml
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.7.10/applicationinsights-agent-3.7.10.jar /opt/appinsights/applicationinsights-agent-3.7.10.jar

CMD java -javaagent:/opt/appinsights/applicationinsights-agent-3.7.10.jar -jar /data/app.jar
