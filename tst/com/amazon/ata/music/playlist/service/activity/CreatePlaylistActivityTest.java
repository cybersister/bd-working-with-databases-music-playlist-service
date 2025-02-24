package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.models.requests.CreatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.CreatePlaylistResult;
import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.beust.jcommander.internal.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CreatePlaylistActivityTest {

    @Mock
    private PlaylistDao playlistDao;

    @InjectMocks
    private CreatePlaylistActivity createPlaylistActivity;

//******************************************************************************************************************

    private String randomUUID;
    private String validName;
    private String invalidName;
    private String validCustomerId;
    private String invalidCustomerId;
    private List<String> listOfTags;
    private Integer zeroSongCount;
    private List<AlbumTrack> emptySongList;
    private Playlist playlist;

//******************************************************************************************************************

    @BeforeEach
    public void setUp() {
        initMocks(this);
        createPlaylistActivity = new CreatePlaylistActivity(playlistDao);

        randomUUID = MusicPlaylistServiceUtils.generatePlaylistId();
        validName = "october 2023";
        validCustomerId = "maddiodie";
        listOfTags = Lists.newArrayList("monthly");
        zeroSongCount = 0;
        emptySongList = new ArrayList<>();

        playlist = new Playlist();
    }

//******************************************************************************************************************

    @Test
    public void handleRequest_validParametersWithNonEmptyListOfTags_returnsPlaylistWithEmptySongList() {
        // GIVEN
        CreatePlaylistRequest request = CreatePlaylistRequest.builder()
                .withName(validName)
                .withCustomerId(validCustomerId)
                .withTags(listOfTags)
                .build();

        playlist = helperMethod(randomUUID, validName, validCustomerId, zeroSongCount, listOfTags,
                emptySongList);
        // calling helper method

        when(playlistDao.savePlaylist(validName, validCustomerId, listOfTags))
                .thenReturn(playlist);

        // WHEN
        CreatePlaylistResult result = createPlaylistActivity.handleRequest(request, null);

        // THEN
        assertEquals(randomUUID, result.getPlaylist().getId(),
                "Playlist ID's should be the same.");
        assertEquals(validName, result.getPlaylist().getName(),
                "Playlist names should be the same.");
        assertEquals(validCustomerId, result.getPlaylist().getCustomerId(),
                "Playlist customer ID's should be the same.");
        assertEquals(zeroSongCount, result.getPlaylist().getSongCount(),
                "Playlist song counts should be the same.");
        assertEquals(listOfTags, result.getPlaylist().getTags(),
                "Playlist tags should be the same.");
        assertTrue(playlist.getSongList().isEmpty());
    }
    // includes a non-empty list of tags

    @Test
    public void handleRequest_invalidCustomerId_throwsInvalidAttributeValueException() {
        // GIVEN
        invalidCustomerId = "'maddiodie'";

        CreatePlaylistRequest request = CreatePlaylistRequest.builder()
                .withName(validName)
                .withCustomerId(invalidCustomerId)
                .withTags(listOfTags)
                .build();

        // WHEN
        when(playlistDao.savePlaylist(validName, invalidCustomerId, listOfTags))
                .thenThrow(new InvalidAttributeValueException("Given Customer ID: "
                        + request.getCustomerId() + " is invalid."));
        // THEN
        assertThrows(InvalidAttributeValueException.class, ()->{
            playlistDao.savePlaylist(validName, invalidCustomerId, listOfTags);
        });
        verify(playlistDao).savePlaylist(validName, invalidCustomerId, listOfTags);
    }

    @Test
    public void handleRequest_invalidName_throwsInvalidAttributeValueException() {
        // GIVEN
        invalidName = "october\\2023";

        CreatePlaylistRequest request = CreatePlaylistRequest.builder()
                .withName(invalidName)
                .withCustomerId(validCustomerId)
                .withTags(listOfTags)
                .build();

        // WHEN
        when(playlistDao.savePlaylist(invalidName, validCustomerId, listOfTags))
                .thenThrow(new InvalidAttributeValueException("Given Customer ID: "
                        + request.getCustomerId() + " is invalid."));
        // THEN
        assertThrows(InvalidAttributeValueException.class, ()->{
            playlistDao.savePlaylist(invalidName, validCustomerId, listOfTags);
        });
        verify(playlistDao).savePlaylist(invalidName, validCustomerId, listOfTags);
    }

    @Test
    public void handleRequest_validParametersWithNullListOfTags_returnsPlaylistWithEmptyListOfTags() {
        // GIVEN
        List<String> nullListOfTags = null;
        List<String> emptyListOfTags = new ArrayList<>();

        CreatePlaylistRequest request = CreatePlaylistRequest.builder()
                .withName(validName)
                .withCustomerId(validCustomerId)
                .withTags(nullListOfTags)
                .build();

        playlist = helperMethod(randomUUID, validName, validCustomerId, zeroSongCount,
                nullListOfTags, emptySongList);

        when(playlistDao.savePlaylist(validName, validCustomerId, nullListOfTags))
                .thenReturn(playlist);

        // WHEN
        CreatePlaylistResult result = createPlaylistActivity.handleRequest(request, null);

        // THEN
        assertTrue(result.getPlaylist().getTags().isEmpty(),
                "Playlist tags should be empty.");
        assertEquals(emptyListOfTags, result.getPlaylist().getTags(),
                "Playlist tags should be empty.");
    }

//******************************************************************************************************************

    private Playlist helperMethod(String id, String name, String customerId, Integer songCount,
                                  List<String> tags, List<AlbumTrack> songList) {
        playlist.setId(id);
        playlist.setName(name);
        playlist.setCustomerId(customerId);
        playlist.setSongCount(songCount);

        if (tags == null) {
            playlist.setTags(new HashSet<>());
        } else {
            playlist.setTags(new HashSet<>(tags));
        }

        playlist.setSongList(songList);

        return playlist;
    }

}
