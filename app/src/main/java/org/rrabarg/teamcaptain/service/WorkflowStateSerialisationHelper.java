package org.rrabarg.teamcaptain.service;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.rrabarg.teamcaptain.domain.WorkflowState;
import org.springframework.stereotype.Service;

@Service
public class WorkflowStateSerialisationHelper {

    public WorkflowState fromString(String serialisedWorkflow) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(serialisedWorkflow, WorkflowState.class);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to de-serialise JSON state", e);
        }
    }

    public String toString(WorkflowState workflowState) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(workflowState);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to serialise workflow", e);
        }
    }

}
