package com.liferunner.dto;

import lombok.*;

/**
 * UserAddressRequestDTO for : 用户地址DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserAddressRequestDTO {

    private String addressId;
    private String userId;
    private String receiver;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String detail;
}
