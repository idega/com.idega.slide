package org.apache.slide.extractor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.util.conf.Configuration;
import org.apache.slide.util.conf.ConfigurationException;

public class IWOfficeExtractor extends OfficeExtractor {

	public IWOfficeExtractor(String uri, String contentType, String namespace) {
		super(uri, contentType, namespace);
	}

	@Override
	public void configure(Configuration config) throws ConfigurationException {
		super.configure(config);
	}

	@Override
	public Map<?, ?> extract(NodeRevisionDescriptors nrds, NodeRevisionDescriptor nrd, InputStream stream) throws ExtractorException {
		Map<?, ?> properties = null;
		try {
			properties = super.extract(nrds, nrd, stream);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (properties == null)
			properties = new HashMap<Object, Object>();
		return properties;
	}

	@Override
	public String getContentType() {
		return super.getContentType();
	}
	
}