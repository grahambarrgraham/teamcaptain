Scenario: Cambridgeshire County Team Nominated Team
Given a Cambridge county match is scheduled
When players are chosen
Then the nominated players are chosen first

Scenario: Cambridgeshire County Team substitute gender
Given a Cambridge county match is scheduled
When players are chosen
Then the nominated ladies are chosen
And the nominated men are chosen
 
Scenario: Cambridgeshire County Team substitutes
Given a Cambridge county match is scheduled
And a nominated player is unavailable
When players are chosen
Then the next strongest player of the same gender is chosen 

