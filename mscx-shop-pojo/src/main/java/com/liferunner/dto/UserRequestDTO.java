package com.liferunner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserRequestDTO for : 用户DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDTO {
    private String username;
    private String password;
    private String confimPassword;
}
