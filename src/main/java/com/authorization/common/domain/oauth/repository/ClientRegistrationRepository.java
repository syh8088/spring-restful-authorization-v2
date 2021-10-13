package com.authorization.common.domain.oauth.repository;

import com.authorization.common.domain.oauth.model.transfer.ClientRegistration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistrationRepository {

    private final Map<String, ClientRegistration> registrations;

    public ClientRegistrationRepository(Map<String, ClientRegistration> registrations) {
        this.registrations = registrations;
    }

    public ClientRegistrationRepository(List<ClientRegistration> registrations) {
        this(createRegistrationsMap(registrations));
    }

    private static Map<String, ClientRegistration> createRegistrationsMap(List<ClientRegistration> registrations) {
        return toUnmodifiableConcurrentMap(registrations);
    }

    private static Map<String, ClientRegistration> toUnmodifiableConcurrentMap(List<ClientRegistration> registrations) {

        ConcurrentHashMap<String, ClientRegistration> result = new ConcurrentHashMap<>();

        for (ClientRegistration registration : registrations)
            result.put(registration.getRegistrationId().getProvider(), registration);
        return Collections.unmodifiableMap(result);
    }

    public ClientRegistration selectByRegistrationId(String registrationId) {
        return this.registrations.get(registrationId);
    }
}
