package com.idega.slide.business;

import java.io.InputStream;
import java.util.logging.Level;

import com.idega.core.file.util.MimeTypeUtil;
import com.idega.util.StringUtil;

public class UploadWorker extends RepositoryWorker {

	private String fileName;
	private String contentType;

	private InputStream stream;

	private boolean closeStream;

	public UploadWorker(IWSlideServiceBean slideService, String uploadPath, String fileName, String contentType, InputStream stream, boolean closeStream) {
		super(slideService, uploadPath);

		this.fileName = fileName;
		this.contentType = contentType;
		this.stream = stream;
		this.closeStream = closeStream;
	}

	@Override
	protected void doWork() {
		IWSlideServiceBean repositoryService = getRepositoryService();
		String uploadPath = getRepositoryPath();

		try {
			String fixedUploadPath = repositoryService.createFoldersAndPreparedUploadPath(uploadPath, true);
			if (uploadPath == null) {
				getLogger().warning("Can not upload: " + uploadPath + fileName);
				return;
			}

			if (StringUtil.isEmpty(contentType)) {
				contentType = MimeTypeUtil.resolveMimeTypeFromFileName(fileName);
			}

			result = repositoryService.getSimpleSlideService().upload(stream, fixedUploadPath, fileName, contentType, null, closeStream);
		} catch (Throwable t) {
			getLogger().log(Level.WARNING, "Error uploading '" + uploadPath + fileName + "' using Slide API. Will try to upload using common API", t);
		} finally {
			repositoryService.removeFromQueue(getRepositoryPath(), getWorkId());
		}
	}

	@Override
	public String toString() {
		return "Upload worker: id: ".concat(getWorkId()).concat(", upload path: ").concat(getRepositoryPath()).concat(", file name: ").concat(fileName);
	}
}