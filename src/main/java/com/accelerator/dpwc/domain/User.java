package com.accelerator.dpwc.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity @Table(name = "T_USER")
public class User extends BasicEntity {

    private static final long serialVersionUID = -7432528979347309638L;

    @Id
    @Pattern(regexp = "\\d{6}", message = "{com.accelerator.dpwc.model.User.username.Pattern}")
    @Column(name = "USERNAME", length = 6, unique = true, nullable = false)
    private String username;

    @NotBlank(message = "{com.accelerator.dpwc.model.User.password.NotBlank}")
    @Column(name = "PASSWORD", length = 32, nullable = false)
    private String password;

    @OneToMany(cascade = {
            CascadeType.REFRESH, CascadeType.REMOVE
    }, fetch = FetchType.LAZY, mappedBy = "id.user", targetEntity = Clock.class)
    private List<Clock> clocks;

    public User() {}

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Clock> getClocks() {
        return clocks;
    }

    public void setClocks(List<Clock> clocks) {
        this.clocks = clocks;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "clocks");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "clocks");
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .setExcludeFieldNames("clocks")
                .toString();
    }

}
