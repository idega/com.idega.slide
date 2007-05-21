package com.idega.slide.presentation;

import java.util.List;

import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.slide.util.IWSlideConstants;

public class IWSlidePropertyHandler implements ICPropertyHandler {

	public List getDefaultHandlerTypes() {
		return null;
	}

	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc, boolean oldGenerationHandler) {
		DropdownMenu menu = new DropdownMenu(name);
		
		try {
			menu.addMenuElement(IWSlideConstants.PROPERTYNAME_DISPLAY_NAME, "Name");
			menu.addMenuElement(IWSlideConstants.PROPERTYNAME_CONTENT_TYPE, "Type");
			menu.addMenuElement(IWSlideConstants.PROPERTYNAME_CONTENT_LENGTH, "Size");
			menu.addMenuElement(IWSlideConstants.PROPERTYNAME_LAST_MODIFIED, "Modification Date");
			menu.addMenuElement(IWSlideConstants.PROPERTYNAME_CREATION_DATE, "Creation Date");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (stringValue != null) {
			menu.setSelectedElement(stringValue);
		}
		return menu;
	}

	public void onUpdate(String[] values, IWContext iwc) {
	}

}
