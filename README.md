# Spring Boot To Postman

Expose an extra actuator capable to generate the Postman collection of your application.

## What is

Spring Boot To Postman is a library designed in order to work when imported in Spring Boot Applications.  
It is able to generate all the Postman collection, pre-generating a lot of data, for your application that you will be
able to import directly into your Postman as a collection using the latest standards.

---

## How it works

It takes care to automagically inspect all endpoints annotated with the `@RequestMapping` annotation, getting infos from
them (using reflection) and generating Postman's items.  
It exposes an extra actuator called `postman-dump`, all the content will be generated when you call this actuator and
will be presented as a JSON, you simply get that JSON and import it, as file or as raw string, in your client.

---

## How to import

### Maven

```xml

<dependencies>
    <dependency>
        <groupId>com.robertomanfreda</groupId>
        <artifactId>spring-boot-to-postman</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>

```

### Gradle

**TODO**

---

## Settings

You need to do some configurations:

```yaml

spring-boot-to-postman:
  enabled: true
  parent:
    package: 'com.example.exampleapplication' # Specify the package of your project

management:
  endpoints:
    web:
      exposure:
        include:
          - 'mappings'
          - 'postman-dump'

```

---

## Example RestController

```java

@RequestMapping("/controller")
@RestController
public class TestController {
    @PostmanItem
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity<?> test() {
        return new ResponseEntity<>("Hello from /test", HttpStatus.OK);
    }
}

```

---

# HINTS

- Do not use aliases of `@RequestMapping` such as `@GetMapping`, `@PostMapping` etc.  
  or the endpoints will be not scanned
- Enable the extra actuator `postman-dump` ONLY in your local/dev/test environment, do not use it in production
  environment
