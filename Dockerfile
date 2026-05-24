FROM amazoncorretto:17
ENV TZ=Asia/Seoul
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

WORKDIR /apps

RUN mkdir -p /apps/logs \
    && mkdir -p /apps/logs/archived \
    && chmod -R 777 /apps/logs

COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
