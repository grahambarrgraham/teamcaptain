package org.rrabarg.teamcaptain;

import java.util.Collection;
import java.util.stream.Collectors;

import org.rrabarg.teamcaptain.domain.Match;
import org.rrabarg.teamcaptain.domain.MatchWorkflow;
import org.springframework.stereotype.Component;

@Component
public class WorkflowService {

    public Collection<MatchWorkflow> getWorkflows(Collection<Match> matches) {
        return matches.stream().map(a -> new MatchWorkflow(a)).collect(Collectors.toList());
    }

}
