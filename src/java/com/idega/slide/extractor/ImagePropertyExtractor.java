package com.idega.slide.extractor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.extractor.AbstractPropertyExtractor;
import org.apache.slide.extractor.ExtractorException;

import com.idega.graphics.ImageInfo;
import com.idega.slide.util.IWSlideConstants;

/**
 * Extracts image information; height,width,bits per pixel and dpi. <br>
 * Based on the open source projects ImageInfo and code from Hippo CMS
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */
public class ImagePropertyExtractor extends AbstractPropertyExtractor {

	private static final String HEIGHT_PROPERTY = IWSlideConstants.PROPERTYNAME_HEIGHT;
	private static final String WIDTH_PROPERTY = IWSlideConstants.PROPERTYNAME_WIDTH;
	private static final String BITS_PER_PIXEL_PROPERTY = IWSlideConstants.PROPERTYNAME_BITS_PER_PIXEL;
	private static final String DPI_PROPERTY = IWSlideConstants.PROPERTYNAME_DPI;
	private static final String WIDTH_AND_HEIGHT_PROPERTY = IWSlideConstants.PROPERTYNAME_WIDTH_AND_HEIGHT_PROPERTY;

	public ImagePropertyExtractor(String namespace, String uri, String contentType) {
		super(namespace, uri, contentType);
	}

	public Map extract(NodeRevisionDescriptors descriptors, NodeRevisionDescriptor descriptor, InputStream content) throws ExtractorException {
		Map result = new HashMap();
		ImageInfo ii = new ImageInfo();
		ii.setInput(content);
		if (ii.check()) {
			final int width = ii.getWidth();
			final int height = ii.getHeight();
			final int bitsPerPixel = ii.getBitsPerPixel();
			final int widthDpi = ii.getPhysicalWidthDpi();
			result.put(HEIGHT_PROPERTY, String.valueOf(height));
			result.put(WIDTH_PROPERTY, String.valueOf(width));
			result.put(WIDTH_AND_HEIGHT_PROPERTY, String.valueOf(width) + "x" + String.valueOf(height));
			result.put(BITS_PER_PIXEL_PROPERTY, String.valueOf(bitsPerPixel));
			if (widthDpi != -1) {
				result.put(DPI_PROPERTY, String.valueOf(widthDpi));
			}
		}
		return result;
	}
}
