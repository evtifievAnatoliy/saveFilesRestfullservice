package com.quantori.restfullservice.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.Entity;

@Configuration
@PropertySource("classpath:data.properties")
public class DataProperties {

    @Getter
    @Value("${saveLocal}")
    private final Boolean SAVELOCAL = null;

    @Getter
    @Value("${directory.path}")
    private final String PATH = null;



}
