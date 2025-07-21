# CosmicDoc Common Library

## Overview
The CosmicDoc Common Library provides shared components, models, and utilities used across all CosmicDoc microservices.

## Components

### Models
- User models (Patient, Doctor, Staff)
- Medical records
- Appointments
- Prescriptions
- Payments
- Inventory items

### DTOs
- Request/Response objects
- Data transfer objects for API communication
- Validation objects

### Repositories
- Base repository implementations
- Firestore data access layer
- Common database operations

### Utilities
- Date/Time utilities
- Validation helpers
- Security utilities
- Common constants

## Usage

### Maven Dependency
Add this to your pom.xml:
```xml
<dependency>
    <groupId>com.cosmicdoc</groupId>
    <artifactId>cosmicdoc-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

### Example Usage
```java
// Using models
import com.cosmicdoc.common.model.Patient;
import com.cosmicdoc.common.model.Appointment;

// Using repositories
import com.cosmicdoc.common.repository.PatientRepository;
import com.cosmicdoc.common.repository.AppointmentRepository;

// Using DTOs
import com.cosmicdoc.common.dto.AppointmentRequest;
import com.cosmicdoc.common.dto.PatientResponse;
```

## Development

### Building
```bash
mvn clean install
```

### Testing
```bash
mvn test
```

## Package Structure
```
com.cosmicdoc.common
├── model/          # Domain models
├── dto/            # Data Transfer Objects
├── repository/     # Repository interfaces
├── repository/impl # Repository implementations
├── util/          # Utility classes
└── response/      # Common API responses
```

## Contributing
1. Create a feature branch
2. Make your changes
3. Run tests
4. Submit a pull request

## Dependencies
- Spring Boot 3.2.0
- Google Cloud Firestore
- Lombok
- Jakarta Validation API

## Version History
- 1.0.0: Initial release
  - Base models and repositories
  - Common utilities
  - Firestore integration

Please read the main project's [Contributing Guide](../../docs/CONTRIBUTING.md) for more details.
