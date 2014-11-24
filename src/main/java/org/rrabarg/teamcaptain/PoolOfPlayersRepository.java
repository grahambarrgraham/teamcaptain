package org.rrabarg.teamcaptain;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.rrabarg.teamcaptain.domain.Gender;
import org.rrabarg.teamcaptain.domain.Player;
import org.rrabarg.teamcaptain.domain.PoolOfPlayers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
public class PoolOfPlayersRepository {

    private static final String CONTACT_LABEL = "TeamCaptain";
    private static final String groupsFeedUrl = "https://www.google.com/m8/feeds/groups/default/full";
    private static final String contactsFeedUrl = "https://www.google.com/m8/feeds/contacts/default/full";

    @Autowired
    ContactsService contactService;

    public String findPlayerPoolIdByName(String competitionName) throws IOException, ServiceException {
        final Query myQuery = new Query(getGroupsFeedUrl());
        myQuery.setStringCustomParameter("title", competitionName);
        final ContactGroupFeed queryResult = contactService.query(myQuery, ContactGroupFeed.class);
        final Optional<ContactGroupEntry> entries = queryResult.getEntries().stream().findAny();
        return entries.isPresent() ? entries.get().getId() : null;
    }

    public String createPlayerPoolWithName(String competitionName) throws MalformedURLException, IOException,
            ServiceException {
        final ContactGroupEntry entry = new ContactGroupEntry();
        entry.setTitle(TextConstruct.plainText(competitionName));
        return contactService.insert(getGroupsFeedUrl(), entry).getId();
    }

    public PoolOfPlayers getPlayerPoolById(String poolId) throws MalformedURLException, IOException, ServiceException {

        final List<Player> players = streamAllContactsInGroup(poolId).map(entry -> instantiatePlayer(entry)).collect(
                Collectors.toList());

        return new PoolOfPlayers(poolId, players);

    }

    public void addPlayersToPool(String poolId, Collection<Player> players) throws MalformedURLException, IOException,
            ServiceException {
        players.stream().forEach(a -> addPlayer(poolId, a));
    }

    public void clearPlayersFromPool(String poolId) throws MalformedURLException, IOException, ServiceException {
        streamAllContactsInGroup(poolId).forEach(a -> deleteContact(a));
    }

    private void addPlayer(String poolId, final Player player) {
        ContactEntry addedContact;
        try {
            addedContact = contactService.insert(getContactFeedUrl(), buildContact(player, poolId));
        } catch (IOException | ServiceException e) {
            throw new RuntimeException("Failed to add player " + player, e);
        }
        player.setId(addedContact.getId());
    }

    private void deleteContact(ContactEntry entry) {
        try {
            contactService.delete(asUri(entry.getEditLink()));
        } catch (IOException | ServiceException | URISyntaxException e) {
            throw new RuntimeException("Failed to delete contact " + entry, e);
        }
    }

    private URI asUri(Link link) throws URISyntaxException {
        return new URI(link.getHref());
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

    private Player instantiatePlayer(ContactEntry entry) {
        return new Player(
                entry.getName().getGivenName().getValue(),
                entry.getName().getFamilyName().getValue(),
                convert(entry.getGender()), getEmailAddress(entry), getPhoneNumber(entry));
    }

    private String getEmailAddress(ContactEntry entry) {
        final Optional<Email> optional = entry.getEmailAddresses().stream()
                .filter(a -> CONTACT_LABEL.equals(a.getLabel())).findFirst();
        return optional.isPresent() ? optional.get().getAddress() : null;
    }

    private String getPhoneNumber(ContactEntry entry) {
        final Optional<PhoneNumber> optional = entry.getPhoneNumbers().stream()
                .filter(a -> CONTACT_LABEL.equals(a.getLabel())).findFirst();
        return optional.isPresent() ? optional.get().getPhoneNumber() : null;
    }

    private Stream<ContactEntry> streamAllContactsInGroup(String groupId) throws MalformedURLException, IOException,
            ServiceException {

        // TODO - work out how to do this using Stream supplier model

        URL feedUrl = getContactFeedUrl();
        final List<ContactEntry> contactEntries = new ArrayList<>();

        while (feedUrl != null) {
            final ContactFeed result = getResult(groupId, feedUrl);
            contactEntries.addAll(result.getEntries());
            final Link nextLink = result.getNextLink();
            feedUrl = new URL(nextLink.getHref());
        }

        return contactEntries.stream();
    }

    private ContactFeed getResult(String groupId, URL href) throws IOException, ServiceException {
        final Query myQuery = new Query(href);
        myQuery.setMaxResults(100);
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
        return gender.getValue() == null ?
                null : Value.MALE == gender.getValue() ?
                        Gender.Male : Gender.Female;
    }
}
