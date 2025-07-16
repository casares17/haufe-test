# haufe-test

# haufe-test

## Overview

This project is a Java application built with Spring Boot, developing a catalogue of beers from various manufacturers.

## Stack

- Java
- Spring Boot
- Spring Boot
- Spring Data JPA
- Spring Security
- H2 Database
- Maven
- Docker
- Kubernetes

## Beers and Manufacturers

- The application provides an API to manage beers and manufacturers.
- The `BeerController` allows creating, updating, deleting and retrieving/searching beers.
- The `ManufacturerController` allows creating, updating, deleting and retrieving manufacturers.

## Persistence

- The application uses an in-memory H2 database to store data. The database schema is created automatically based on the
  entity classes.
- The database is initialized with sample data using the `data.sql` script.
- Ids are generated automatically by the database, based on IDENTITY strategy.

## Search and Pagination

- The API supports search and pagination for beers.
- The `BeerController` provides a search endpoint to search beers by name, type, alcoholByVolume or manufacturer name.
- The results can be paginated using the `page` and `size` request parameters. Page 0 and size 10 are the default
  values.
- The results can be ordered by name, type, alcoholByVolume or manufacturer name. Sort by name and ascending order is
  the default.

## Security

- The API is secured with Spring Security. It uses Basic (username, password) Auth. The user credentials are hardcoded
  in the `SecurityConfig` class for simplicity.
- The API has two roles: `MANUFACTURER` and `ADMIN`. The `MANUFACTURER` role can create, update and delete its own data.
  The `ADMIN` role can access the edit everything. Anonymous users can access the read-only endpoints methods for beers
  and manufacturers.
- Two users are created for testing purposes. The `manufacturer` user has the `MANUFACTURER` role and the `admin` user
  has the `ADMIN` role.
- Further iteration could include JWT token for a more robust security implementation. That could allow an extra
  functionality where manufacturers could edit beers data that they own, but not the ones from other manufacturers.

## Deployment in Kubernetes

The app is dockerized and can be deployed in a Kubernetes cluster using Minikube. The deployment files are located in
the `devops/` directory. The following steps are required:

1. Start Minikube:
   ```sh
   minikube start
   ```
2. Use Minikube's Docker daemon:
    ```sh
    eval $(minikube docker-env)
    ```

3. Build the Docker image:
   ```sh
   docker build -t haufe-test:latest .
   ```

4. Apply the provided Kubernetes deployment (cleaning up any previous deployment):

   ```sh
    kubectl delete -f devops/
    kubectl apply -f devops/
    ```

5. Start the application:

    ```sh
    minikube service haufe-service
    ```

6. Access the application:

   Open your browser and navigate to the URL provided by the `minikube service` command, typically
   `http://<minikube-ip>:<port>`. You can check that the application is running by accessing the `/actuator/health`
   endpoint.

## Testing with Postman

A sample Postman collection is provided in the project. It contains some requests to test the API endpoints. It could be
extended and improved with more tests and scenarios.
