package com.epam.jdi.httptests.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdName {
    public String Accept;

    @JsonProperty("Accept-Encoding")
    public String AcceptEncoding;

    @JsonProperty("Content-Type")
    public String ContentType;

    public String Host;
    public String Id;
    public String Name;

    @JsonProperty("User-Agent")
    public String UserAgent;
}
