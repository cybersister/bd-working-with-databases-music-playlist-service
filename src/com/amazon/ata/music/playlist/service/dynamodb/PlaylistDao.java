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
            throw new InvalidAttributeValueException("Given Customer ID: " + customerId + "is invalid.");
        } else if (!isNameValid) {
            throw new InvalidAttributeValueException("Given Name: " + name + "is invalid.");
        }

        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setCustomerId(customerId);
        playlist.setId(MusicPlaylistServiceUtils.generatePlaylistId());
        playlist.setSongList(new ArrayList<>());
        // empty list

        if (tags == null) {
            playlist.setTags(new HashSet<>());
        } else if (tags.isEmpty()) {
            throw new InvalidAttributeValueException("The List of tags cannot be empty.");
        } else {
            playlist.setTags(new HashSet<>(tags));
        }

        this.dynamoDbMapper.save(playlist);

        return playlist;
    }

}
