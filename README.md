# FreeResearch4j

![Build Status](https://github.com/ice09/freeresearch4j/actions/workflows/main.yml/badge.svg)

**FreeResearch4j** is a Spring Boot application that uses modern AI technologies, particularly [LangChain4j](https://github.com/langchain4j/langchain4j) and [GraalVM](https://www.graalvm.org/), to provide dynamic, multi-step workflows integrating Java, JavaScript execution, and function calling for data retrieval. 

This application is a showcase for advanced integration of AI tools such as OpenAI's GPT models, Mistral AI for OCR, Google Search integration, and dynamic JavaScript execution capabilities through GraalVM.

<img src="docs/img/freeresearch4j.png" width="300"/>

## Features

- **Spring Boot Application**: Spring Boot is used for serving the services and the static HTML and for integration of all services.
- **Langchain4j Integration**: Seamless integration of AI models including OpenAI GPT and Mistral AI.
- **Dynamic Tool Integration**:
    - **Google Custom Search**: Integrated via the official Google Custom Search JSON API.
    - **OCR with Mistral AI**: Extract text from PDFs for processing and analysis.
    - **JavaScript Execution via GraalVM**: Execute dynamic JavaScript workflows seamlessly within Java using GraalVM.
    - **Web Crawling**: Fetch and process live content directly from the web using JSoup.
- **Chat Interface**: Interactive chat functionality supporting file uploads and markdown-rendered responses.

## Getting Started

### Prerequisites

- Java 24 (GraalVM for JS integration, specified in `.sdkmanrc`)
- Gradle (provided by wrapper)

## Installation

Clone the repository and navigate to the directory:

```bash
git clone <repository-url>
cd freeresearch4j
```

Set your API keys in `src/main/resources/application.properties`:

```properties
openai.apikey=YOUR_OPENAI_API_KEY
mistralai.key=YOUR_MISTRAL_API_KEY
google.search.apikey=YOUR_GOOGLE_SEARCH_API_KEY
google.search.cxkey=YOUR_GOOGLE_CSE_KEY
```

## Run the JS server
This project aims to use GraalVM for dynamic JS execution, however for the additional tooling an external JS process is currently used.
This will be deprecated in next versions.

To setup the externally running process, see [README.md](node/README.md).

## Build and Run

Ensure GraalVM and JDK 24 are installed (managed via SDKMAN or directly):

```bash
sdk env
```

Build and run the application:

```bash
./gradlew bootRun
```

Access the interactive chat interface:

```
http://localhost:8080
```

## Project Structure

- `src/main/java`: Core application code.
    - `config`: Spring Boot and Langchain4j configuration.
    - `controller`: REST controllers for chat interactions.
    - `tools`: Custom integrations (OCR, JavaScript execution, Web crawling, Google search).
- `src/main/resources`: Application configuration and properties.
- `build.gradle.kts`: Gradle build configuration.

## Usage

### Chat and Workflow Examples

- **Ask general questions**: The assistant will use Google Search and web crawling for real-time answers.
- **Upload PDF**: Extract text via OCR for further interaction.
- **Dynamic JavaScript execution**: Perform complex workflows directly through interactive chat queries.

## License

Apache License 2.0

