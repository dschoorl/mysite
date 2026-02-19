# Run `mvn clean package` before building the docker image 

FROM eclipse-temurin:21-noble

EXPOSE 9080

RUN mkdir -p /var/mysite/{logs,sites}

# create static page for localhost site
COPY sample_site/ /var/mysite/sites

# Provide correct values through the -e command line option and/or the -v option with docker container run 
# in order to define the static web sites you want to serve 
ENV MYSITE_LOG_DIR="/var/mysite/logs" \
    MYSITE_DATA_DIR="/var/mysite/sites"

WORKDIR /usr/lib/mysite

COPY webapp/target/mysite.war .

CMD java -jar mysite.war