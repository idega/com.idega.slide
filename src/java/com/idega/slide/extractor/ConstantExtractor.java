/*
 * Copyright 2004 Hippo Webworks.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.idega.slide.extractor;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.slide.common.PropertyName;
import org.apache.slide.extractor.AbstractPropertyExtractor;
import org.apache.slide.extractor.ExtractorException;
import org.apache.slide.util.conf.Configurable;
import org.apache.slide.util.conf.Configuration;
import org.apache.slide.util.conf.ConfigurationException;

/**
 * 
 * Puts constant properties on a document. Not really an extractor but handy.
 * With this extractor you could set a fixed property on all files within a certain folder.
 * You configure this extractor in Domain.xml.
 * 
 * @author <a href="mailto:m.pfingsthorn@hippo.nl">Max Pfingsthorn</a>
 */
public class ConstantExtractor extends AbstractPropertyExtractor implements Configurable
{
	private Map properties = new HashMap();
    
    public ConstantExtractor(String namespace, String uri, String contentType) {
        super(namespace, uri, contentType);
    }

   

    public Map extract(InputStream content) throws ExtractorException
	{
		return this.properties;
	}
    
    public void configure(Configuration configuration) throws ConfigurationException {
        Enumeration instructions = configuration.getConfigurations("instruction");
        
        
        while (instructions.hasMoreElements()) {
            Configuration instruction = (Configuration)instructions.nextElement();
            PropertyName propertyName = new PropertyName(
            		instruction.getAttribute("property"),
					instruction.getAttribute("namespace", "DAV:"));
            
            String value = instruction.getAttribute("value","");
            
            this.properties.put(propertyName,value);
        }
	}
}
