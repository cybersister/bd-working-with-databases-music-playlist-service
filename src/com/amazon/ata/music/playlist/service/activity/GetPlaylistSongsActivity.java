package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.models.SongOrder;
import com.amazon.ata.music.playlist.service.models.requests.GetPlaylistSongsRequest;
import com.amazon.ata.music.playlist.service.models.results.GetPlaylistSongsResult;
import com.amazon.ata.music.playlist.service.models.SongModel;
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
 * Implementation of the GetPlaylistSongsActivity for the MusicPlaylistService's GetPlaylistSongs API.
 *
 * This API allows the customer to get the list of songs of a saved playlist.
 */
public class GetPlaylistSongsActivity implements RequestHandler<GetPlaylistSongsRequest,
        GetPlaylistSongsResult> {

    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new GetPlaylistSongsActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     */
    @Inject
    public GetPlaylistSongsActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    public GetPlaylistSongsActivity() {
        AmazonDynamoDB amazonDynamoDB = DynamoDbClientProvider.getDynamoDBClient();
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        this.playlistDao = new PlaylistDao(dynamoDBMapper);
    }
    // strictly for the aws lambda function ... requires default no-argument constructor

    /**
     * This method handles the incoming request by retrieving the playlist from the database.
     * <p>
     * It then returns the playlist's song list.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     *
     * @param getPlaylistSongsRequest request object containing the playlist ID
     * @return getPlaylistSongsResult result object containing the playlist's list of API defined
     *                                {@link SongModel}s
     */
    @Override
    public GetPlaylistSongsResult handleRequest(final GetPlaylistSongsRequest getPlaylistSongsRequest,
                                                Context context) {
        log.info("Received GetPlaylistSongsRequest {}", getPlaylistSongsRequest);

        String requestedId = getPlaylistSongsRequest.getId();
        Playlist playlist = playlistDao.getPlaylist(requestedId);

        List<SongModel> songModelList = new ArrayList<>();

        for (AlbumTrack albumTrack : playlist.getSongList()) {
            SongModel songModel = new ModelConverter().toSongModel(albumTrack);

            songModelList.add(songModel);
        }

        if (getPlaylistSongsRequest.getOrder() != null) {
            if (getPlaylistSongsRequest.getOrder().equals(SongOrder.SHUFFLED)) {
                Collections.shuffle(songModelList);
            } else if (getPlaylistSongsRequest.getOrder().equals(SongOrder.REVERSED)) {
                Collections.reverse(songModelList);
            }
        }

        return GetPlaylistSongsResult.builder()
                .withSongList(songModelList)
                .build();
    }

}
