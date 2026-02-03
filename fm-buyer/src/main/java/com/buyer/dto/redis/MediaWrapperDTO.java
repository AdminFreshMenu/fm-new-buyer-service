package com.buyer.dto.redis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaWrapperDTO {

    @JsonProperty("MOBILE")
    private MediaDTO mobile;

    @JsonProperty("LARGE")
    private MediaDTO large;

    @JsonProperty("ORIGINAL")
    private MediaDTO original;

    @JsonProperty("SMALL")
    private MediaDTO small;

    @JsonProperty("MEDIUM")
    private MediaDTO medium;
}
