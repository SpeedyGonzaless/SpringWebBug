package com.example.springwebbug

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

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