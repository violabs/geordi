# Geordi Test Framework

## Introduction
Geordi Test Framework is a Kotlin-based testing framework that leverages JUnit 5's `TestTemplate` 
feature for dynamic and parameterized testing. Designed for flexibility and ease of use, it supports file-based and 
parameter-based scenarios, making it ideal for a wide range of testing needs. Inspired by Spock, this framework
aims to provide a similar level of testing power and flexibility in a more lightweight and Kotlin-friendly package.

## Features
- 📁 **File-Based Testing:** Easily define tests that rely on file inputs and expected outputs.
- 🔢 **Parameter-Based Testing:** Create tests with varying parameters, including complex objects and data types.
- 🗂️ **Custom Scenario Management:** Utilize `SimulationGroup` for organized scenario handling.
- 🧪 **JUnit 5 Integration:** Seamlessly integrates with JUnit 5's advanced testing features.

## Getting Started

### Prerequisites
- Kotlin
- JUnit 5

### Installation
Clone the repository and include it in your project:
```
git clone https://github.com/your-repository/geordi-test-framework.git
```

### Setting Up Scenarios
Define your scenarios using `SimulationGroup`. Example:
```kotlin
private val FILE_BASED_SCENARIOS = SimulationGroup
    .vars("scenario", "scenarioFile", "expectedFile")
    .with("full", "full_scenario.txt", "full_expected.json")
    .with("partial", "partial_scenario.txt", "partial_expected.json")
```

### Writing Test Templates
Create test templates in your test classes using the `@TestTemplate` annotation. Example:
```kotlin
@TestTemplate
fun `show file based test`(scenarioFile: String, expectedFile: String) {
    // Test logic here
}
```

### Running Tests
Run your tests as you would with any standard JUnit 5 setup.

## Examples
See `UnitTestExample.kt` for detailed examples of file-based and parameter-based tests.

## License
This project is licensed under the MIT License - see the `LICENSE.md` file for details.

## Acknowledgments
- The JUnit 5 Team for their extensive and well-documented testing framework.
- The Spock Framework for their elegant and powerful testing framework.
- Mockk for their excellent mocking library.
- Mockito for their excellent mocking library.

## Road Map

- [ ] Java integration support (mostly there probably)
- [ ] Integration testing support
  - Spring Boot
  - Managed test scenario support (Docker)
- [ ] Asynchronous testing support

### Missing a feature?

If you have a feature request, please open an issue and we'll see what we can do!