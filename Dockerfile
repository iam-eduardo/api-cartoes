# Estágio de compilação
FROM eclipse-temurin:22-jdk-alpine AS build
WORKDIR /workspace/app

# Copiar arquivos de projeto Maven
COPY pom.xml .
COPY src src

# Compilar o projeto
RUN apk add --no-cache maven && \
    mvn package -DskipTests && \
    mkdir -p target/dependency && \
    (cd target/dependency; jar -xf ../*.jar)

# Estágio de execução
FROM eclipse-temurin:22-jre-alpine
VOLUME /tmp

# Copiar os arquivos compilados da etapa de build
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Definir o ponto de entrada
ENTRYPOINT ["java","-cp","app:app/lib/*","com.cartoes.api_cartoes.ApiCartoesApplication"]