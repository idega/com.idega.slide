package com.idega.slide.business;

import java.util.logging.Level;

import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.common.SlideCommonUtil;
import org.apache.slide.common.SlideToken;
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.macro.Macro;
import org.apache.slide.macro.MacroImpl;
import org.apache.slide.structure.ObjectNotFoundException;

import com.idega.slide.bean.SlideAction;

public class DeleteWorker extends RepositoryWorker {

	private String deletePath;
	
	public DeleteWorker(IWSlideServiceBean repositoryService, IWSimpleSlideServiceImp simpleRepositoryService, String repositoryPath) {
		super(repositoryService, simpleRepositoryService, repositoryPath);
		
		deletePath = repositoryPath;
	}
	
	@Override
	protected void doWork() {
		try {
			IWSimpleSlideServiceImp simpleRepositoryService = getSimpleRepositoryService();
			
			if (!simpleRepositoryService.checkExistance(deletePath)) {
				result = Boolean.FALSE;
				return;
			}
			
			NamespaceAccessToken namespace = getSimpleRepositoryService().startTransaction(SlideAction.COMMIT);
			if (namespace == null) {
				result = Boolean.FALSE;
				return;
			}
			
			boolean deleteXML = true;
			try {
				SlideToken token = simpleRepositoryService.getContentToken();
				org.apache.slide.common.Namespace slideNamespace = SlideCommonUtil.getInstance().getDefaultNamespace();

				Macro macro = new MacroImpl(slideNamespace, slideNamespace.getConfig(), simpleRepositoryService.getSecurity(), simpleRepositoryService.getContent(),
						simpleRepositoryService.getStructure(), namespace.getLockHelper());
				macro.delete(token, deletePath);
				
				simpleRepositoryService.removeValueFromCache(IWSimpleSlideServiceImp.CACHE_RESOURCE_DESCRIPTORS_NAME, IWSimpleSlideServiceImp.THREE_MINUTES,
						deletePath);
				simpleRepositoryService.removeValueFromCache(IWSimpleSlideServiceImp.CACHE_RESOURCE_DESCRIPTOR_NAME, IWSimpleSlideServiceImp.THREE_MINUTES,
						deletePath);
				simpleRepositoryService.removeValueFromCache(IWSimpleSlideServiceImp.CACHE_RESOURCE_EXISTANCE_NAME, -1, deletePath);
			} catch (Throwable t) {
				getLogger().log(Level.WARNING, "Unable to delete: " + deletePath, t);
				deleteXML = t instanceof ObjectNotFoundException || t instanceof RevisionDescriptorNotFoundException;
				if (!deleteXML) {
					simpleRepositoryService.rollbackTransaction(namespace);
					result = Boolean.FALSE;
					return;
				}
			} finally {
				if (deleteXML) {
					simpleRepositoryService.deletetDefinitionFile(deletePath);
				}
			}
			
			simpleRepositoryService.commitTransaction(namespace);
			result = Boolean.TRUE;
		} catch (Throwable t) {
			result = Boolean.FALSE;
			getLogger().log(Level.WARNING, "Error deleting: " + deletePath, t);
		} finally {
			getRepositoryService().removeFromQueue(getRepositoryPath(), getWorkId());
		}
	}

}