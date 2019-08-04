# Exercise2

## Brief

- Developed & tested with JDK 12.
- `Spark` for light and easy REST.
- `SLF4j` as `Spark` expects SLF4j implementation provided.
- `JUnit` and `Mockito` for unit & integration tests.
- IoC & DI done manually through constructors, due to very small amount of classes to deal with (~4 injections). 

## Configuration
* Listening HTTP port can be configured in `config.properties` file, located in `src/main/resources`.
* Also a default (initial) balance of new accounts can be defined in the same configuration file.

## Running

### With maven
1. Clone repository
2. `cd Exercise2`
3. `mvn`

### With java
1. Clone repository
2. `cd Exercise2`
3. `mvn package`
4. `cd target`
5. `java -jar exercise2-1.0.0-jar-with-dependencies.jar`

## Running tests

1. `mvn test`

## REST API

- GET http://localhost:8000/account
- GET http://localhost:8000/account/{number}
- DELETE http://localhost:8000/account/{number}
- POST http://localhost:8000/payment

### Payment request body schema

    {
      "$schema": "http://json-schema.org/draft-04/schema#",
      "type": "object",
      "properties": {
        "sourceAccount": {
          "type": "string"
        },
        "targetAccount": {
          "type": "string"
        },
        "amount": {
          "type": "number"
        }
      },
      "required": [
        "sourceAccount",
        "targetAccount",
        "amount"
      ]
    }

Example:

    {
	    "sourceAccount": "3333333333",
	    "targetAccount": "2222222222",
	    "amount": 1.34
    }

There's also a Postman examples file (you can import it in Postman) provided in the `postman` directory.

