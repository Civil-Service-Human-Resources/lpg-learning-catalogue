FROM java:8

ENV SPRING_PROFILES_ACTIVE production

ENV AUTH_USER user
ENV AUTH_PASSWORD password

ENV ELASTICSEARCH_CLUSTER local
ENV ELASTICSEARCH_HOST 127.0.0.1
ENV ELASTICSEARCH_PORT 9300

EXPOSE 9000

CMD java -jar /data/app.jar

ADD environ/wait-for-it.sh .
ADD build/libs/learning-catalogue.jar /data/app.jar
