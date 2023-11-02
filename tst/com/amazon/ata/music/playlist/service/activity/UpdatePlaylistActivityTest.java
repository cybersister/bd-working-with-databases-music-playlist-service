package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeChangeException;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.requests.UpdatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.UpdatePlaylistResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdatePlaylistActivityTest {

    @Mock
    private PlaylistDao playlistDao;
    @InjectMocks
    private UpdatePlaylistActivity updatePlaylistActivity;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        updatePlaylistActivity = new UpdatePlaylistActivity(playlistDao);
    }

    @Test
    public void handleRequest_goodRequest_updatesPlaylistName() {
        // GIVEN
        String id = "id";
        String customerId = "customerId";
        String playlistName = "new name";

        UpdatePlaylistRequest request = UpdatePlaylistRequest.builder()
                                            .withId(id)
                                            .withCustomerId(customerId)
                                            .withName(playlistName)
                                            .build();

        Playlist endingPlaylist = new Playlist();
        endingPlaylist.setCustomerId(customerId);
        endingPlaylist.setName("new name");
        endingPlaylist.setSongCount(0);

        when(playlistDao.updatePlaylist(request.getId(), request.getName(), request.getCustomerId()))
                .thenReturn(endingPlaylist);

        // WHEN
        UpdatePlaylistResult result = updatePlaylistActivity.handleRequest(request, null);

        // THEN
        assertEquals(playlistName, result.getPlaylist().getName());
        assertEquals(customerId, result.getPlaylist().getCustomerId());
    }

    @Test
    public void handleRequest_invalidName_throwsInvalidAttributeValueException() {
        // GIVEN
        UpdatePlaylistRequest request = UpdatePlaylistRequest.builder()
                                            .withId("id")
                                            .withName("I'm illegal")
                                            .withCustomerId("customerId")
                                            .build();
        when(playlistDao.updatePlaylist(request.getId(), request.getName(), request.getCustomerId()))
                .thenThrow(InvalidAttributeValueException.class);

        // WHEN + THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                updatePlaylistActivity.handleRequest(request, null)
        );
    }

    @Test
    public void handleRequest_playlistDoesNotExist_throwsPlaylistNotFoundException() {
        // GIVEN
        UpdatePlaylistRequest request = UpdatePlaylistRequest.builder()
                                            .withId("different")
                                            .withName("name")
                                            .withCustomerId("customerId")
                                            .build();
        when(playlistDao.updatePlaylist(request.getId(), request.getName(), request.getCustomerId()))
                .thenThrow(PlaylistNotFoundException.class);

        // WHEN + THEN
        assertThrows(PlaylistNotFoundException.class, () ->
                updatePlaylistActivity.handleRequest(request, null)
        );
    }

    @Test
    public void handleRequest_customerIdNotMatch_throwsInvalidAttributeChangeException() {
        // GIVEN
        UpdatePlaylistRequest request = UpdatePlaylistRequest.builder()
                                            .withId("id")
                                            .withName("name")
                                            .withCustomerId("different")
                                            .build();
        when(playlistDao.updatePlaylist(request.getId(), request.getName(),
                request.getCustomerId()))
                .thenThrow(InvalidAttributeChangeException.class);

        // WHEN + THEN
        assertThrows(InvalidAttributeChangeException.class, () ->
                updatePlaylistActivity.handleRequest(request, null)
        );
    }

}
