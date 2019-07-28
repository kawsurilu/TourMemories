package org.tourmemories.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

import org.tourmemories.domain.enumeration.PrivacyCategories;

import org.tourmemories.domain.enumeration.PinnedPostCategories;

/**
 * A Status.
 */
@Entity
@Table(name = "status")
public class Status implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Lob
    @Column(name = "status_text", nullable = false)
    private String statusText;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", nullable = false)
    private PrivacyCategories privacy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "pinned_status", nullable = false)
    private PinnedPostCategories pinnedStatus;

    @NotNull
    @Column(name = "login_id", nullable = false)
    private String loginId;

    @ManyToOne
    @JsonIgnoreProperties("statuses")
    private Location locations;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatusText() {
        return statusText;
    }

    public Status statusText(String statusText) {
        this.statusText = statusText;
        return this;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public PrivacyCategories getPrivacy() {
        return privacy;
    }

    public Status privacy(PrivacyCategories privacy) {
        this.privacy = privacy;
        return this;
    }

    public void setPrivacy(PrivacyCategories privacy) {
        this.privacy = privacy;
    }

    public PinnedPostCategories getPinnedStatus() {
        return pinnedStatus;
    }

    public Status pinnedStatus(PinnedPostCategories pinnedStatus) {
        this.pinnedStatus = pinnedStatus;
        return this;
    }

    public void setPinnedStatus(PinnedPostCategories pinnedStatus) {
        this.pinnedStatus = pinnedStatus;
    }

    public String getLoginId() {
        return loginId;
    }

    public Status loginId(String loginId) {
        this.loginId = loginId;
        return this;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public Location getLocations() {
        return locations;
    }

    public Status locations(Location location) {
        this.locations = location;
        return this;
    }

    public void setLocations(Location location) {
        this.locations = location;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Status)) {
            return false;
        }
        return id != null && id.equals(((Status) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Status{" +
            "id=" + getId() +
            ", statusText='" + getStatusText() + "'" +
            ", privacy='" + getPrivacy() + "'" +
            ", pinnedStatus='" + getPinnedStatus() + "'" +
            ", loginId='" + getLoginId() + "'" +
            "}";
    }
}
