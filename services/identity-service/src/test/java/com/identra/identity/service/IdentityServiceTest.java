package com.identra.identity.service;

import com.identra.canonical.Identity;
import com.identra.canonical.IdentityWriteRequest;
import com.identra.identity.cache.IdentityCache;
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

    @Mock
    private IdentityCache cache;

    private IdentityService service;

    @BeforeEach
    void setUp() {
        service = new IdentityService(repository, cache);
    }

    @Test
    void getUsesCacheHit() {
        UUID tenantId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Identity cached = sample(tenantId, id, "jdoe");
        when(cache.get(tenantId, id)).thenReturn(Optional.of(cached));
        assertEquals("jdoe", service.get(tenantId, id).userName());
    }

    @Test
    void createPersistsAndCaches() {
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
        verify(cache).put(any());
    }

    @Test
    void getMissingThrowsNotFound() {
        UUID tenantId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(cache.get(tenantId, id)).thenReturn(Optional.empty());
        when(repository.findById(tenantId, id)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.get(tenantId, id));
    }

    private static Identity sample(UUID tenantId, UUID id, String userName) {
        Instant now = Instant.now();
        return new Identity(
                id,
                tenantId,
                List.of(),
                userName,
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
                now,
                now
        );
    }
}
