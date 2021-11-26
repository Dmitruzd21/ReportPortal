# CI - status:
[![Build status](https://ci.appveyor.com/api/projects/status/8rwbvi82ki847047?svg=true)](https://ci.appveyor.com/project/Dmitruzd21/reportportal)

## ***Руководство для установки и интеграции сервиса ReportPortal с Gradle - JUnit5 (ОС Windows)***

Рекомендуется параллельно следовать официальной инструкции от разработчиков и данным руководством

### **Предварительные действия**

Docker должен быть заранее установлен в вашей операционной системе.
Откройте Docker, перейдите в Settings, далее в Resouses, далее в Advanced. Убедитесь, что докеру отдается минимум 3 гб от ресурсов вашего компьютера (если настройка невозможна, то существует возможность изменить конфигурации в файле wclconfig).
Убедитесь, что установлены Docker Engine и Compose.

### **Основный действия**

### **1-й этап (установка ReportPortal):**

Перейдите на сайт reportportal.io.
Перейдите в раздел Install в шапке сайта.
Чтобы использовать сервис ReportPortal при помощи Docker (рекомендуется) нажмите на кнопку Deploy with Docker.

Создайте в вашем проекте в IDEA папку reportportal.

В папке reportportal создайте файл docker-compose.yml и вставьте в него конфигурации для скачивания и запуска контейнеров, необходимых для работы сервиса. 

Конфигурации для docker-compose.yml находятся по ссылке: https://github.com/reportportal/reportportal/blob/master/docker-compose.yml

Следующим этапом необходимо перед загрузкой контейнеров задать конфигурации для ElasticSearch:

Откройте консоль в IDEA и введите команду, которая позволит работать с папкой reportportal: 

`cd reportportal`

Далее введите команду:

`wsl -d docker-desktop
sysctl -w vm.max_map_count=262144`

Далее необходимо задать особые разрешения для некоторых файлов.

Введите в консоль:

`mkdir -p data/elasticsearch`

Можете убедиться, что папке reportportal появилась папка data, а в ней папка elasticsearch.

Затем выполните в терминале команду:

` chmod 777 data/elasticsearch`

Далее выполните терминале команду: 

`chgrp 1000 data/elasticsearch`

В новом терминале (в пределах вашего проекта) выполните команду перехода в папку reportportal:`cd reportportal`, а затем для начала загрузки контейнеров:  `docker-compose -p reportportal up -d --force-recreate`

Ожидайте скачивание и запуск контейнеров. Это может занять около 30 мин (в зависимости от скорости вашего интернета).

После того как произошла установка всех контейнеров в конце вы увидите в терминале примерно следующие логи (см. ниже).

![1](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/ReportPortal%20–%20docker-compose.yml%20%5BReportPortal%5D%202.1.png)

### **2-й этап (открытие ReportPortal):**

Далее откройте любой браузер и в поисковой строке введите следующий URL: http://IP_ADDRESS:8080.

Должна открыться следующая страница:

![785](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/Report%20Portal%20-%20Google%20Chrome%202021-11-25%2013.51.43.1.png)



Если этого не произошло, то введите URL: http://localhost:8080

Введите логин: superadmin. Введите пароль: erebus

Примечание: В последующим рекомендуется заменить пароль для обеспечения безопасности.

Нажмите на кнопку Login, вид страницы представлен на скриншоте:

![457](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/Report%20Portal%20-%20Google%20Chrome%202021-11-25%2015.16.59.2.png)

### **3-й этап (интеграция с тестовым фреймворком)**

Выберете язык Java и тестовый фреймворк JUnit5 на сайте https://reportportal.io/installation.

Вы окажитесь на сайте: https://github.com/reportportal/agent-java-junit5 , где будут даны дальнейшие инструкции.

Примечание: В данном руководстве представлена интеграция ReportPortal в готовый проект. Кроме того, данное руководство по интеграции значительно отличается от инструкции разработчиков.

В папке resources создайте директорий:  /META-INF/services

Добавьте в этот директорий файл с названием: org.junit.jupiter.api.extension.Extension.
Этот файл должен содержать всего одну строку:
`com.epam.reportportal.junit5.ReportPortalExtension`

**Сделайте изменения в build.gradle:**

В раздел "репозитории" дополнительно внесите:

`mavenLocal()
maven { url "http://dl.bintray.com/epam/reportportal" }
`

Добавьте следующие зависимости:

    testCompile 'com.epam.reportportal:agent-java-junit5:5.1.0'
    implementation 'com.epam.reportportal:agent-java-junit5:5.0.0'
    implementation 'com.epam.reportportal:logger-java-logback:5.0.2'
    implementation 'ch.qos.logback:logback-classic:1.2.3'
    implementation 'com.epam.reportportal:logger-java-log4j:5.0.2'
    compileOnly 'log4j:log4j:1.2.17'
    implementation 'org.apache.logging.log4j:log4j-api:2.13.3'
    implementation 'org.apache.logging.log4j:log4j-core:2.13.3'

В разделе test добавьте:

`testLogging.showStandardStreams = true`

`systemProperty 'junit.jupiter.extensions.autodetection.enabled', true`

Добавьте тест с logging.
Для этого создайте тестовый класс LoggingUtils, поместите его папку util и добавьте в него следующий код:

```
package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LoggingUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger("binary_data_logger");

    private LoggingUtils() {
        //statics only
    }

    public static void log(File file, String message) {
        LOGGER.info("RP_MESSAGE#FILE#{}#{}", file.getAbsolutePath(), message);
    }
}
```


В папке resources cоздайте файл log4j2.xml и добавьте в него следующую информацию:

```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout
                    pattern="%d [%t] %-5level %logger{36} - %msg%n%throwable"/>
        </Console>
        <ReportPortalLog4j2Appender name="ReportPortalAppender">
            <PatternLayout
                    pattern="%d [%t] %-5level %logger{36} - %msg%n%throwable"/>
        </ReportPortalLog4j2Appender>
    </Appenders>
    <Loggers>
        <Root level="DEBUG">
            <AppenderRef ref="ConsoleAppender"/>
            <AppenderRef ref="ReportPortalAppender"/>
        </Root>
    </Loggers>
</Configuration>
```


В папке resourced cоздайте файл logback.xml и поместите в него следующую информацию:

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- Send debug messages to System.out -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- By default, encoders are assigned the type ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level %logger{5} - %thread - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="RP" class="com.epam.reportportal.logback.appender.ReportPortalAppender">
        <encoder>
            <!--Best practice: don't put time and logging level to the final message. Appender do this for you-->
            <pattern>%d{HH:mm:ss.SSS} [%t] %-5level - %msg%n</pattern>
            <pattern>[%t] - %msg%n</pattern>
        </encoder>
    </appender>

    <!--'additivity' flag is important! Without it logback will double-log log messages-->
    <logger name="binary_data_logger" level="TRACE" additivity="false">
        <appender-ref ref="RP"/>
    </logger>

    <logger name="com.epam.reportportal.service" level="WARN"/>
    <logger name="com.epam.reportportal.utils" level="WARN"/>

    <!-- By default, the level of the root level is set to DEBUG -->
    <root level="TRACE">
        <appender-ref ref="RP"/>
        <appender-ref ref="STDOUT"/>
    </root>

</configuration>
```


Перейдите в браузер со страницей ReportPortal. Нажмите в списке опций Administrate.

![89](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/Report%20Portal%20-%20Google%20Chrome%202021-11-25%2018.02.17.rhhj.png)

Нажмите Add New Project.

![532](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/Report%20Portal%20-%20Google%20Chrome%202021-11-25%2018.05.42.turu.png)

Дайте название проекту, например, ReportPortal.

![44](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/Report%20Portal%20-%20Google%20Chrome%202021-11-25%2016.13.13.grg.png)

Оставьте настройки по умолчанию и нажмите Submit.

Далее снова нажмите на иконку и выберите Profile.

![67](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/Report%20Portal%20-%20Google%20Chrome%202021-11-25%2018.29.58.u6i.png)

Далее в папке resources создайте файл с названием reportportal.properties и скопируйте в него содержание блока CONFIGURATION EXAMPLES (Java).

![34](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/Report%20Portal%20-%20Google%20Chrome%202021-11-25%2018.44.48.hjrt.png)

**Включение расширения ReportPortal в тесты**

В папке util cоздайте класс ScreenShooterReportPortalExtension.java и поместите в него следующий код:


```
package util;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.screenshot;
import static com.codeborne.selenide.WebDriverRunner.driver;

/**
* Use this class to automatically take screenshots in case of ANY errors in tests (not only Selenide errors) and send them to ReportPortal.
*
* @see com.codeborne.selenide.junit5.ScreenShooterExtension
  */
  public class ScreenShooterReportPortalExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private static final Logger log = LoggerFactory.getLogger(ScreenShooterReportPortalExtension.class);

  private final boolean captureSuccessfulTests;

  public ScreenShooterReportPortalExtension() {
  this(false);
  }

  public ScreenShooterReportPortalExtension(final boolean captureSuccessfulTests) {
  this.captureSuccessfulTests = captureSuccessfulTests;
  }

  @Override
  public void beforeTestExecution(final ExtensionContext context) {
  final Optional<Class<?>> testClass = context.getTestClass();
  final String className = testClass.map(Class::getName).orElse("EmptyClass");

       final Optional<Method> testMethod = context.getTestMethod();
       final String methodName = testMethod.map(Method::getName).orElse("emptyMethod");

       Screenshots.startContext(className, methodName);
  }

  @Override
  public void afterTestExecution(final ExtensionContext context) {
  if (captureSuccessfulTests) {
  log.info(screenshot(context.getTestMethod().toString()));
  } else {
  context.getExecutionException().ifPresent(error -> {
  if (!(error instanceof UIAssertionError)) {
  File screenshot = ScreenShotLaboratory.getInstance().takeScreenShotAsFile(driver());
  if (screenshot != null) {
  LoggingUtils.log(screenshot, "Attached screenshot");
  }
  }
  });
  }
  Screenshots.finishContext();
  }
  }
```

Теперь перейдите в класс, в котором непосредственно находятся ваши тесты, и перед публичным классом укажите аннотацию, которая позволит сделать тест отслеживаемым ReportPortal при помощи ранее созданного класса ScreenShooterReportPortalExtension.java:

`@ExtendWith({ScreenShooterReportPortalExtension.class})`

Пример того, как может выглядеть структура вашего проекта, представлен на изображении ниже:

![4668](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/7664876.png)
![873](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/6907544.png)

Добавьте импорты тот же класс:

`import org.junit.jupiter.api.extension.ExtendWith;
import ru.netology.util.ScreenShooterReportPortalExtension;`

Теперь запустите ваши тесты и перейдите в сервис ReportPortal.

Нажмите launches на левой боковой панеле. Результаты ваших тестов теперь отображаются в системе ReportPortal. Поздравляем! 

![44](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/Report%20Portal%20-%20Google%20Chrome%202021-11-26%2021.28.59.1.png)

![45](https://github.com/Dmitruzd21/ReportPortal/blob/master/photoReportPortal/Report%20Portal%20-%20Google%20Chrome%202021-11-26%2021.31.53.2.png)
