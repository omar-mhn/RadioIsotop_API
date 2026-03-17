package com.projecte.radioisotopo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Configuration
public class FhirConfig {
    
    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    @Bean
    public IParser fhirParser(FhirContext fhirContext) {
        return fhirContext.newJsonParser().setPrettyPrint(true);
    }
}