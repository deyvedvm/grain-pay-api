# Grain Pay API

Grain Pay API is a Spring Boot application designed to manage expenses. It provides endpoints for creating, retrieving, updating, and deleting expense records.

## Installation and Setup

Ensure you have Java 11 or newer and Maven installed on your system.

1. Clone the repository:

```bash
git clone https://github.com/yourusername/grain-pay-api.git
```

2. Navigate to the project directory:
    
```bash
cd grain-pay-api
```
3. Build the project:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

## Usage

After starting the application, you can access the REST API endpoints at `http://localhost:8080/`.

Example: Retrieve all expenses

```bash
curl -X GET http://localhost:8080/expenses
```

## Configuration

The application can be configured via the `application-dev.yml` file for development purposes. Ensure to set the environment variables `POSTGRES_DATASOURCE_URL`, `POSTGRES_DATASOURCE_USERNAME`, `POSTGRES_DATASOURCE_PASSWORD`, and `PORT` before running the application.

## Features

- CRUD operations for expenses
- Pagination and sorting for listing expenses
- Validation of expense entries
- Logging of API requests and responses

## Contributing

We welcome contributions! Please open an issue or submit a pull request for any enhancements, bug fixes, or features.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For any questions or contributions, please contact [deyvedev@gmail.com](deyvedev@gmail.com) or open an issue on GitHub.

## Additional Tools

- [DataDog Dashboard](https://app.datadoghq.com/dashboard/lists)
- [Bonsai Elasticsearch](https://app.bonsai.io/clusters)

## TODO

- [X] Add tests
- [X] Add documentation
- [ ] Add CI/CD
- [ ] Add Swagger for API documentation
- [x] Add ControllerAdvice for global exception handling
- [ ] Implement Authentication with JWT
- [ ] Integrate Keycloak for identity and access management
- [X] Containerize the application with Docker
- [ ] Orchestrate the application with Kubernetes
- [ ] Monitor the application with Grafana and Prometheus
- [ ] Log management with ELK Stack
- [ ] Implement event-driven architecture with Kafka
- [ ] Implement CQRS and Event Sourcing

