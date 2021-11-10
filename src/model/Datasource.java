package model;

import org.xml.sax.SAXException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {
    public static final String DB_NAME = "music.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\yasser\\IdeaProjects\\MusicDatabase\\" + DB_NAME;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;


    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";

    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;


    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;


    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;


    public static final String QUERY_ALBUMS_BY_ARTISTS_START =
            "SELECT "+ TABLE_ALBUMS+"."+COLUMN_ALBUM_NAME+" FROM "+TABLE_ALBUMS+
                    " INNER JOIN "+TABLE_ARTISTS+" ON "+ TABLE_ALBUMS+ "."+COLUMN_ALBUM_ARTIST+
                    " = "+TABLE_ARTISTS+"."+COLUMN_ARTIST_ID+
                    " WHERE "+TABLE_ARTISTS+"."+COLUMN_ARTIST_NAME+" = \"";
    public static final String QUERY_ALBUMS_BY_ARTIST_SORT=
            " ORDER BY "+TABLE_ALBUMS+"."+COLUMN_ALBUM_NAME+" COLLATE NOCASE";

public static final String QUERY_ARTIST_FOR_SONG_START ="Select "+TABLE_ARTISTS +"."+COLUMN_ARTIST_NAME+","+TABLE_ALBUMS+"."+COLUMN_ALBUM_NAME+
        ","+TABLE_SONGS+"."+COLUMN_SONG_TRACK +" from "+TABLE_SONGS+
        " INNER JOIN "+TABLE_ALBUMS+" ON "+TABLE_SONGS+"."+COLUMN_SONG_ALBUM+" = "+TABLE_ALBUMS+"."+ COLUMN_ALBUM_ID+
        " INNER JOIN "+TABLE_ARTISTS +" on "+ TABLE_ALBUMS+"."+COLUMN_ALBUM_ARTIST +" = "+ TABLE_ARTISTS+"."+COLUMN_ARTIST_ID +
        " WHERE "+ TABLE_SONGS+"."+COLUMN_SONG_TITLE +" =\"";

  public static final String  QUERY_ARTIST_BY_ARTIST_SORT= " order by "+ TABLE_ARTISTS+"."+COLUMN_ARTIST_NAME+","+TABLE_ALBUMS+"."+ COLUMN_ALBUM_NAME +" COLLATE nocase ";


    private Connection conn;


    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            return true;

        } catch (SQLException e) {
            System.out.println("Couldn't oprn database : " + e.getMessage());
            return false;

        }

    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();

            }
        } catch (SQLException e) {
            System.out.println("Couldn't close database : " + e.getMessage());
        }
    }

    public List<Artist> queryArtists(int sortOrder) {

        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_ARTISTS);
        if (sortOrder != ORDER_BY_NONE) {
            sb.append(" ORDER BY ");
            sb.append(COLUMN_ARTIST_NAME);
            sb.append(" COLLATE NOCASE ");
            if (sortOrder == ORDER_BY_ASC) {
                sb.append("DESC");

            } else {
                sb.append("ASC");
            }
        }


        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sb.toString())) {


            List<Artist> artists = new ArrayList<>();
            while (results.next()) {
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }

            return artists;
        } catch (SQLException e) {
            System.out.println("Query failed ! " + e.getMessage());
            return null;
        }
    }

    public List<String> queryAlbumForArtist(String artistName, int sortOredr) {
//        select albums.name from albums INNER JOIN artists on albums.artist = artists._id where  artists.name = "Pink Floyd" order by albums.name COLLATE nocase ASC
        StringBuilder sb = new StringBuilder(QUERY_ALBUMS_BY_ARTISTS_START);
        sb.append(artistName);
        sb.append("\"");

        sortTable(sortOredr, sb, QUERY_ALBUMS_BY_ARTIST_SORT);
        System.out.println("SQL statement = " +sb.toString());
        try(Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sb.toString())){
            List<String> albums = new ArrayList<>();
            while (results.next()){
                albums.add(results.getString(1));
            }

            return albums;

        }catch (SQLException e){
            System.out.println("Query failed ! " +e.getMessage());
            return null;
        }
    }

    public  List<SongArtist> queryArtistForSong(String songname,int sortOrder){
        StringBuilder sb = new StringBuilder(QUERY_ALBUMS_BY_ARTISTS_START);
        sb.append(songname);
        sb.append("\"");
        sortTable(sortOrder, sb, QUERY_ARTIST_BY_ARTIST_SORT);
        System.out.println("SQL Statement" +sb.toString());

        try(Statement statement = conn.createStatement();
       ResultSet results = statement.executeQuery(sb.toString())){
                List<SongArtist> songArtists = new ArrayList<>();
            while (results.next()){
                   SongArtist songArtist =new SongArtist();
                   songArtist.setArtistName(results.getString(1));
                   songArtist.setAlbumName(results.getString(2));
                   songArtist.setTrack(results.getInt(3));
                   songArtists.add(songArtist);
            }
            return songArtists;
        }catch (SQLException e){
            System.out.println("Query failed ! " + e.getMessage());
            return null;
        }

    }

    private void sortTable(int sortOrder, StringBuilder sb, String queryArtistByArtistSort) {
        if (sortOrder != ORDER_BY_NONE) {
            sb.append(queryArtistByArtistSort);
            if (sortOrder == ORDER_BY_DESC) {
                sb.append(" DESC");
            } else {
                sb.append(" ASC");
            }
        }
    }
}
