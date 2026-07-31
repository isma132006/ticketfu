package com.ismael.ticketfu.dto.response;

import lombok.Data;

@Data
public class UserResponse {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private  Long id;
}
