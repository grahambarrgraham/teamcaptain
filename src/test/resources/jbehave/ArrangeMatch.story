Scenario: a notification email is sent out to the first pick players
Given a match is scheduled
When it is 10 days before the match
Then an availability notification is sent to the first pick members

