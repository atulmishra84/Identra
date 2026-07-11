package com.identra.identity.service;

import com.identra.canonical.Identity;
import com.identra.canonical.IdentityWriteRequest;
import com.identra.identity.persistence.IdentityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {

    @Mock
    private IdentityRepository repository;

    private IdentityService service;

    @BeforeEach
    void setUp() {
        service = new IdentityService(repository);
    }

    @Test
    void createPersistsNewIdentity() {
        UUID tenantId = UUID.randomUUID();
        when(repository.findByUserName(tenantId, "jdoe")).thenReturn(Optional.empty());
        when(repository.insert(any())).thenAnswer(inv -> inv.getArgument(0));

        Identity created = service.create(tenantId, new IdentityWriteRequest(
                "jdoe",
                List.of(new Identity.Email("jdoe@example.com", "work", true)),
                new Identity.Name("Jane Doe", "Doe", "Jane"),
                true,
                "Engineering",
                null,
                Identity.EmploymentStatus.ACTIVE,
                List.of(),
                "identra",
                null
        ));

        assertEquals("jdoe", created.userName());
        assertEquals(tenantId, created.tenantId());
        verify(repository).insert(any());
    }

    @Test
    void getMissingThrowsNotFound() {
        UUID tenantId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(repository.findById(tenantId, id)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.get(tenantId, id));
    }

    @Test
    void createDuplicateUserNameConflicts() {
        UUID tenantId = UUID.randomUUID();
        Identity existing = new Identity(
                UUID.randomUUID(),
                tenantId,
                List.of(),
                "jdoe",
                List.of(),
                null,
                true,
                null,
                null,
                Identity.EmploymentStatus.ACTIVE,
                null,
                "identra",
                0,
                "W/\"x\"",
                Instant.now(),
                Instant.now()
        );
        when(repository.findByUserName(tenantId, "jdoe")).thenReturn(Optional.of(existing));
        assertThrows(ResponseStatusException.class, () -> service.create(tenantId, new IdentityWriteRequest(
                "jdoe", List.of(), null, true, null, null, null, List.of(), null, null
        )));
    }
}
