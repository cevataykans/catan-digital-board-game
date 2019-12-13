package SceneManagement;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    public enum Backtrack{
        GAME, OPENING, MAIN_MENU, PLAYER_SELECTION
    }

    public enum Effect{
        KNIGHT, VICTORY_POINT, CHANGE_OF_FORTUNE, CHAT, CITY_BUILD, COLLECT_MATERIAL, END_TURN, HARBOR,
        MONOPOLY, ROAD_BUILD, ROBBER, ROLL_DICE, SELECTION_SCREEN, SETTLEMENT_BUILT, TRADE,
        VICTORY, YEAR_OF_PLENTY, PERFECTLY_BALANCED, BUGLE
    }

    private static SoundManager soundManager = null;

    private Map<Backtrack, MediaPlayer> backtracks;
    private MediaPlayer currentBacktrack;

    private Map<Effect, MediaPlayer> effects;
    private MediaPlayer currentEffect;

    private SoundManager(){
        this.backtracks = new HashMap<>();
        this.effects = new HashMap<>();

        try {
            getSoundsFromFile();
        } catch(URISyntaxException e){
            e.printStackTrace();
        }

        this.currentBacktrack = null;
        this.currentEffect = null;
    }

    public static SoundManager getInstance(){
        if(soundManager == null)
            soundManager = new SoundManager();
        return soundManager;
    }

    private void getSoundsFromFile() throws URISyntaxException {
        for(Backtrack backtrack: Backtrack.values()){
            URL file = SoundManager.class.getResource("../sounds/Backtracks/" + backtrack.name() + ".wav");
            final Media media = new Media(file.toURI().toString());
            final MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.5);
            backtracks.put(backtrack ,mediaPlayer);
        }

        for(Effect effect: Effect.values()){
            URL file = SoundManager.class.getResource("../sounds/Effects/" + effect.name() + ".wav");
            final Media media = new Media(file.toURI().toString());
            final MediaPlayer mediaPlayer = new MediaPlayer(media);
            effects.put(effect ,mediaPlayer);
        }
    }

    public void playBacktrack(Backtrack music){
        if(this.currentBacktrack == backtracks.get(music))
            return;
        if(this.currentBacktrack != null) {
            fadeSoundOut(this.currentBacktrack);
        }
        this.currentBacktrack = backtracks.get(music);
        fadeSoundIn(this.currentBacktrack);
    }

    public void playEffect(Effect effect){
        if(this.currentEffect != null)
            this.currentEffect.stop();
        this.currentEffect = effects.get(effect);
        this.currentEffect.play();
    }

    private void fadeSoundOut(MediaPlayer mediaPlayer){
        MediaPlayer mp = mediaPlayer;
        Timer timer = new Timer(5, null);
        timer.addActionListener(new ActionListener() {
            double soundLevel = 0.5;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(soundLevel <= 0){
                    timer.stop();
                    mp.stop();
                }
                else{
                    mp.setVolume(soundLevel);
                    soundLevel = soundLevel - 0.02;
                }
            }
        });
        timer.start();
    }

    private void fadeSoundIn(MediaPlayer mediaPlayer){
        MediaPlayer mp = mediaPlayer;
        mp.play();
        Timer timer = new Timer(5, null);
        timer.addActionListener(new ActionListener() {
            double soundLevel = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(soundLevel >= 0){
                    timer.stop();
                    mp.setVolume(0);
                }
                else{
                    mp.setVolume(soundLevel);
                    soundLevel = soundLevel + 0.02;
                }
            }
        });
        timer.start();
    }

}
