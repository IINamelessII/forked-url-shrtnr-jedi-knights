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

## Environment prerequisites

### Java
This is a Java project, so you will need an environment with installed [JDK] 15. For installation,
you could use:
- [sdkman] on Linux/MacOS
- [AdoptOpenJDK] on Windows

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
