package org.rrabarg.teamcaptain.service.google;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.rrabarg.teamcaptain.domain.ContactDetail;
import org.rrabarg.teamcaptain.domain.Gender;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PlayerPool;
import org.rrabarg.teamcaptain.domain.TeamCaptain;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.contacts.Gender.Value;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.FamilyName;
import com.google.gdata.data.extensions.GivenName;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.ServiceException;

@Repository
public class PlayerPoolGoogleRepository {

    Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final String CONTACT_LABEL = "TeamCaptain";
    private static final String groupsFeedUrl = "https://www.google.com/m8/feeds/groups/default/full";
    private static final String contactsFeedUrl = "https://www.google.com/m8/feeds/contacts/default/full";

    @Autowired
    ContactsService contactService;

    public String findPlayerPoolIdByName(String competitionName) throws IOException, ServiceException {

        if (competitionName == null) {
            return null;
        }

        final Collection<ContactGroupEntry> groups = getContactGroupsForName(competitionName);

        if (groups.size() > 1) {
            log.warn("Multiple contact groups for name " + competitionName
                    + " probably left over by an abandonded test");
        }

        final Collection<ContactGroupEntry> filteredGroups = groups.stream().filter(e -> e.getDeleted() != null)
                .collect(Collectors.toList());

        if (filteredGroups.size() == 0) {
            return null;
        }

        return filteredGroups.iterator().next().getId();
    }

    public void deletePlayerPoolsByName(String name) throws MalformedURLException, IOException,
            ServiceException {

        if (name == null) {
            return;
        }

        for (final ContactGroupEntry group : getContactGroupsForName(name)) {
            log.debug("Deleting group " + group.getId() + " with name " + group.getTitle().getPlainText());
            group.delete();
        }
    }

    private Collection<ContactGroupEntry> getContactGroupsForName(String competitionName) throws MalformedURLException,
            IOException, ServiceException {
        final Query myQuery = new Query(getGroupsFeedUrl());
        myQuery.setStringCustomParameter("Title", competitionName);
        final ContactGroupFeed queryResult = contactService.query(myQuery, ContactGroupFeed.class);

        final Stream<ContactGroupEntry> stream = queryResult.getEntries().stream();

        final Stream<ContactGroupEntry> filter = stream.filter(
                a -> competitionName.equals(a.getTitle().getPlainText()));

        return filter.collect(Collectors.toList());
    }

    public String createPlayerPoolWithName(String title) throws MalformedURLException, IOException,
            ServiceException {

        if (title == null) {
            throw new RuntimeException("Invalid title " + title);
        }

        final ContactGroupEntry entry = new ContactGroupEntry();
        entry.setTitle(TextConstruct.plainText(title));

        log.debug("Creating group with title " + title);

        return contactService.insert(getGroupsFeedUrl(), entry).getId();
    }

    public PlayerPool getPlayerPoolById(String poolId) throws MalformedURLException, IOException, ServiceException {

        if (poolId == null) {
            throw new RuntimeException("Invalid pool id " + poolId);
        }

        final List<Player> players = streamAllContactsInGroup(poolId).map(entry -> instantiatePlayer(entry)).collect(
                Collectors.toList());

        return new PlayerPool(poolId, players);

    }

    public void addPlayersToPool(String poolId, Collection<Player> players) throws MalformedURLException, IOException,
            ServiceException {
        for (final Player player : players) {
            addPlayer(poolId, player);
        }
    }

    public void clearPlayersFromPool(String poolId) throws MalformedURLException, IOException, ServiceException {
        assert (poolId != null);
        streamAllContactsInGroup(poolId).forEach(a -> deleteContact(a));
    }

    private void addPlayer(String poolId, final Player player) {

        log.debug("Adding contact for player " + player + " to pool " + poolId);

        ContactEntry addedContact;
        try {
            addedContact = contactService.insert(getContactFeedUrl(), buildContact(player, poolId));
        } catch (IOException | ServiceException e) {
            throw new RuntimeException("Failed to add player " + player, e);
        }
        player.setId(addedContact.getId());

        log.debug("Added contact for player " + player + " to pool " + poolId + " got player id "
                + addedContact.getId() + " status " + addedContact.getStatus());
    }

    private void deleteContact(ContactEntry entry) {
        int tries = 0;
        while (true) {
            try {
                contactService.delete(asUrl(entry.getEditLink()), entry.getEtag());
                return;
            } catch (IOException | ServiceException e) {
                if (tries++ > 5) {
                    throw new RuntimeException("Failed to delete contact with surname : "
                            + entry.getName().getFamilyName().getValue(), e);
                }
                try {
                    log.warn("Failed to delete contact " + entry.getId() + " for "
                            + entry.getName().getFamilyName().getValue() + " sleep 50 and retrying..", e);
                    Thread.sleep(50);
                } catch (final InterruptedException e1) {
                    //
                }
            }
        }
    }

    private URL asUrl(Link link) throws MalformedURLException {
        return new URL(link.getHref());
    }

