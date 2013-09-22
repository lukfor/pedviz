/* Copyright © 2007 by Christian Fuchsberger and Lukas Forer info@pedvizapi.org.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License <http://www.pedvizapi.org/gpl.txt>
 * for more details. 
 */

package pedviz.view;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Screen3D;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * An offscreen canvas with a print method that writes out a screen capture
 * image to a jpg file
 * 
 * @author lukas forer
 */
class Canvas3DExporter extends Canvas3D {
    boolean printing = false;

    String path;

    public Canvas3DExporter(GraphicsConfiguration gconfig) {
	super(gconfig, true);
    }

    public BufferedImage convertToGrayscale(BufferedImage source) {
	BufferedImageOp op = new ColorConvertOp(ColorSpace
		.getInstance(ColorSpace.CS_GRAY), null);
	return op.filter(source, null);
    }

    public void export(String path, Dimension dim, boolean toWait, boolean gray) {
	this.path = path;
	if (!toWait)
	    printing = true;
	BufferedImage bImage = new BufferedImage(dim.width, dim.height,
		BufferedImage.TYPE_INT_RGB);
	ImageComponent2D buffer = new ImageComponent2D(
		ImageComponent.FORMAT_RGB, bImage);
	buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
	this.setSize(new Dimension(dim.width, dim.height));
	// set up screen dimensions and aspect ratio
	Screen3D s = this.getScreen3D();
	s.setSize(new Dimension(dim.width, dim.height));
	s.setPhysicalScreenHeight(0.0254 / 90.0 * dim.height);
	s.setPhysicalScreenWidth(0.0254 / 90.0 * dim.width);
	this.setOffScreenBuffer(buffer);
	this.renderOffScreenBuffer();
	if (toWait) {
	    this.waitForOffScreenRendering();
	    drawOffScreenBuffer(gray);
	}

    }

    public void postSwap() {
	if (printing) {
	    super.postSwap();
	    drawOffScreenBuffer(false);
	    printing = false;
	}
    }

    void drawOffScreenBuffer(boolean gray) {
	BufferedImage bImage = this.getOffScreenBuffer().getImage();
	ImageComponent2D newImageComponent = new ImageComponent2D(
		ImageComponent.FORMAT_RGBA, bImage);
	// write that to disk....
	if (!path.endsWith(".jpg")) {
	    path = path + ".jpg";
	}
	try {
	    FileOutputStream out = new FileOutputStream(path);
	    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
	    if (gray) {
		bImage = convertToGrayscale(bImage);
	    }
	    JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bImage);
	    param.setQuality(1f, false); // 90% qualith JPEG

	    encoder.setJPEGEncodeParam(param);
	    encoder.encode(bImage);
	    out.close();
	} catch (IOException e) {
	    System.out.println(e);
	}
    }
}
