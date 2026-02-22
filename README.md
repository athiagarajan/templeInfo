# Temple Info Extractor

A Spring Boot application designed to extract detailed information about temples from specified web pages using web scraping and metadata analysis.

## Features

- **Information Extraction**: Retrieves temple details such as:
  - Moolavar & Urchavar
  - Amman / Thayar
  - Thala Virutcham & Theertham
  - Agamam / Pooja
  - Historical data (Old year, Historical Name)
  - Location (City, District, State)
- **Web Scraping**: Utilizes Jsoup for robust HTML parsing and data extraction.
- **API Documentation**: Integrated Swagger UI for easy endpoint exploration.

## Tech Stack

- **Java**: 19
- **Framework**: Spring Boot 3.2.2
- **Build Tool**: Gradle
- **Libraries**:
  - `Jsoup`: For HTML parsing and scraping.
  - `SpringDoc OpenAPI`: For Swagger UI and API documentation.
  - `Vertex AI SDK`: (Included for future AI-enhanced extraction).

## Getting Started

### Prerequisites

- Java 19 or higher
- Gradle (optional, uses wrapper)

### Running the Application

1. Clone the repository.
2. Navigate to the `templeInfo` directory.
3. Run the application using the Gradle wrapper:

   ```powershell
   ./gradlew bootRun
   ```

The application will start on `http://localhost:8080`.

### API Endpoints

- **Extract Temple Info**: 
  - `POST /temple-info`
  - Body: `{ "url": "TEMPLE_PAGE_URL" }`
- **Swagger UI**:
  - `http://localhost:8080/swagger-ui/index.html`

## Configuration

- `src/main/resources/application.properties`: Contains logging and application settings.
- `.env`: Used for sensitive environment variables (e.g., GitHub tokens).

## Logging

Debug logs are enabled for:
- `com.example.templeinfo`
- `com.google.cloud.vertexai`
- `org.springframework.web`
