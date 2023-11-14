package com.amazon.ata.music.playlist.service.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Represents a record in the album_tracks table.
 */
@DynamoDBTable(tableName = "album_tracks")
public class AlbumTrack {

    private String asin;
    private String trackNumber;
    private String albumName;
    private String songTitle;

    public AlbumTrack() {}

    @DynamoDBHashKey(attributeName = "asin")
    public String getAsin() {
        return asin;
    }

    public AlbumTrack setAsin(String asin) {
        this.asin = asin;
        return this;
    }

    @DynamoDBRangeKey(attributeName = "track_number")
    public String getTrackNumber() {
        return trackNumber;
    }

    public AlbumTrack setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
        return this;
    }

    @DynamoDBAttribute(attributeName = "album_name")
    public String getAlbumName() {
        return albumName;
    }

    public AlbumTrack setAlbumName(String albumName) {
        this.albumName = albumName;
        return this;
    }

    @DynamoDBAttribute(attributeName = "song_title")
    public String getSongTitle() {
        return songTitle;
    }

    public AlbumTrack setSongTitle(String songTitle) {
        this.songTitle = songTitle;
        return this;
    }

}
