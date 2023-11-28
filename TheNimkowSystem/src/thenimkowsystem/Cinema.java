/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package thenimkowsystem;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JFrame;

public class Cinema extends JFrame {

    public Cinema() {
        setTitle("Movies from TMDb");
        setSize(200, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPane);

        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/popular?api_key=f846867b6184611eeff179631d3f9e26&language=es-ES&page=1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray results = jsonObject.getAsJsonArray("results");

            for (JsonElement element : results) {
                JsonObject movie = element.getAsJsonObject();

                String title = movie.get("title").getAsString();
                String overview = movie.get("overview").getAsString();
                String posterPath = movie.get("poster_path").getAsString();
                int movieId = movie.get("id").getAsInt();
                 String vote_average = movie.get("vote_average").getAsString(); 

                URL movieDetailsURL = new URL("https://api.themoviedb.org/3/movie/" + movieId + "?api_key=f846867b6184611eeff179631d3f9e26&language=es-ES&append_to_response=credits");
                HttpURLConnection detailsConnection = (HttpURLConnection) movieDetailsURL.openConnection();
                detailsConnection.setRequestMethod("GET");

                StringBuilder detailsResponse;
                try (BufferedReader detailsReader = new BufferedReader(new InputStreamReader(detailsConnection.getInputStream()))) {
                    detailsResponse = new StringBuilder();
                    String detailsLine;
                    while ((detailsLine = detailsReader.readLine()) != null) {
                        detailsResponse.append(detailsLine);
                    }
                }

                JsonObject movieDetails = JsonParser.parseString(detailsResponse.toString()).getAsJsonObject();
                JsonObject credits = movieDetails.getAsJsonObject("credits");
                JsonArray cast = credits.getAsJsonArray("cast");
                String releaseDate = movieDetails.get("release_date").getAsString();

                JLabel titleLabel = new JLabel(title);
                JLabel overviewLabel = new JLabel(overview);
                JLabel releaseDateLabel = new JLabel("Release Date: " + releaseDate);

                BufferedImage img = loadImage(posterPath);
                JLabel picLabel = new JLabel(new ImageIcon(img));

                contentPane.add(picLabel);
                contentPane.add(titleLabel);
                contentPane.add(overviewLabel);
                contentPane.add(releaseDateLabel);

                for (JsonElement castMember : cast) {
                    String actorName = castMember.getAsJsonObject().get("name").getAsString();
                    JLabel actorLabel = new JLabel("Actor: " + actorName);
                    contentPane.add(actorLabel);
                }

                contentPane.add(Box.createRigidArea(new Dimension(0, 20))); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Cinema();
        });
    }

    private BufferedImage loadImage(String imageUrl) throws IOException {
        URL url = new URL("https://image.tmdb.org/t/p/w500" + imageUrl);
        return ImageIO.read(url);
    }
}