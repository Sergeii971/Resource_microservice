package com.os.course.util.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AuthorizationHeader {
    private String authorizationHeader;
}
