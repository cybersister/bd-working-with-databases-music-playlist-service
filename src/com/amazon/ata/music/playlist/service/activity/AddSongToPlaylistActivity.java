package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
import com.amazon.ata.music.playlist.service.models.requests.AddSongToPlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.AddSongToPlaylistResult;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the AddSongToPlaylistActivity for the MusicPlaylistService's AddSongToPlaylist API.
 *
 * This API allows the customer to add a song to their existing playlist.
 */
public class AddSongToPlaylistActivity implements
        RequestHandler<AddSongToPlaylistRequest, AddSongToPlaylistResult> {

    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;
    private final AlbumTrackDao albumTrackDao;

    /**
     * Instantiates a new AddSongToPlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     * @param albumTrackDao AlbumTrackDao to access the album_track table.
     */
    @Inject
    public AddSongToPlaylistActivity(PlaylistDao playlistDao, AlbumTrackDao albumTrackDao) {
        this.playlistDao = playlistDao;
        this.albumTrackDao = albumTrackDao;
    }

//    public AddSongToPlaylistActivity() {
//        AmazonDynamoDB amazonDynamoDB = DynamoDbClientProvider.getDynamoDBClient();
//        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
//        this.playlistDao = new PlaylistDao(dynamoDBMapper);
//        this.albumTrackDao = new AlbumTrackDao(dynamoDBMapper);
//    }
    // strictly for the aws lambda function ... requires default no-argument constructor

    // commenting out for the sake of PASSING a test

    /**
     * This method handles the incoming request by adding another song to a playlist and persisting the
     * updated playlist.
     * <p>
     * It then returns the updated song list of the playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the album track does not exist, this should throw an AlbumTrackNotFoundException.
     *
     * @param addSongToPlaylistRequest request object containing the playlist ID and an asin and track
     *                                 number to retrieve the song data
     * @return addSongToPlaylistResult result object containing the playlist's updated list of API
     *                                 defined {@link SongModel}s
     */
    @Override
    public AddSongToPlaylistResult handleRequest(final AddSongToPlaylistRequest addSongToPlaylistRequest,
                                                 Context context) {
        log.info("Received AddSongToPlaylistRequest {} ", addSongToPlaylistRequest);

        String id = addSongToPlaylistRequest.getId();
        String asin = addSongToPlaylistRequest.getAsin();
        int trackNumber = addSongToPlaylistRequest.getTrackNumber();

        Playlist playlist = playlistDao.getPlaylist(id);

        // AlbumTrack albumTrack = albumTrackDao.addSongToPlaylist(playlist, asin, trackNumber);

        List<AlbumTrack> listOfTracks = playlist.getSongList();

        AlbumTrack song = albumTrackDao.getAlbumTrack(asin, trackNumber);

        if (song == null) {
            throw new AlbumTrackNotFoundException();
        }

        if (addSongToPlaylistRequest.isQueueNext()) {
            listOfTracks.add(0, song);
        } else {
            listOfTracks.add(song);
        }

        playlist.setSongList(listOfTracks);

        playlistDao.savePlaylist(playlist);

        // return the updated song list from the api
        // think we could do this in the <addSongToPlaylist()> method

        List<SongModel> songModelList = new ArrayList<>();

        for (AlbumTrack albumTrack : playlist.getSongList()) {
            SongModel songModel = new ModelConverter().toSongModel(albumTrack);

            songModelList.add(songModel);
        }

        return AddSongToPlaylistResult.builder()
                .withSongList(songModelList)
                .build();

    }

}
