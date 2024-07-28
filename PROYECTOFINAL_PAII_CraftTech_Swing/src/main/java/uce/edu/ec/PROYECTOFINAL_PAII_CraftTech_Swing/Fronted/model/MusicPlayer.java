package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_Swing.Fronted.model;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MusicPlayer {
    private List<String> playlist;
    private AdvancedPlayer player;
    private ExecutorService executor;
    private Future<?> currentTask;
    private int currentTrackIndex;
    private boolean isPaused;
    private volatile boolean isPlaying;
    private volatile boolean isPausedRequest;

    public MusicPlayer() {
        this.playlist = new ArrayList<>();
        initializePlaylist();
        this.executor = Executors.newSingleThreadExecutor();
        this.currentTrackIndex = 0;
        this.isPaused = false;
        this.isPlaying = false;
        this.isPausedRequest = false;
    }

    private void initializePlaylist() {
        playlist.add("C:\\Users\\bryan\\IdeaProjects\\Proyecto_API\\src\\res\\02. The Black.mp3");
        playlist.add("C:\\Users\\bryan\\IdeaProjects\\Proyecto_API\\src\\res\\09. Here I Am.mp3");
    }

    public void play() {
        if (playlist.isEmpty()) {
            System.out.println("No hay canciones en la lista de reproducción.");
            return;
        }

        if (currentTask != null && !currentTask.isDone()) {
            if (isPlaying) {
                // Pausar la música si ya está reproduciéndose
                isPausedRequest = !isPausedRequest;
                if (!isPausedRequest) {
                    resume();
                }
            } else {
                // Si no está reproduciéndose, iniciar la reproducción
                isPlaying = true;
                isPaused = false;
                currentTask = executor.submit(() -> playTrack());
            }
            return;
        }

        // Si no hay tarea actual o la tarea está completada
        isPlaying = true;
        isPaused = false;
        currentTask = executor.submit(() -> playTrack());
    }

    private void playTrack() {
        try {
            while (isPlaying) {
                if (isPausedRequest) {
                    isPaused = true;
                    return; // Salir del método para pausar la reproducción
                }

                String song = playlist.get(currentTrackIndex);
                FileInputStream fileInputStream = new FileInputStream(song);
                player = new AdvancedPlayer(fileInputStream);

                System.out.println("Reproduciendo: " + song);
                player.play(0, Integer.MAX_VALUE); // Reproduce toda la canción

                if (isPaused) {
                    isPaused = false;
                    // Mantener el hilo en espera mientras está en pausa
                    synchronized (this) {
                        wait();
                    }
                } else {
                    // Avanzar a la siguiente canción en la lista
                    nextTrack();
                }
            }
        } catch (JavaLayerException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void resume() {
        // Reanudar la reproducción
        notify(); // Despertar el hilo de espera
    }

    public void nextTrack() {
        if (playlist.isEmpty()) {
            System.out.println("No hay canciones en la lista de reproducción.");
            return;
        }

        currentTrackIndex = (currentTrackIndex + 1) % playlist.size();
        play();
    }
}