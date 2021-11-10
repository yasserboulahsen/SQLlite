package com.yasser;

import model.Artist;
import model.Datasource;
import model.SongArtist;

import java.util.List;

public class Main {

    public static void main(String[] args) {
            Datasource datasource = new Datasource();

            if (!datasource.open()) {
                System.out.println("Can't open Datasource!");
                return;
            }
            List<Artist> artists = datasource.queryArtists(Datasource.ORDER_BY_DESC);

            if (artists == null) {
                System.out.println("No artits");
                return;
            }
            artists.forEach(artist -> {
                System.out.println("ID = " + artist.getId() + ", Name = " + artist.getName());
            });

            List<String> albumsForArtits =
                    datasource.queryAlbumForArtist("Carole King",Datasource.ORDER_BY_ASC);

            albumsForArtits.forEach(album->{
                System.out.println("Album Name = "+album);
            });



            List<SongArtist> songArtists = datasource.queryArtistForSong("Heartless",Datasource.ORDER_BY_NONE);


            if(songArtists == null) {
                System.out.println("Couldn't find the artist");
                return;
            }
        songArtists.forEach(songArtist -> {
            System.out.println("Artist name : "+songArtist.getArtistName()+
                    "Track"+songArtist.getTrack()+
                    "Album name : "+songArtist.getAlbumName());
        });

        datasource.close();
    }
}
