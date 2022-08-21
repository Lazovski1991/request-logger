# **Библиотека логирования запросов и ответов**
Для логирования запросов и ответов можно просто подлючить к проекту и настроить файл yml проекта(необязательно). ЯП котлин, основана на OncePerRequestFilter spring. Версия spring 2.4.8.

### Подлючение к проекту
1. Добавить репозиторий в проект. Пример для maven:
````
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
````
Для gradle:
````
allprojects {
	repositories {
	...
	maven { url 'https://jitpack.io' }
	}
}
````
2. Добавить зависомость(уточнять последнюю версию). Пример для Maven:
````
<dependency>
	 <groupId>com.github.Lazovski1991</groupId>
	 <artifactId>request-logger</artifactId>
	 <version>2.1.0</version>
</dependency>
````

Для gradle:
````
dependencies {
   implementation 'com.github.Lazovski1991:request-logger:2.2.0'
}
```` 
### Настройка yml
Пример:
```
logging:
  config: classpath:logback-spring.xml
  service:
    enable: true
    auth:
        type: jwt
        token-header-name: testHeader
        field-name-token: ["userId", "userName", "email"]
    url-exclude: ["/tests"]
    file-part-type: ["application/octet-stream", "image/jpeg"]
    enable-log-stacktrace: true
    length-stacktrace: 1500
    enable-log-request: true
    enable-log-response: true

jwt:
  parse:
    service:
      secret-key: best-protection-service-developed-by-the-best-Java-backend-developer
```
* logging.config - указывает на файл xml с настройками логирования logback
* logging.enable - включение логирования запросов и ответоа(по умолчанию true)
* logging.auth.type - два типа, keycloak и jwt(если не заполнить будет jwt)
* logging.auth.token-header-name - заголовок в котором приходит токет авторизации(необязательно)
* logging.auth.field-name-token - поля которые необходимо достать из токена(необязательно)(тип keycloak пока умеет только три поля ["userId", "username", "email"])
* logging.url-exclude - исключает эти url из логирования
* logging.file-part-type - тип файлов, при загрузке названия которых выводить в логи(по умолчанию это "image/jpeg", "image/png", "image/jpg")
* logging.enable-log-request - включает логирование запросов(по умолчанию true)
* logging.enable-log-response - включает логирование ответов(по умолчанию true)

Отдельно стоит упомянуть про jwt.parse.service.secret-key. В эту библиотеку включена минибиблиотека для парсинга джейсона и извлечения из него информации. Это необходимо для того чтобы в логи можно было вывести какую-нибудь информацию о user (и не только). Если задано logging.token-header-name и field-name-token то необходимо задать секретный ключ в этой настройке, иначе в логи будет сыпаться исключение с просьбой его добавить. Если нет необходимость извлекать какие-то поля из джейсона, эти поля необязательны. Для типа keycloak это поле не нужно
### Логирование стектрейса
Для логирования стектрейса необходимо в ExceptionHandler перед тем как отдать ответь, вызвать метод stackTraceLog статического класса LogUtil и передать в качестве параметра исключение и длину стектрейса который мы хотим что залогировался. Второй параметр не обязательный, по умолчанию длина лога 10000 символов.

Пример:
```
    @ExceptionHandler(value = [BathDBException::class])
    protected fun handleDBExc(ex: BathDBException, request: WebRequest?): ResponseEntity<Any?>? {
        LogUtil.stackTraceLog(ex, 5000)
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
```

### Прочее
Для того чтобы логировать профиль при локальном запуске, необходимо задать профиль в файле pom.xml или build.gradle:
```
bootRun {
    systemProperty 'spring.profiles.active', 'dev'`
}
```
Для того чтобы логировать имя приложения в yml необходимо добавить информацию, пример:
```
spring:
  application:
    name: web-test
