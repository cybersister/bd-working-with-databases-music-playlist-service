package com.amazon.ata.music.playlist.service.dynamodb;

import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;

import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazon.ata.test.assertions.PlantUmlClassDiagramAssertions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Accesses data for a playlist using {@link Playlist} to represent the model in DynamoDB.
 */
public class PlaylistDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a PlaylistDao object.
     *
     * @param dynamoDbMapper the {@link DynamoDBMapper} used to interact with the playlists table
     */
    public PlaylistDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the {@link Playlist} corresponding to the specified id.
     *
     * @param id the Playlist ID
     * @return the stored Playlist, or null if none was found.
     */
    public Playlist getPlaylist(String id) {
        Playlist playlist = this.dynamoDbMapper.load(Playlist.class, id);

        if (playlist == null) {
            throw new PlaylistNotFoundException("Could not find playlist with id " + id);
        }

        return playlist;
    }

    public Playlist savePlaylist(String name, String customerId, List<String> tags) {
        boolean isCustomerIdValid = MusicPlaylistServiceUtils.isValidString(customerId);
        boolean isNameValid = MusicPlaylistServiceUtils.isValidString(name);

        if (!isCustomerIdValid) {
            throw new InvalidAttributeValueException("Given Customer ID: " + customerId + " is invalid.");
        } else if (!isNameValid) {
            throw new InvalidAttributeValueException("Given Name: " + name + " is invalid.");
        }

        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setCustomerId(customerId);
        playlist.setId(MusicPlaylistServiceUtils.generatePlaylistId());
        playlist.setSongList(new ArrayList<>());
        playlist.setSongCount(0);

        if (tags == null) {
            playlist.setTags(new HashSet<>());
        } else {
            playlist.setTags(new HashSet<>(tags));
        }

        this.dynamoDbMapper.save(playlist);

        return playlist;
    }

    public Playlist updatePlaylist(String playlistId, String updatedPlaylistName, String customerId) {

        // validate that the provided playlist exists and if it doesn't throw a <PlaylistNotFoundException>

        // validate the provided customer id (create a separate method for this)
        // ensure it matches the customer id associated with the given playlist and if not throw a ...
        // ... <InvalidAttributeChangeException> (making an assumption here)
        // also validate that it doesn't contain any invalid characters, throw a ...
        // ... <InvalidAttributeValueException> if it fails the validation (DO THIS VALIDATION FIRST)

        // validate that the new updated playlist name doesn't contain any invalid characters
        // there is a method that already exists for this
        // throw a <InvalidAttributeValueException> if it fails the validation

        // update the given updated playlist name

        // save the updated playlist to the playlist dao

        // IF I SAVE SOMETHING IT IS BASED OFF OF THE ID AND NOT THE NAME
        // THE PLAYLIST ID IS THE HASH KEY/PARTITION KEY
        // USE THE <DynamoDbMapper.save()> METHOD IN THIS CASE
        // IT WILL WRITE OVER TOP OF THE ALREADY EXISTING PLAYLIST

        return playlist;
    }
    // todo -> implement this playlist <3

}
