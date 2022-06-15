package com.el.common.mail.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * since 8/16/21
 *
 * @author eddie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailAccountConfig {

    /**
     * 账户
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 域名
     */
    private String host;
    /**
     * 是否开启认证
     */
    private Boolean auth;


    public String getHostIfPresent() {
        if (Objects.isNull(this.host)) {
            throw new IllegalArgumentException("邮箱认证域名不能为空，请检查配置");
        }
        return this.host;
    }

    public String getAccountIfPresent() {
        if (Objects.isNull(this.account)) {
            throw new IllegalArgumentException("邮箱账户不能为空，请检查配置");
        }
        return this.account;
    }

    public String getPasswordIfPresent() {
        if (Objects.isNull(this.password)) {
            throw new IllegalArgumentException("邮箱密码不能为空，请检查配置");
        }
        return this.password;
    }

    public boolean getAuthIfPresent() {
        if (Objects.isNull(this.auth)) {
            return false;
        }
        return this.auth;
    }

    public int getPortIfPresent() {
        if (Objects.isNull(this.port)) {
            return 25;
        }
        return this.port;
    }

}
