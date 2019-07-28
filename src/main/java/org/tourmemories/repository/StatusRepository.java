package org.tourmemories.repository;

import org.tourmemories.domain.Status;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.tourmemories.domain.enumeration.PrivacyCategories;

import java.util.List;


/**
 * Spring Data  repository for the Status entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    List<Status> getByLoginId(final String pLoginId);

    List<Status> getByPrivacy(final PrivacyCategories pPrivacyCategories);
}
