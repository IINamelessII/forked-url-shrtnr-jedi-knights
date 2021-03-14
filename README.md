# Jedi-Knights - URL shortener {🧪}

## Students group

- Голіней Андрій a.holinei@gmail.com
- Гурняк Андрій MrHurniak@gmail.com
- Курінський Вадим kurinskijvadim@gmail.com
- Макарчук Вадим vm29v07@gmail.com
- Обух Василь vasiaobukh7@gmail.com

## Design document

The [design document](https://docs.google.com/document/d/1ZcmFHWav7F9b_4gvjhD-IwysB07OhW4KYkA5R5uDIZo/edit) that
describes architecture and implementation details of this project.

### Swagger links

* [Swagger link](http://localhost:8080/swagger/shorten-url-service.yml)
* [Swagger UI](http://localhost:8080/swagger-ui/index.html)
### Main scenario endpoints

1. Sign up

```shell
curl -X 'POST' \
  'http://localhost:8080/users/signup' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "email": "aaa@example.com",
  "password": "passw000rd"
}'
```

2. Login

```shell
curl -X 'POST' \
  'http://localhost:8080/users/signin' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "email": "aaa@example.com",
  "password": "passw000rd"
}'
```
Example output

```shell
{
    "token": "<token>"
}
```

3. Create shorten URL

```shell
curl -X 'POST' \
  'http://localhost:8080/urls/shorten' \
  -H 'accept: application/json' \
  -H 'Authorization: <TOKEN FROM THE LOGIN RESPONSE>' \
  -H 'Content-Type: application/json' \
  -d '{
  "alias": "palevo",
  "uri": "https://github.com/future-stardust/url-shrtnr-palevo"
}'
```

4. Redirect

```shell
curl -X 'GET' \
  'http://localhost:8080/r/palevo' \
  -H 'accept: application/json'
```

5. List of user’s shortened links

```shell
curl -X 'GET' \
  'http://localhost:8080/urls' \
  -H 'accept: application/json' \
  -H 'Authorization:  <TOKEN FROM THE LOGIN RESPONSE>'
```
Example output

```shell
{
  "urls": [
    {
      "alias": "palevo",
      "original_url": "https://github.com/future-stardust/url-shrtnr-palevo"
    }
  ]
}
```
6. Delete shortened link

```shell
curl -X 'DELETE' \
  'http://localhost:8080/urls/palevo' \
  -H 'accept: application/json' \
  -H 'Authorization:  <TOKEN FROM THE LOGIN RESPONSE>'
```

## Environment prerequisites

### Java
This is a Java project, so you will need an environment with installed [JDK] 15. For installation,
you could use:
- [sdkman] on Linux/MacOS

### IDE
As IDE use [IntelliJ Idea Edu].

### Checkstyle
We use [checkstyle] to ensure coding standards. To get real-time detection in IDE you could use [Checkstyle-IDEA]
plugin. We use Google rules (local copy `./config/checkstyle/checkstyle.xml`).

## How to start development

1. Clone this repo
2. Open the project directory in IntelliJ Idea Edu
3. Configure IDE code style settings

1. Open `Settings`
2. Go to `Editor` -> `Code Style` -> `Import Scheme`
   ![Settings screenshot](./media/code-style-import.png)
3. Import scheme from `./config/idea/intellij-java-google-style.xml`