```
При исключении url из логирования, можно пользоваться символом * или **:
```
/test логируются все кроме /test
/test/* логируются все кроме /test и /test/something, такие будут /test/test/something
/test/** логируются все, кроме тех которые начинаются с /test
```
В библиотеке есть стандартная настройка файла xml logback. Для его использования можно включить эти настройки в свой файл. Пример:
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <include resource="my.company/logback-default.xml"/>
</configuration>
```
### Логирование в ELK
При логировании в elastick по умолчанию увидим следующие индексы:
* Device_id (из заголовка Device-Id запроса)
* File_Name_Upload имена загружаемых файлов
* Method запроса и ответа
* Params запроса
* Pod_Name который обработал запроса
* Request_Body тело запроса 
* Request_Duration продолжительность обработки запроса
* Request_Id уникальный id запроса
* Request_Uri запроса
* Headers запроса(кроме заголовка в котором токен, т.к. он есть отдельно)
* Response_Status ответа
* Token аутентификации
* User_Agent запроса
* так же другие поля, которые мы может достать из токена(например сведения о пользователе(как это делать выше))
### Примеры логов
Пример лога запроса:
```
04:47:50.492 [] [] [http-nio-8090-exec-2] INFO  REQUEST -
------------------------------>
APPLICATION: web-test
REQUEST-ID: 12111a32-4817-4f02-9b1a-0f8b680e38ca
METHOD: POST
URI: /test
USER_AGENT: PostmanRuntime/7.28.4
DEVICE-ID: unknown
AUTH-TOKEN: unknown
TOKEN_INFO: unknown
HEADERS: [{content-type=[application/json]}, {user-agent=[PostmanRuntime/7.28.4]}, {accept=[*/*]}, {postman-token=[9a08d28c-d3e9-4905-bcb6-c347782ed95d]}, {host=[localhost:8090]}, {accept-encoding=[gzip, deflate, br]}, {connection=[keep-alive]}, {content-length=[51]}]
PARAMS: []
FILE_NAME_UPLOAD: []
REQUEST-IP: 0:0:0:0:0:0:0:1
PROFILE: dev
TIME: 2022-01-30T04:47:50.492850415
BODY:
{
"field1": "field1",
"field2": "field2"
}
<------------------------------>
```
Пример ответа:
```
04:47:50.493 [] [] [http-nio-8090-exec-2] INFO  RESPONSE - 
------------------------------>>>
APPLICATION: web-test
REQUEST-ID: 12111a32-4817-4f02-9b1a-0f8b680e38ca
METHOD: POST
URI: /test
DURATION_REQUEST: 87 ms
TOKEN_INFO: unknown
HEADERS: []
PROFILE: dev
TIME: 2022-01-30T04:47:50.493306008
BODY: 
<------------------------------>
```
Запрос с извлеченной информацией из токена:
```
04:53:18.942 [] [] [http-nio-8090-exec-2] INFO  REQUEST - 
------------------------------>
APPLICATION: web-test
REQUEST-ID: f398d8a4-5076-4d93-8aa2-21ecc4e3d80f
METHOD: POST
URI: /test/token
USER_AGENT: PostmanRuntime/7.28.4
DEVICE-ID: unknown
AUTH-TOKEN: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGV4IiwiZW1haWwiOiJMYXpvdnNraTE5OTFAZ21haWwuY29tIiwicm9sZSI6IlJPTEVfQkFTSUMiLCJ1c2VySWQiOiIyNzc5ODFjNy01Njg0LTQ5ODYtOTRlNy1iNTFjZjM3YzdlMTIiLCJ1c2VybmFtZSI6ImFsZXgiLCJ0eXBlVG9rZW4iOiJ0b2tlbiIsImV4cCI6MTY0MzUwODA4Mn0.4gOs77QLdb4THnCgzzBZ91zfN_9lfuUQJfp_W2p79EkKiXi9ilJIlmIPDPlHDCKRH8gKaVkEyAZUsBoxyWPauA
TOKEN_INFO: {
	userId = 277981c7-5684-4986-94e7-b51cf37c7e12,
	username = alex,
	email = Lazovski1991@gmail.com,
}
HEADERS: [{testheader=[eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGV4IiwiZW1haWwiOiJMYXpvdnNraTE5OTFAZ21haWwuY29tIiwicm9sZSI6IlJPTEVfQkFTSUMiLCJ1c2VySWQiOiIyNzc5ODFjNy01Njg0LTQ5ODYtOTRlNy1iNTFjZjM3YzdlMTIiLCJ1c2VybmFtZSI6ImFsZXgiLCJ0eXBlVG9rZW4iOiJ0b2tlbiIsImV4cCI6MTY0MzUwODA4Mn0.4gOs77QLdb4THnCgzzBZ91zfN_9lfuUQJfp_W2p79EkKiXi9ilJIlmIPDPlHDCKRH8gKaVkEyAZUsBoxyWPauA]}, {user-agent=[PostmanRuntime/7.28.4]}, {accept=[*/*]}, {postman-token=[784efd0e-c1e7-4f6a-bb4c-aa006ca40457]}, {host=[localhost:8090]}, {accept-encoding=[gzip, deflate, br]}, {connection=[keep-alive]}, {content-length=[0]}]
PARAMS: [{field=email}]
FILE_NAME_UPLOAD: []
REQUEST-IP: 0:0:0:0:0:0:0:1
PROFILE: dev
TIME: 2022-01-30T04:53:18.942140246
BODY: 
<------------------------------>
```