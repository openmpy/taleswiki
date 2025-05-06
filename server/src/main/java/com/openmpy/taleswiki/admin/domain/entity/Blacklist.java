package com.openmpy.taleswiki.admin.domain.entity;

import com.openmpy.taleswiki.admin.domain.BlacklistReason;
import com.openmpy.taleswiki.common.domain.ClientIp;
import com.openmpy.taleswiki.common.domain.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Blacklist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ip", nullable = false))
    private ClientIp ip;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "reason", nullable = false))
    private BlacklistReason reason;

    @Builder
    public Blacklist(final String ip, final String reason) {
        this.ip = new ClientIp(ip);
        this.reason = new BlacklistReason(reason);
    }

    public static Blacklist create(final String ip, final String reason) {
        return Blacklist.builder()
                .ip(ip)
                .reason(reason)
                .build();
    }

    public String getIp() {
        return ip.getValue();
    }

    public String getReason() {
        return reason.getValue();
    }
}
