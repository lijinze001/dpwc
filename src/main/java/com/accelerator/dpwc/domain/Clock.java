package com.accelerator.dpwc.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity @Table(name = "T_CLOCK")
public class Clock extends BasicEntity {

    private static final long serialVersionUID = 1627023133176705510L;

    @EmbeddedId
    private Id id;

    @Column(name = "TYPE", length = 1, nullable = false)
    private Integer type;

    public Clock() {}

    public Clock(Id id) {
        this.id = id;
    }

    public Clock(Integer type) {
        this.type = type;
    }


    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Embeddable
    public static class Id implements Serializable {

        private static final long serialVersionUID = -5993608289589197081L;

        @JoinColumn(name = "USERNAME", nullable = false, referencedColumnName = "username")
        @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, targetEntity = User.class, optional = false)
        private User user;

        @Column(name = "DATE", nullable = false) @Temporal(TemporalType.DATE)
        private Date date;

        public Id() {}

        public Id(User user) {
            this.user = user;
        }

        public Id(Date date) {
            this.date = date;
        }

        public Id(User user, Date date) {
            this.user = user;
            this.date = date;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }

    }

}

