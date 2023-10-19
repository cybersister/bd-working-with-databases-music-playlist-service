package com.amazon.ata.music.playlist.service.converters;

import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ModelConverter {

    /**
     * Converts a provided {@link Playlist} into a {@link PlaylistModel} representation.
     * @param playlist the playlist to convert
     * @return the converted playlist
     */
    public PlaylistModel toPlaylistModel(Playlist playlist) {
        return PlaylistModel.builder()
            .withId(playlist.getId())
            .withName(playlist.getName())
            .withCustomerId(playlist.getCustomerId())
            .withSongCount(playlist.getSongCount())
            .withTags(Lists.newArrayList(playlist.getTags()))
            .build();
        // fixme: it doesn't have a '.withSongList()' method apart of the builder ...
        //  do we think this needs to be updated? ... right off the bat, yes
    }

}
