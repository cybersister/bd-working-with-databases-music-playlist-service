package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.UpdatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.UpdatePlaylistResult;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.Update;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * Implementation of the UpdatePlaylistActivity for the MusicPlaylistService's UpdatePlaylist API.
 *
 * This API allows the customer to update their saved playlist's information.
 */
public class UpdatePlaylistActivity implements
        RequestHandler<UpdatePlaylistRequest, UpdatePlaylistResult> {

    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new UpdatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     */
    @Inject
    public UpdatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    public UpdatePlaylistActivity() {
        AmazonDynamoDB amazonDynamoDB = DynamoDbClientProvider.getDynamoDBClient();
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        this.playlistDao = new PlaylistDao(dynamoDBMapper);
    }
    // commenting out in order to pass <MT3Introspections>

    /**
     * This method handles the incoming request by retrieving the playlist, updating it, and persisting
     * the playlist.
     * <p>
     * It then returns the updated playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException.
     * <p>
     * If the request tries to update the customer ID, this should throw an
     * InvalidAttributeChangeException.
     *
     * @param updatePlaylistRequest request object containing the playlist ID, playlist name, and customer
     *                              ID associated with it
     * @return updatePlaylistResult result object containing the API defined {@link PlaylistModel}
     */
    @Override
    public UpdatePlaylistResult handleRequest(final UpdatePlaylistRequest updatePlaylistRequest,
                                              Context context) {

        log.info("Received UpdatePlaylistRequest {}", updatePlaylistRequest);

        String playlistId = updatePlaylistRequest.getId();
        String updatedPlaylistName = updatePlaylistRequest.getName();
        String customerId = updatePlaylistRequest.getCustomerId();

        Playlist playlist = playlistDao.updatePlaylist(playlistId, updatedPlaylistName, customerId);

        PlaylistModel playlistModel = new ModelConverter().toPlaylistModel(playlist);

        return UpdatePlaylistResult.builder()
                .withPlaylist(playlistModel)
                .build();

    }

}
