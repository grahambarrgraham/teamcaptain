package org.rrabarg.teamcaptain.service;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.rrabarg.teamcaptain.domain.CompetitionState;
import org.springframework.stereotype.Service;

@Service
public class CompetitionStateSerialisationHelper {

    public CompetitionState fromString(String serialisedState) {

        if (serialisedState == null) {
            return null;
        }

        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(serialisedState, CompetitionState.class);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to de-serialise JSON state", e);
        }
    }

    public String toString(CompetitionState state) {

        if (state == null) {
            return null;
        }

        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(state);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to serialise workflow", e);
        }
    }

}
