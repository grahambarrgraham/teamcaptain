Narrative A team for the match is confirmed

Scenario: a team is confirmed, all players received details
Given a match is scheduled and is in the selection window
When sufficient players are assigned to the match
Then a match confirmation notification is sent out to all notified players
And the confirmation contains the list of players assigned to the match
And the confirmation contains the match details
And the team captain is notified of the match confirmation message from the system

