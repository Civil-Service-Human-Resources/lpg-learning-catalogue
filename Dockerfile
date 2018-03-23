FROM java:8

ENV SPRING_PROFILES_ACTIVE production

ENV AUTH_USER user
ENV AUTH_PASSWORD password

ENV ELASTICSEARCH_URI http://127.0.0.1:9200
ENV ELASTICSEARCH_USER elastic
ENV ELASTICSEARCH_PASSWORD changeme

EXPOSE 9000

CMD java -jar /data/app.jar

ADD environ/wait-for-it.sh .
ADD build/libs/learning-catalogue.jar /data/app.jar
