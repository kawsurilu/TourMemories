package org.tourmemories.web.rest;

import org.tourmemories.TourMemoriesApp;
import org.tourmemories.domain.Status;
import org.tourmemories.repository.StatusRepository;
import org.tourmemories.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.tourmemories.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.tourmemories.domain.enumeration.PrivacyCategories;
import org.tourmemories.domain.enumeration.PinnedPostCategories;
/**
 * Integration tests for the {@Link StatusResource} REST controller.
 */
@SpringBootTest(classes = TourMemoriesApp.class)
public class StatusResourceIT {

    private static final String DEFAULT_STATUS_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_TEXT = "BBBBBBBBBB";

    private static final PrivacyCategories DEFAULT_PRIVACY = PrivacyCategories.PUBLIC;
    private static final PrivacyCategories UPDATED_PRIVACY = PrivacyCategories.PRIVATE;

    private static final PinnedPostCategories DEFAULT_PINNED_STATUS = PinnedPostCategories.PINNED;
    private static final PinnedPostCategories UPDATED_PINNED_STATUS = PinnedPostCategories.UNPINNED;

    private static final String DEFAULT_LOGIN_ID = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN_ID = "BBBBBBBBBB";

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restStatusMockMvc;

    private Status status;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final StatusResource statusResource = new StatusResource(statusRepository);
        this.restStatusMockMvc = MockMvcBuilders.standaloneSetup(statusResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createEntity(EntityManager em) {
        Status status = new Status()
            .statusText(DEFAULT_STATUS_TEXT)
            .privacy(DEFAULT_PRIVACY)
            .pinnedStatus(DEFAULT_PINNED_STATUS)
            .loginId(DEFAULT_LOGIN_ID);
        return status;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createUpdatedEntity(EntityManager em) {
        Status status = new Status()
            .statusText(UPDATED_STATUS_TEXT)
            .privacy(UPDATED_PRIVACY)
            .pinnedStatus(UPDATED_PINNED_STATUS)
            .loginId(UPDATED_LOGIN_ID);
        return status;
    }

    @BeforeEach
    public void initTest() {
        status = createEntity(em);
    }

    @Test
    @Transactional
    public void createStatus() throws Exception {
        int databaseSizeBeforeCreate = statusRepository.findAll().size();

        // Create the Status
        restStatusMockMvc.perform(post("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(status)))
            .andExpect(status().isCreated());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate + 1);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getStatusText()).isEqualTo(DEFAULT_STATUS_TEXT);
        assertThat(testStatus.getPrivacy()).isEqualTo(DEFAULT_PRIVACY);
        assertThat(testStatus.getPinnedStatus()).isEqualTo(DEFAULT_PINNED_STATUS);
        assertThat(testStatus.getLoginId()).isEqualTo(DEFAULT_LOGIN_ID);
    }

    @Test
    @Transactional
    public void createStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = statusRepository.findAll().size();

        // Create the Status with an existing ID
        status.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusMockMvc.perform(post("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(status)))
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkPrivacyIsRequired() throws Exception {
        int databaseSizeBeforeTest = statusRepository.findAll().size();
        // set the field null
        status.setPrivacy(null);

        // Create the Status, which fails.

        restStatusMockMvc.perform(post("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(status)))
            .andExpect(status().isBadRequest());

        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPinnedStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = statusRepository.findAll().size();
        // set the field null
        status.setPinnedStatus(null);

        // Create the Status, which fails.

        restStatusMockMvc.perform(post("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(status)))
            .andExpect(status().isBadRequest());

        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLoginIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = statusRepository.findAll().size();
        // set the field null
        status.setLoginId(null);

        // Create the Status, which fails.

        restStatusMockMvc.perform(post("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(status)))
            .andExpect(status().isBadRequest());

        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStatuses() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList
        restStatusMockMvc.perform(get("/api/statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].statusText").value(hasItem(DEFAULT_STATUS_TEXT.toString())))
            .andExpect(jsonPath("$.[*].privacy").value(hasItem(DEFAULT_PRIVACY.toString())))
            .andExpect(jsonPath("$.[*].pinnedStatus").value(hasItem(DEFAULT_PINNED_STATUS.toString())))
            .andExpect(jsonPath("$.[*].loginId").value(hasItem(DEFAULT_LOGIN_ID.toString())));
    }
    
    @Test
    @Transactional
    public void getStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get the status
        restStatusMockMvc.perform(get("/api/statuses/{id}", status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(status.getId().intValue()))
            .andExpect(jsonPath("$.statusText").value(DEFAULT_STATUS_TEXT.toString()))
            .andExpect(jsonPath("$.privacy").value(DEFAULT_PRIVACY.toString()))
            .andExpect(jsonPath("$.pinnedStatus").value(DEFAULT_PINNED_STATUS.toString()))
            .andExpect(jsonPath("$.loginId").value(DEFAULT_LOGIN_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStatus() throws Exception {
        // Get the status
        restStatusMockMvc.perform(get("/api/statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Update the status
        Status updatedStatus = statusRepository.findById(status.getId()).get();
        // Disconnect from session so that the updates on updatedStatus are not directly saved in db
        em.detach(updatedStatus);
        updatedStatus
            .statusText(UPDATED_STATUS_TEXT)
            .privacy(UPDATED_PRIVACY)
            .pinnedStatus(UPDATED_PINNED_STATUS)
            .loginId(UPDATED_LOGIN_ID);

        restStatusMockMvc.perform(put("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedStatus)))
            .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getStatusText()).isEqualTo(UPDATED_STATUS_TEXT);
        assertThat(testStatus.getPrivacy()).isEqualTo(UPDATED_PRIVACY);
        assertThat(testStatus.getPinnedStatus()).isEqualTo(UPDATED_PINNED_STATUS);
        assertThat(testStatus.getLoginId()).isEqualTo(UPDATED_LOGIN_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Create the Status

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatusMockMvc.perform(put("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(status)))
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        int databaseSizeBeforeDelete = statusRepository.findAll().size();

        // Delete the status
        restStatusMockMvc.perform(delete("/api/statuses/{id}", status.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Status.class);
        Status status1 = new Status();
        status1.setId(1L);
        Status status2 = new Status();
        status2.setId(status1.getId());
        assertThat(status1).isEqualTo(status2);
        status2.setId(2L);
        assertThat(status1).isNotEqualTo(status2);
        status1.setId(null);
        assertThat(status1).isNotEqualTo(status2);
    }
}
