/*
 * Copyright (C) 2020 tonikelope
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
package com.tonikelope.coronapoker;

import java.awt.Image;
import javax.swing.Icon;
import javax.swing.JLabel;

//Thanks to -> https://stackoverflow.com/a/42079313
public class GifLabel extends JLabel {

    private volatile int frames = 0;
    private volatile int conta_frames = 0;
    private volatile String audio = null;
    private volatile int audio_frame_start = -1;
    private volatile int audio_frame_end = -1;
    private volatile boolean gif_finished = false;
    private volatile Object gif_finished_notifier = null;
    private volatile boolean audio_playing = false;

    public boolean isGif_finished() {
        return gif_finished;
    }

    @Override
    public void setIcon(Icon icon) {
        gif_finished = false;
        conta_frames = 0;
        audio = null;
        audio_playing = false;
        super.setIcon(icon);
    }

    public void setIcon(Icon icon, int frames) {
        this.frames = frames;
        setIcon(icon);
    }

    public void setNotifier(Object notifier) {
        gif_finished_notifier = notifier;
    }

    public void addAudio(String audio, int start_frame, int end_frame) {
        if (!audio_playing && audio != null && start_frame < end_frame && start_frame > 0) {
            this.audio = audio;
            this.audio_frame_start = start_frame;
            this.audio_frame_end = end_frame;
        }
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {

        if (!gif_finished) {

            if ((infoflags & FRAMEBITS) != 0) {

                conta_frames++;

                if (audio != null) {

                    if (!audio_playing && conta_frames == this.audio_frame_start) {
                        audio_playing = true;
                        Audio.playWavResource(audio);
                    } else if (audio_playing && conta_frames == this.audio_frame_end) {
                        audio_playing = false;
                        Audio.stopWavResource(audio);
                        audio = null;
                    }

                }
            }

            boolean imageupdate = super.imageUpdate(img, infoflags, x, y, w, h);

            gif_finished = !imageupdate || (frames != 0 && conta_frames == frames);

            if (gif_finished && gif_finished_notifier != null) {

                synchronized (gif_finished_notifier) {
                    gif_finished_notifier.notifyAll();
                }
            }

            return imageupdate;
        } else {
            return true;
        }
    }

}
