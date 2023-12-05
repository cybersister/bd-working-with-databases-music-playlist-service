package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.models.requests.CreatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.CreatePlaylistResult;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of the CreatePlaylistActivity for the MusicPlaylistService's CreatePlaylist API.
 *
 * This API allows the customer to create a new playlist with no songs.
 */
public class CreatePlaylistActivity implements
        RequestHandler<CreatePlaylistRequest, CreatePlaylistResult> {

    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new CreatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlists table.
     */
    @Inject
    public CreatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    public CreatePlaylistActivity() {
        AmazonDynamoDB amazonDynamoDB = DynamoDbClientProvider.getDynamoDBClient();
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        this.playlistDao = new PlaylistDao(dynamoDBMapper);
    }
    // strictly for the aws lambda function ... requires default no-argument constructor

    // commenting out for the sake of PASSING a test

    /**
     * This method handles the incoming request by persisting a new playlist with the provided playlist
     * name and customer ID from the request.
     * <p>
     * It then returns the newly created playlist.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException.
     *
     * @param createPlaylistRequest request object containing the playlist name and customer ID associated
     *                              with it
     * @return createPlaylistResult result object containing the API defined {@link PlaylistModel}s
     */
    @Override
    public CreatePlaylistResult handleRequest(final CreatePlaylistRequest createPlaylistRequest,
                                              Context context) {

        log.info("Received CreatePlaylistRequest {}", createPlaylistRequest);

        String savedName = createPlaylistRequest.getName();
        String savedCustomerId = createPlaylistRequest.getCustomerId();
        List<String> savedTags = createPlaylistRequest.getTags();

        Playlist playlist = playlistDao.savePlaylist(savedName, savedCustomerId, savedTags);

        PlaylistModel playlistModel = new ModelConverter().toPlaylistModel(playlist);

        return CreatePlaylistResult.builder()
                .withPlaylist(playlistModel)
                .build();

    }

}
