# Project Guidelines

## General Guidelines
* Ensure when adding new libraries, they are alphabetized under their sections, and check the sections to ensure they are alphabetized as well.
* Always try to use the latest version of any library being added.
* Follow the existing project architecture and patterns when adding new features.
* Write unit tests for all new functionality.
* Update documentation when making significant changes.
* Do not use deprecated methods.

## Project Structure
The project follows a clean architecture approach with the following main components:
* Follow MVVM architecture pattern
* Write clean code, follow the SOLID principles, and adhere to the Clean Architecture principles

### Data Layer
* `data/db`: Contains Room database implementation, DAOs, and converters
* `data/model`: Contains data models/entities
* `data/preferences`: Contains preferences implementation for app settings
* `data/repository`: Contains repositories that provide data access to the rest of the app
* When modifying any database entity, ensure any required data migration is implemented to ensure compatibility when users upgrade

### UI Layer
* `ui/main`: Main screen for displaying and managing links
* `ui/settings`: Settings screen for app configuration
* `ui/theme`: Theme-related components and configurations

### Other Components
* `di`: Dependency injection modules and application class
* `navigation`: Navigation setup and screen definitions
* `utils`: Utility classes and helper functions

## Coding Standards
* Follow Kotlin coding conventions
* Use ktlint for code formatting (configured in the project)
* Use meaningful variable and function names
* Write clear comments for complex logic
* Keep functions small and focused on a single responsibility
* Use sealed classes for UI states
* Use Flow and StateFlow for reactive programming

## Testing Guidelines
* Write unit tests for ViewModels, repositories, and utility classes
* Write instrumentation tests for database operations
* Use MockK or Mockito for mocking dependencies in tests
* Follow the AAA (Arrange-Act-Assert) pattern in test methods

## Build Process
* The project uses Gradle with Kotlin DSL for build configuration
* Dependencies are managed in the `libs.versions.toml` file
* The project has separate debug and release build configurations

## Git Workflow
* Create feature branches for new features
* Create bugfix branches for bug fixes
* Use descriptive commit messages
* Squash commits before merging to main branch
* Keep pull requests focused on a single feature or fix

## Dependency Management
* All dependencies should be declared in the `libs.versions.toml` file
* Group related dependencies together
* Keep dependencies up to date, but ensure compatibility
* Avoid adding unnecessary dependencies

## Performance Considerations
* Use coroutines for asynchronous operations
* Use Room's Flow API for database operations
* Optimize UI rendering by avoiding unnecessary recompositions
* Use proper scoping for Hilt dependencies

## Security Guidelines
* Don't store sensitive information in plain text
* Use secure storage for credentials if needed
* Validate user input to prevent injection attacks
* Follow Android security best practices

## Accessibility Guidelines
* Ensure all UI elements have content descriptions
* Support dynamic text sizes
* Ensure sufficient color contrast
* Test with accessibility services like TalkBack

## Release Process
* Update version code and version name in build.gradle.kts
* Run all tests before release
* Generate signed APK/AAB for release
* Test the release build on multiple devices before publishing
