package com.amazon.ata.music.playlist.service.dynamodb;

import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AlbumTrackDaoTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @InjectMocks
    private AlbumTrackDao albumTrackDao;

//******************************************************************************************************************

    private String asin;
    private int trackNumber;
    private String albumName;
    private String songTitle;
    private AlbumTrack albumTrack;
    private String nonExistingAsin;

//******************************************************************************************************************

    @BeforeEach
    public void setUp() {
        initMocks(this);
        albumTrackDao = new AlbumTrackDao(dynamoDBMapper);

        asin = "123456789";
        trackNumber = 7;
        albumName = "For All the Dogs";
        songTitle = "Your Mom";

        albumTrack = new AlbumTrack();
        albumTrack.setAsin(asin);
        albumTrack.setTrackNumber(trackNumber);
        albumTrack.setAlbumName(albumName);
        albumTrack.setSongTitle(songTitle);
    }

//******************************************************************************************************************

    @Test
    public void getAlbumTrack_validAsinValidTrackNumber_loadsExistingAlbumTrack() {
        // GIVEN
        when(dynamoDBMapper.load(AlbumTrack.class, asin, trackNumber)).thenReturn(albumTrack);

        // WHEN
        AlbumTrack result = albumTrackDao.getAlbumTrack(asin, trackNumber);

        // THEN
        assertEquals(asin, result.getAsin(), "AlbumTrack ASINs should be the same.");
        assertEquals(trackNumber, result.getTrackNumber(),
                "AlbumTrack track numbers should be the same.");
        assertEquals(albumName, result.getAlbumName(),
                "AlbumTrack album names should be the same.");
        assertEquals(songTitle, result.getSongTitle(),
                "AlbumTrack song titles should be the same.");
    }

    @Test
    public void getPlaylist_nonExistingAsin_throwsAlbumTrackNotFoundException() {
        // GIVEN
        nonExistingAsin = "drakeandjcolearegoingontourtogetherandiwillbethere";
        when(dynamoDBMapper.load(AlbumTrack.class, nonExistingAsin, trackNumber)).thenReturn(null);

        // WHEN + THEN
        assertThrows(AlbumTrackNotFoundException.class, ()->{
            albumTrackDao.getAlbumTrack(nonExistingAsin, trackNumber);
        });
        verify(dynamoDBMapper).load(AlbumTrack.class, nonExistingAsin, trackNumber);
    }

    // <getAlbumTrack()> tests
//******************************************************************************************************************

}
