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

package jmicro.utils;

//http://stackoverflow.com/questions/11345343/png-image-files-to-video-lossless

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Size;

import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;

public class PngImagesToMovieJCodec {
    private final SeekableByteChannel ch;
    private final FramesMP4MuxerTrack outTrack;
    private int frameNo;
    private final MP4Muxer muxer;
    private Size size;
    private final long interval;
    private final long timescale = 25;

    public PngImagesToMovieJCodec (File out, int mseconds) throws IOException {
        this.ch = NIOUtils.writableFileChannel(out);

        // Muxer that will store the encoded frames
        muxer = new MP4Muxer(ch, Brand.MP4);

        // Add video track to muxer
        outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, 25);
        
        interval = (mseconds*timescale)/1000;
    }

    public void encodeImage(File png) throws IOException {
        if (size == null) {
            BufferedImage read = ImageIO.read(png);
            size = new Size(read.getWidth(), read.getHeight());
        }
        
        // Add packet to video track
        // MP4Packet(Buffer data, long pts, long timescale, long duration, long frameNo, boolean iframe, TapeTimecode tapeTimecode, long mediaPts, int entryNo)
        outTrack.addFrame(new MP4Packet(NIOUtils.fetchFrom(png), frameNo*interval, timescale, interval,     frameNo,        true,           null,                     frameNo*interval, 0));

        frameNo++;
    }

    public void finish() throws IOException {
        // Push saved SPS/PPS to a special storage in MP4
        outTrack.addSampleEntry(MP4Muxer.videoSampleEntry("png ", size, "JCodec"));

        // Write MP4 header and finalize recording
        muxer.writeHeader();
        NIOUtils.closeQuietly(ch);
    }
}