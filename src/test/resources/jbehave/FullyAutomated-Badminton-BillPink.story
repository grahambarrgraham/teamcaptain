Scenario: Bill Pink Match
Given a Bill Pink Badminton match is scheduled
When players are chosen
Then the 3 strongest eligible ladies are chosen from the pool
And the 3 strongest eligible men are chosen from the pool

Scenario: Cambridge League Mens Badminton
Given a Cambridge league mens doubles badminton match is scheduled
When players are chosen
Then the 6 strongest eligible and available men are chosen from the pool

Scenario: Cambridge League Ladies Badminton
Given a Cambridge league mens doubles badminton match is scheduled
When players are chosen
Then the 6 strongest eligible and available ladies are chosen from the pool

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