    private ContactEntry buildContact(Player player, String groupHref) {
        final ContactEntry contact = new ContactEntry();
        final Name name = new Name();
        name.setGivenName(new GivenName(player.getFirstname(), null));
        name.setFamilyName(new FamilyName(player.getSurname(), null));
        contact.setName(name);
        contact.setGender(asGoogleGender(player.getGender()));
        final GroupMembershipInfo groupMembershipInfo = new GroupMembershipInfo(false, groupHref);
        contact.addGroupMembershipInfo(groupMembershipInfo);

        if (player.getEmailAddress() != null) {
            final Email email = new Email();
            email.setAddress(player.getEmailAddress());
            email.setDisplayName(player.getFirstname() + " " + player.getSurname());
            email.setLabel(CONTACT_LABEL);
            contact.addEmailAddress(email);
        }

        if (player.getMobileNumber() != null) {
            final PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setPhoneNumber(player.getMobileNumber());
            phoneNumber.setLabel(CONTACT_LABEL);
        }

        return contact;
    }

    private com.google.gdata.data.contacts.Gender asGoogleGender(Gender gender) {
        if (gender == null) {
            return null;
        }

        return Gender.Male == gender ?
                new com.google.gdata.data.contacts.Gender(Value.MALE) :
                new com.google.gdata.data.contacts.Gender(Value.FEMALE);
    }

    private TeamCaptain instantiateTeamCaptain(ContactEntry entry) {

        try {
            final TeamCaptain captain = new TeamCaptain(new ContactDetail(
                    getGivenName(entry),
                    getFamilyName(entry),
                    getEmailAddress(entry),
                    getPhoneNumber(entry)));

            log.debug("Instantiated captain " + captain);

            return captain;
        } catch (final Exception e) {
            log.error("Failed to instantiate captain for entry " + entry);
            throw e;
        }

    }

    private Player instantiatePlayer(ContactEntry entry) {

        try {
            final Player player = new Player(
                    getGivenName(entry),
                    getFamilyName(entry),
                    convert(entry.getGender()),
                    getEmailAddress(entry),
                    getPhoneNumber(entry));

            log.debug("Instantiated player " + player);

            return player;
        } catch (final Exception e) {
            log.error("Failed to instantiate player for entry " + entry);
            throw e;
        }

    }

    private String getGivenName(ContactEntry entry) {
        return entry == null ? null : entry.getName() == null ? null : entry.getName().getGivenName() == null ? null
                : entry.getName().getGivenName().getValue();
    }

    private String getFamilyName(ContactEntry entry) {
        return entry == null ? null : entry.getName() == null ? null : entry.getName().getFamilyName() == null ? null
                : entry.getName().getFamilyName().getValue();
    }

    private String getEmailAddress(ContactEntry entry) {
        if (entry.getEmailAddresses() == null) {
            return null;
        }
        final Optional<Email> optional = entry.getEmailAddresses().stream()
                .filter(a -> CONTACT_LABEL.equals(a.getLabel())).findFirst();
        return optional.isPresent() ? optional.get().getAddress() : null;
    }

    private String getPhoneNumber(ContactEntry entry) {
        if (entry.getPhoneNumbers() == null) {
            return null;
        }
        final Optional<PhoneNumber> optional = entry.getPhoneNumbers().stream()
                .filter(a -> CONTACT_LABEL.equals(a.getLabel())).findFirst();
        return optional.isPresent() ? optional.get().getPhoneNumber() : null;
    }

    private Stream<ContactEntry> streamAllContactsInGroup(String groupId) throws MalformedURLException, IOException,
            ServiceException {

        if (groupId == null) {
            throw new RuntimeException("Invalid contact group " + groupId);
        }

        // TODO - work out how to do this using Stream supplier model

        final List<ContactEntry> contactEntries = new ArrayList<>();

        int startingIndex = 1;

        while (true) {
            final ContactFeed result = getResult(groupId, startingIndex);

            final List<ContactEntry> entries = result.getEntries();

            log.debug("query result size for players query for group id " + groupId + " and starting index "
                    + startingIndex + " was " + entries.size());

            if ((entries == null) || (entries.size() == 0)) {
                break; // yuk
            }
            contactEntries.addAll(entries);
            startingIndex += entries.size();
        }

        return contactEntries.stream().filter(a -> null != a);
    }

    private ContactFeed getResult(String groupId, int startingIndex) throws IOException, ServiceException {
        final Query myQuery = new Query(getContactFeedUrl());
        myQuery.setMaxResults(100);
        myQuery.setStartIndex(startingIndex);
        myQuery.setStringCustomParameter("group", groupId);
        final ContactFeed queryResult = contactService.query(myQuery, ContactFeed.class);
        return queryResult;
    }

    private URL getContactFeedUrl() throws MalformedURLException {
        return new URL(contactsFeedUrl);
    }

    private URL getGroupsFeedUrl() throws MalformedURLException {
        return new URL(groupsFeedUrl);
    }

    private Gender convert(com.google.gdata.data.contacts.Gender gender) {

        if (gender == null) {
            return null;
        }

        return gender.getValue() == null ?
                null : Value.MALE == gender.getValue() ?
                        Gender.Male : Gender.Female;
    }
}
