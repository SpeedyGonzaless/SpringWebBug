# SpringWebBug
Bug with Jackson2ObjectMapper: method canRead return true, but method read fails

# TLDR
the bug is in MappingJackson2HttpMessageConverter. For ByteArray method canRead() returns true, but method read() fails.

# Research

If you add MappingJackson2HttpMessageConverter to list of HttpMessageConverters with your hands 
(for example  with extending WebMvcConfigurationSupport
and overriding configureMessageConverters)
```
@Configuration
class ConverterConfiguration(
    private var builder: Jackson2ObjectMapperBuilder,
) : WebMvcConfigurationSupport() {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
        converters.add(converter())
        addDefaultHttpMessageConverters(converters)
    }

    @Bean
    fun converter(): MappingJackson2HttpMessageConverter? {
        val objectMapper = builder.build<ObjectMapper>()
        return MappingJackson2HttpMessageConverter(objectMapper)
    }

}
```
so the MappingJackson2HttpMessageConverter will be at the begging of list:
![image](https://user-images.githubusercontent.com/40829475/191683472-bbf7a979-839c-4fcb-9067-2873a629a351.png)


Then when you will try to make http request with ByteArray in body
```
@PostMapping
fun countries(
    @RequestBody bytes: ByteArray
): String {
    return "Success"
}
```
![image](https://user-images.githubusercontent.com/40829475/191684217-44112444-4640-46ec-92ab-78503b2a213d.png)

Your application will fail with exception
```
2022-09-22 09:25:21.914  WARN 48389 --- [nio-8083-exec-2] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `[B` from Object value (token `JsonToken.START_OBJECT`); nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value of type `[B` from Object value (token `JsonToken.START_OBJECT`)<EOL> at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 1, column: 1]]
```

The reason is in class AbstractMessageConverterMethodArgumentResolver, method readWithMessageConverters in this code:
```
for (HttpMessageConverter<?> converter : this.messageConverters) {
  Class<HttpMessageConverter<?>> converterType = (Class<HttpMessageConverter<?>>) converter.getClass();
  GenericHttpMessageConverter<?> genericConverter =
      (converter instanceof GenericHttpMessageConverter ? (GenericHttpMessageConverter<?>) converter : null);
  if (genericConverter != null ? genericConverter.canRead(targetType, contextClass, contentType) :
      (targetClass != null && converter.canRead(targetClass, contentType))) {
    if (message.hasBody()) {
      HttpInputMessage msgToUse =
          getAdvice().beforeBodyRead(message, parameter, targetType, converterType);
      body = (genericConverter != null ? genericConverter.read(targetType, contextClass, msgToUse) :
          ((HttpMessageConverter<T>) converter).read(targetClass, msgToUse));
      body = getAdvice().afterBodyRead(body, msgToUse, parameter, targetType, converterType);
    }
    else {
      body = getAdvice().handleEmptyBody(null, message, parameter, targetType, converterType);
    }
    break;
  }
}
```
As MappingJackson2HttpMessageConverter is first in the list we get it, check with the method canRead(), it returns  true and then we get exception when call wethod read().

But if we remove this code
```
override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
    converters.add(converter())
    addDefaultHttpMessageConverters(converters)
}
```
then ByteArrayHttpMessageConverter would be first in list
![image](https://user-images.githubusercontent.com/40829475/191686736-317db916-a7c0-484f-b006-07e73a6cbd25.png)
and everything works fine
![image](https://user-images.githubusercontent.com/40829475/191686821-168c877a-8dc7-4527-ac07-bcb257fd3768.png)

# Result:
the bug is in MappingJackson2HttpMessageConverter. For ByteArray method canRead() returns true, but method read() fails.

