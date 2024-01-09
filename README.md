# Geordi
The Next Generation of Testing Framework

![push](https://github.com/violabs/geordi/actions/workflows/push.yml/badge.svg?branch=main)

## Introduction
Geordi Test Framework is a Kotlin-based testing framework that leverages JUnit 5's `TestTemplate` 
feature for dynamic and parameterized testing. Designed for flexibility and ease of use, it supports file-based and 
parameter-based scenarios, making it ideal for a wide range of testing needs. Inspired by Spock, this framework
aims to provide a similar level of testing power and flexibility in a more lightweight and Kotlin-friendly package.

## Features
- 🚀 **Dynamic Test Creation:** Generate multiple test cases from compact scenario definitions.
- 📁 **File-Based Testing:** Easily define tests that rely on file inputs and expected outputs.
- 🔢 **Parameter-Based Testing:** Create tests with varying parameters, including complex objects and data types.
- 🗂️ **Custom Scenario Management:** Utilize `SimulationGroup` for organized scenario handling.
- 🧪 **JUnit 5 Integration:** Seamlessly integrates with JUnit 5's advanced testing features.

## Getting Started

### Prerequisites
- Kotlin (or Java)
- JUnit 5

### Installation
Will be in maven soon.

### Setting Up Scenarios
Define your scenarios using `SimulationGroup`. Example:
```kotlin
private val FILE_BASED_SCENARIOS = SimulationGroup
    .vars("scenario", "scenarioFile",         "expectedFile")
    .with("full",     "full_scenario.txt",    "full_expected.json")
    .with("partial",  "partial_scenario.txt", "partial_expected.json")
```

In order for the scenarios to get picked up by the framework, you have to do a small bit of setup in your test class.

```kotlin
companion object {
    @JvmStatic
    @BeforeAll
    fun setup() = setup<UnitTestExample>(
        FILE_BASED_SCENARIOS to { ::`show file based test` },
        PARAMETER_BASED_SCENARIOS to { ::`show parameter based test` }
    )
}
```
Add this companion object which uses the SimulationGroups you want to test, as well as the test function
it should use. The test function should be a function that takes the same number of parameters as the number of
variables in the SimulationGroup. The parameters should be in the same order as the variables in the SimulationGroup.

> The `{ ::show file based test }` format basically provides a function where the parameter is an instance
> of this class. That gives access to the available functions on that class. It is the same as 
> `{ this::show file based test }`

### Writing Test Templates
Create test templates in your test classes using the `@TestTemplate` annotation. Example:
```kotlin
@TestTemplate
fun `show file based test`(scenarioFile: String, expectedFile: String) {
    // Test logic here
}
```

### Using UnitSim
UnitSim is a utility class that provides a number of useful functions for testing.

At a high level, it has functionality to test individual methods, which also have pre-made
functions belonging to them. This can be used either with or without TestTemplates. It allows for mocking
utilizing the Mockk library.

Full documentation will be available at geordi.violabs.io soon.

```kotlin
class Example : UnitSim() {
    @Test
    fun `test method`() = test {
        expect { 2 }
      
        whenever { 1 + 1 }
    }
}
```

### Running Tests
Run your tests as you would with any standard JUnit 5 setup.

## Examples
See `UnitTestExample.kt` for detailed examples of file-based and parameter-based tests.

## License
This project is licensed under the Apache 2.0 License - see the `LICENSE.md` file for details.

## Acknowledgments
- The JUnit 5 Team for their extensive and well-documented testing framework.
- The Spock Framework for their elegant and powerful testing framework.
- Mockk for their excellent mocking library.
- Mockito for their excellent mocking library.

## Road Map

- [ ] Setup Maven Central publishing
- [ ] Java integration support (mostly there probably)
- [ ] Integration testing support
  - Spring Boot
  - Managed test scenario support (Docker)
- [ ] Asynchronous testing support

### Missing a feature?

If you have a feature request, please open an issue and we'll see what we can do!