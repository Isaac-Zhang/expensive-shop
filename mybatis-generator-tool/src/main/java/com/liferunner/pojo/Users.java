package com.liferunner.pojo;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "users")
public class Users {
    @Column(name = "USER")
    private String user;

    @Column(name = "CURRENT_CONNECTIONS")
    private Long currentConnections;

    @Column(name = "TOTAL_CONNECTIONS")
    private Long totalConnections;
}