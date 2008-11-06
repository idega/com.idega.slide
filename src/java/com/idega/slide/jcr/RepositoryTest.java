package com.idega.slide.jcr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import com.idega.util.FileUtil;

public class RepositoryTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NotBoundException 
	 * @throws ClassCastException 
	 * @throws RepositoryException 
	 * @throws NoSuchWorkspaceException 
	 * @throws LoginException 
	 */
	public static void main(String[] args) throws IOException, ClassCastException, NotBoundException, LoginException, NoSuchWorkspaceException, RepositoryException {
		// TODO Auto-generated method stub
		//startEmbeddedRepository();
		
		Repository repository = getSlideRepository();
		
		Credentials credentials = new SimpleCredentials("root", "".toCharArray());
		
		Session session = repository.login(credentials);
		//session.getN
		Node rootNode = session.getRootNode();
		
		NodeIterator nodeIterator = rootNode.getNodes();
		//dumpNodeInfo(nodeIterator);
		
		Node filesNode;
		try{
			filesNode = rootNode.getNode("files");
			System.out.println("Node="+filesNode.getName()+" found");

		}
		catch(PathNotFoundException ne){
			filesNode = rootNode.addNode("files", "nt:folder");
			filesNode.save();
			System.out.println("Node="+filesNode.getName()+" not found - created");
		}
		Node testFolder;
		try{
			testFolder = filesNode.getNode("testFolder");
			System.out.println("Node="+testFolder.getName()+" found");
		}
		catch(PathNotFoundException ne){
			testFolder = filesNode.addNode("testFolder", "nt:folder");
			testFolder.save();
			System.out.println("Node="+testFolder.getName()+" not found - created");
		}
		Node fileNode;
		Node contentNode;
		try{

			//try{
				//fileNode = rootNode.getNode("/files/cms/themes/Multi_FreeStyle_III/Multi_FreeStyle_III.rwtheme/Contents/css/font/font1.css");
				fileNode = testFolder.getNode("testfile.pdf");
				System.out.println("Node="+fileNode.getName()+" found");
				String nodeType = fileNode.getPrimaryNodeType().getName();
				if(nodeType.equals("nt:file")){
					System.out.println("NodeType of "+fileNode.getName()+" is "+nodeType);
				}
				else{
					System.err.println("Error: NodeType of "+fileNode.getName()+" is "+nodeType);
				}
			/*}
			catch(PathNotFoundException ne){
				fileNode = testFolder.addNode("testfile.pdf", "nt:file");
				System.out.println("Node="+fileNode.getName()+" not found - created");
				fileNode.save();
			}*/
			
			
			try{
				//fileNode = rootNode.getNode("/files/cms/themes/Multi_FreeStyle_III/Multi_FreeStyle_III.rwtheme/Contents/css/font/font1.css");
				contentNode = fileNode.getNode("jcr:content");
				System.out.println("Node="+contentNode.getName()+" found");
			}
			catch(PathNotFoundException ne){
				contentNode = fileNode.addNode("jcr:content","nt:unstructured");
				System.out.println("Node="+contentNode.getName()+" not found - created");
			}
			
			FileInputStream fileinstream = new FileInputStream(new File("testfile.pdf"));
			contentNode.setProperty("jcr:data",fileinstream);
			contentNode.setProperty("jcr:mimetype","application/pdf");
			contentNode.save();
			
			//fileNode.setProperty("testprop2","blabla");
			try{
				String testproperty = fileNode.getProperty("testprop").getString();
				int iTestProp = Integer.parseInt(testproperty);
				fileNode.setProperty("testprop",Integer.toString(++iTestProp));
				//fileNode.save();
			}
			catch(PathNotFoundException pe){
				fileNode.setProperty("testprop","1");
				//fileNode.save();
			}
			
			fileNode.save();
			
			
			contentNode = fileNode.getNode("jcr:content");
			System.out.println("Node="+fileNode.getName()+" found");
			
			Property property = contentNode.getProperty("jcr:data");
			InputStream instream = property.getStream();

			File fileCopy =  new File(fileNode.getName()+"-copy");
			if(!fileCopy.exists()){
				fileCopy.createNewFile();
			}
			FileOutputStream outstream = new FileOutputStream(fileCopy);
			
			byte[] buffer = new byte[1024];
			while(instream.read(buffer)!=-1){
				outstream.write(buffer);
			}
			

		}
		catch(PathNotFoundException ne){
			//Create the file if it does not exist:
			fileNode = testFolder.addNode("testfile.pdf", "nt:file");
			//fileNode.save();
			contentNode = fileNode.addNode("jcr:content","nt:unstructured");
			System.out.println("Node="+fileNode.getName()+" not found - created");
			

			FileInputStream instream = new FileInputStream(new File("testfile.pdf"));
			contentNode.setProperty("jcr:data",instream);
			contentNode.setProperty("jcr:mimetype","application/pdf");
			contentNode.save();
			
			//fileNode.save();
			
		}
		
		//node.setProperty("jcr:primaryType", "nt:folder");
		//node.save();
		
		session.save();
		
		dumpNodeInfo(testFolder.getNodes());
		
		System.exit(0);
	}



	private static void dumpNodeInfo(NodeIterator nodeIterator)
			throws RepositoryException, ValueFormatException {
		while(nodeIterator.hasNext()) {
		
			Node node = nodeIterator.nextNode();
			String nodeName = node.getName();
			String nodePath = node.getPath();
			//Node node = rootNode.getNode("testfolder");
			PropertyIterator iterator = node.getProperties();
			System.out.print("NodePath="+nodePath+" - Name="+nodeName+" [");
			while(iterator.hasNext()) {
				Property property = iterator.nextProperty();
				String name = property.getName();
				Value value = property.getValue();
				System.out.print("@"+name+"="+value.getString()+", ");
			}
			System.out.println("]");

			//Node folderNode = rootNode.addNode("nt:folder", "testfolder");
			dumpNodeInfo(node.getNodes());
		}
	}


	
	private static Repository getSlideRepository() {
		// TODO Auto-generated method stub
		return new SlideRepository();
	}

	/*private static Repository getRemoteRepository() {
		String url = "rmi://localhost:1099/jackrabbit.repository";
		
		//ClientRepositoryFactory factory = new ClientRepositoryFactory();
		//Repository repository = factory.getRepository(url);
		
		Repository repository =
		    new RMIRemoteRepository("//localhost/jackrabbit.repository");
		return repository;
	}

	private static Repository getEmbeddedRepository() throws IOException,
			RemoteException, AccessException {
		Repository srepository = new TransientRepository();
		ServerAdapterFactory sfactory = new ServerAdapterFactory();
		RemoteRepository remote = sfactory.getRemoteRepository(srepository);
		Registry reg = LocateRegistry.createRegistry(1100);
		reg.rebind("jackrabbit", remote);
		return srepository;
	}*/

}
