# --- STAGE 1: Build ---
# Start with the official JDK 25 image
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Install Maven manually
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven && \
    rm -rf /var/lib/apt/lists/*

# Root POM
COPY pom.xml .

# Backend
COPY backend/pom.xml ./backend/pom.xml
COPY backend/src ./backend/src

# Frontend
COPY frontend/pom.xml ./frontend/pom.xml

RUN pwd
RUN ls -lart
RUN mvn clean package -DskipTests
RUN ls -lart backend/target/

# --- STAGE 2: Runtime ---
# Debian/Ubuntu is required for Tess4J/JNA: Alpine uses musl, while the JNA
# native library bundled with Tess4J is built against glibc.
FROM eclipse-temurin:25-jre-resolute
WORKDIR /app

# Install native Tesseract and Leptonica libraries plus English traineddata.
# Tess4J/JNA looks for libtesseract.so and libleptonica.so, so expose the
# versioned Debian libraries under those names.
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        tesseract-ocr \
        tesseract-ocr-eng && \
    TESS_LIB="$(find /usr/lib -name 'libtesseract.so.*' -print -quit)" && \
    LEPT_LIB="$(find /usr/lib \( -name 'libleptonica.so.*' -o -name 'liblept.so.*' \) -print -quit)" && \
    test -n "$TESS_LIB" && \
    test -n "$LEPT_LIB" && \
    ln -sf "$TESS_LIB" /usr/lib/libtesseract.so && \
    ln -sf "$LEPT_LIB" /usr/lib/libleptonica.so && \
    ldconfig && \
    tesseract --version && \
    test -f /usr/share/tesseract-ocr/5/tessdata/eng.traineddata && \
    rm -rf /var/lib/apt/lists/*

# Tess4J reads the traineddata location from TESSDATA_PREFIX.
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata

RUN groupadd --system spring && \
    useradd --system --gid spring --create-home --shell /usr/sbin/nologin spring
USER spring:spring

COPY --from=build /app/backend/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-Djna.library.path=/usr/lib", "-jar", "app.jar"]