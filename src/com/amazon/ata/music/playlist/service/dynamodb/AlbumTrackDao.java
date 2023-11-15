package com.amazon.ata.music.playlist.service.dynamodb;

import com.amazon.ata.music.playlist.service.activity.CreatePlaylistActivity;
import com.amazon.ata.music.playlist.service.activity.GetPlaylistActivity;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;

import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import javax.inject.Inject;
import java.util.List;

/**
 * Accesses data for an album using {@link AlbumTrack} to represent the model in DynamoDB.
 */
public class AlbumTrackDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates an AlbumTrackDao object.
     * @param dynamoDbMapper the {@link DynamoDBMapper} used to interact with the album_track table
     */
    @Inject
    public AlbumTrackDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    public AlbumTrack getAlbumTrack(String asin, String trackNumber) {
        AlbumTrack albumTrack = this.dynamoDbMapper.load(AlbumTrack.class, asin, trackNumber);

        if (albumTrack == null) {
            throw new AlbumTrackNotFoundException("Could not find playlist with asin " + asin
                    + " and track number " + trackNumber + ".");
        }

        return albumTrack;
    }

    public AlbumTrack addSongToPlaylist(Playlist playlist, String asin, int trackNumber) {
        List<AlbumTrack> listOfTracks = playlist.getSongList();

        AlbumTrack song = new AlbumTrack();
        song.setAsin(asin);
        song.setTrackNumber(String.valueOf(trackNumber));

        listOfTracks.add(song);

        playlist.setSongList(listOfTracks);

        this.dynamoDbMapper.save(playlist);

        return song;
    }

}
