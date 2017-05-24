/*
 * Copyright (C) 2015 Nicola Cadenelli (nicolacdnll@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package jmicro.vlcjutils;

import java.awt.Canvas;
import java.awt.Window;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

/**
 *
*
 */
public class MyCanvas extends Canvas{
    
    private final MediaPlayerFactory mediaPlayerFactory; 
    private final CanvasVideoSurface videoSurface;
    private final EmbeddedMediaPlayer mediaPlayer;
    
            
    public MyCanvas(MediaPlayerEventAdapter mpea){
            mediaPlayerFactory  = new MediaPlayerFactory("--no-video-title-show");
            videoSurface        = mediaPlayerFactory.newVideoSurface(this);
            mediaPlayer         = mediaPlayerFactory.newEmbeddedMediaPlayer();
            mediaPlayer.setVideoSurface(videoSurface);
            mediaPlayer.addMediaPlayerEventListener(mpea);
//mediaPlayer.setVideoSurface(this);
          mediaPlayer.enableOverlay(true);     
//mediaPlayer.setVideoTitleDisplay(libvlc_position_e.disable, 0); This function requires libvlc 2.1.0 or later
    }
    
    public void restartMedia() {
        mediaPlayer.play();
    }
    
    public void playMedia(String file) {
        mediaPlayer.playMedia(file);
    }
    
    public void stopMedia(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }
    
    public void pauseMedia(){
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }
    
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    
    public void setOverlay(Window w){
        mediaPlayer.setOverlay(w);
    }
    
//    @Override
//    public Dimension minimumSize() {
//        return new Dimension (200,200);
//    }
//            
//    @Override
//    public Dimension preferredSize() {
//        return new Dimension (200,200);
//    }
}

