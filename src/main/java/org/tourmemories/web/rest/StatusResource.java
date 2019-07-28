package org.tourmemories.web.rest;

import org.tourmemories.domain.Status;
import org.tourmemories.domain.enumeration.PinnedPostCategories;
import org.tourmemories.domain.enumeration.PrivacyCategories;
import org.tourmemories.repository.StatusRepository;
import org.tourmemories.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link org.tourmemories.domain.Status}.
 */
@RestController
@RequestMapping("/api")
public class StatusResource {

    private final Logger log = LoggerFactory.getLogger(StatusResource.class);

    private static final String ENTITY_NAME = "status";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatusRepository statusRepository;

    public StatusResource(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    /**
     * {@code POST  /statuses} : Create a new status.
     *
     * @param status the status to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new status, or with status {@code 400 (Bad Request)} if the status has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/statuses")
    public ResponseEntity<Status> createStatus(@Valid @RequestBody Status status) throws URISyntaxException {
        log.debug("REST request to save Status : {}", status);
        if (status.getId() != null) {
            throw new BadRequestAlertException("A new status cannot already have an ID", ENTITY_NAME, "idexists");
        }
        makeExistingPinnedPostToUnPinned(status);
        Status result = statusRepository.save(status);
        return ResponseEntity.created(new URI("/api/statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    private void makeExistingPinnedPostToUnPinned(@RequestBody @Valid Status status) {
        if(status.getPinnedStatus().equals(PinnedPostCategories.PINNED)) {
            List<Status> statuses = statusRepository.findAll();
            List<Status> existingPinnedPost = new ArrayList<>();
            for(Status status1: statuses){
                if(status1.getPinnedStatus().equals(PinnedPostCategories.PINNED)){
                    existingPinnedPost.add(status1);
                }
            }
            for(Status status1: existingPinnedPost){
                status1.setPinnedStatus(PinnedPostCategories.UNPINNED);
                statusRepository.save(status1);
            }
        }
    }

    /**
     * {@code PUT  /statuses} : Updates an existing status.
     *
     * @param status the status to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated status,
     * or with status {@code 400 (Bad Request)} if the status is not valid,
     * or with status {@code 500 (Internal Server Error)} if the status couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/statuses")
    public ResponseEntity<Status> updateStatus(@Valid @RequestBody Status status) throws URISyntaxException {
        log.debug("REST request to update Status : {}", status);
        if (status.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        makeExistingPinnedPostToUnPinned(status);
        Status result = statusRepository.save(status);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, status.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /statuses} : get all the statuses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statuses in body.
     */
    @GetMapping("/statuses")
    public List<Status> getAllStatuses() {
        log.debug("REST request to get all Statuses");
        return statusRepository.findAll();
    }

    @GetMapping("/statuses/loginId/{loginId}")
    public List<Status> getAllStatuses(@PathVariable String loginId) {
        log.debug("REST request to get all Statuses by login id: {}", loginId);
        return statusRepository.getByLoginId(loginId);
    }

    @GetMapping("/statuses/privacy/{privacy}")
    public List<Status> getAllStatuses(@PathVariable PrivacyCategories privacy) {
        log.debug("REST request to get all Statuses by privacy: {}", privacy);
        return statusRepository.getByPrivacy(privacy);
    }

    /**
     * {@code GET  /statuses/:id} : get the "id" status.
     *
     * @param id the id of the status to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the status, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/statuses/{id}")
    public ResponseEntity<Status> getStatus(@PathVariable Long id) {
        log.debug("REST request to get Status : {}", id);
        Optional<Status> status = statusRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(status);
    }

    /**
     * {@code DELETE  /statuses/:id} : delete the "id" status.
     *
     * @param id the id of the status to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/statuses/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        log.debug("REST request to delete Status : {}", id);
        statusRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
