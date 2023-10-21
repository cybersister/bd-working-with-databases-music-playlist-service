package com.amazon.ata.music.playlist.service.dynamodb;

import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;

public class PlaylistDaoTest {

    @Mock
    private DynamoDBMapper dynamoDbMapper;

    @InjectMocks
    private PlaylistDao playlistDao;

//******************************************************************************************************************

    private Playlist playlist;
    private String id;
    private String name;
    private String customerId;
    private Set<String> setOfTags;
    private Integer songCount;
    private List<AlbumTrack> songs;
    private String nonExistingId;
    private String invalidCustomerId;
    private List<String> listOfTags;
    private String invalidName;
    private Set<String> emptySetOfTags;
    private List<String> emptyListOfTags;
    private List<AlbumTrack> emptyListOfSongs;
    private Integer nullSongCount;

//******************************************************************************************************************

    @BeforeEach
    public void setUp() {
        initMocks(this);
        playlistDao = new PlaylistDao(dynamoDbMapper);

        id = "69";
        name = "Where Duh Hoes At?";
        customerId = "maddiodie";
        setOfTags = new HashSet<>();
        setOfTags.add("nntz");
        songCount = 1;
        songs = new ArrayList<>();
        songs.add(new AlbumTrack());

        playlist = new Playlist();
        playlist.setId(id);
        playlist.setName(name);
        playlist.setCustomerId(customerId);
        playlist.setTags(setOfTags);
        playlist.setSongCount(songCount);
        playlist.setSongList(songs);

        listOfTags = new ArrayList<>();
        listOfTags.add("nntz");
    }

//******************************************************************************************************************

    @Test
    public void getPlaylist_validId_loadsExistingPlaylist() {
        // GIVEN
        when(dynamoDbMapper.load(Playlist.class, id)).thenReturn(playlist);

        // WHEN
        Playlist result = playlistDao.getPlaylist(id);

        // THEN
        assertEquals(id, result.getId(), "Playlist IDs should be the same");
        assertEquals(name, result.getName(), "Playlist names should be the same.");
        assertEquals(customerId, result.getCustomerId(),
                "Playlist customer ID's should be the same.");
        assertEquals(setOfTags, result.getTags(), "Playlist list of tags should be the same.");
        assertEquals(songCount, result.getSongCount(),
                "Playlist song counts should be the same.");
        assertEquals(songs, result.getSongList(), "Playlist list of songs should be the same.");
    }

    @Test
    public void getPlaylist_nonExistingId_throwsPlaylistNotFoundException() {
        // GIVEN
        nonExistingId = "drakeisthemfgoat";
        when(dynamoDbMapper.load(Playlist.class, nonExistingId)).thenReturn(null);

        // WHEN + THEN
        assertThrows(PlaylistNotFoundException.class, ()->{
            playlistDao.getPlaylist(nonExistingId);
        });
        verify(dynamoDbMapper).load(Playlist.class, nonExistingId);
    }

    // <getPlaylist()> tests
//******************************************************************************************************************

    @Test
    public void savePlaylist_invalidCustomerId_throwsInvalidAttributeException() {
        // GIVEN
        invalidCustomerId = "maddio'die";

        // WHEN + THEN
        assertThrows(InvalidAttributeValueException.class, ()->{
            playlistDao.savePlaylist(name, invalidCustomerId, listOfTags);
        });
    }

    @Test
    public void savePlaylist_invalidName_throwsInvalidAttributeException() {
        // GIVEN
        invalidName = "Your \"Momma\"!";

        // WHEN + THEN
        assertThrows(InvalidAttributeValueException.class, ()->{
            playlistDao.savePlaylist(invalidName, customerId, listOfTags);
        });
    }

    @Test
    public void savePlaylist_nullObjectForTags_returnsPlaylistWithEmptyHashSet() {
        // GIVEN
        emptySetOfTags = new HashSet<>();

        // WHEN
        Playlist result = playlistDao.savePlaylist(name, customerId, null);

        // THEN
        assertEquals(name, result.getName(), "Playlist names should be the same.");
        assertEquals(customerId, result.getCustomerId(),
                "Playlist customer ID's should be the same.");
        assertEquals(emptySetOfTags, result.getTags(),
                "Playlist list of tags should be the same.");

        verify(dynamoDbMapper).save(result);
    }

    @Test
    public void savePlaylist_emptyListOfTags_throwsInvalidAttributeException() {
        // GIVEN
        emptyListOfTags = new ArrayList<>();

        // WHEN + THEN
        assertThrows(InvalidAttributeValueException.class, ()->{
            playlistDao.savePlaylist(name, customerId, emptyListOfTags);
        });
    }

    @Test
    public void savePlaylist_validParameters_returnsPlaylist() {
        // GIVEN
        emptyListOfSongs = new ArrayList<>();
        nullSongCount = null;

        // WHEN
        Playlist result = playlistDao.savePlaylist(name, customerId, listOfTags);

        // THEN
        assertEquals(name, result.getName(), "Playlist names should be the same.");
        assertEquals(customerId, result.getCustomerId(),
                "Playlist customer ID's should be the same.");
        assertEquals(setOfTags, result.getTags(),
                "Playlist list of tags should be the same.");
        assertEquals(nullSongCount, result.getSongCount(),
                "Playlist song counts should be the same.");
        assertEquals(emptyListOfSongs, result.getSongList(),
                "Playlist list of songs should be the same.");

        verify(dynamoDbMapper).save(result);
    }

    // <savePlaylist()> tests
//******************************************************************************************************************

}
