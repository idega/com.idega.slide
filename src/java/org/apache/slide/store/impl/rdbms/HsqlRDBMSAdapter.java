package org.apache.slide.store.impl.rdbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import org.apache.slide.common.Service;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.Uri;
import org.apache.slide.content.NodeProperty;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.util.logger.Logger;

/**
 * TODO change delete x from X to delete from X where in (...)
 * Last modified: $Date: 2006/04/09 11:44:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron </a>
 * @version $Revision: 1.4 $
 */
public class HsqlRDBMSAdapter extends StandardRDBMSAdapter {
//	CommonRDBMSAdapter {

    public HsqlRDBMSAdapter(Service service, Logger logger) {
        super(service, logger);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.slide.store.impl.rdbms.StandardRDBMSAdapter#convertRevisionNumberToComparable(java.lang.String)
     */
    protected String convertRevisionNumberToComparable(String revisionNumber) {
        /*
         * CONVERT(SUBSTRING(vh.REVISION_NO,1,(LOCATE('.',vh.REVISION_NO)-1))
         * ,INTEGER), CONVERT(SUBSTRING(vh.REVISION_NO, (
         * LOCATE('.',vh.REVISION_NO)+1 )),INTEGER)
         */
        String s = "convert(SUBSTRING(" + revisionNumber + ",1,(LOCATE('.',"
                + revisionNumber + ")-1)) ,INTEGER), convert( SUBSTRING("
                + revisionNumber + ",(LOCATE('.'," + revisionNumber
                + ")+1)),INTEGER)";
        return s;
    }

    public void removeObject(Connection connection, Uri uri, ObjectNode object)
            throws ServiceAccessException, ObjectNotFoundException {
        PreparedStatement statement = null;
        try {
            clearBinding(connection, uri);

            // delete links
            try {
                statement = connection
                        .prepareStatement("delete from LINKS where URI_ID  in  (select URI_ID  from URI  where URI_STRING = ? )");
                statement.setString(1, uri.toString());
                statement.executeUpdate();
            } finally {
                close(statement);
            }
            // delete version history
            // FIXME: Is this true??? Should the version history be removed if
            // the object is removed???
            try {
                statement = connection
                        .prepareStatement("delete from VERSION_HISTORY where URI_ID in (select URI_ID from URI where URI_STRING = ? )");
                statement.setString(1, uri.toString());
                statement.executeUpdate();
            } finally {
                close(statement);
            }
            // delete version
            try {
                statement = connection
                        .prepareStatement("delete from VERSION where URI_ID in (select URI_ID from URI where URI_STRING = ? )");
                statement.setString(1, uri.toString());
                statement.executeUpdate();
            } finally {
                close(statement);
            }
            // delete the object itself
            try {
                statement = connection
                        .prepareStatement("delete from OBJECT where URI_ID in (select URI_ID from URI where URI_STRING = ?)");
                statement.setString(1, uri.toString());
                statement.executeUpdate();
            } finally {
                close(statement);
            }
            // finally delete the uri
            try {
                statement = connection
                        .prepareStatement("delete from URI where URI_STRING = ?");
                statement.setString(1, uri.toString());
                statement.executeUpdate();
            } finally {
                close(statement);
            }
        } catch (SQLException e) {
            throw createException(e, uri.toString());
        }
    }

    /*
     * @see org.apache.slide.store.impl.rdbms.RDBMSAdapter#removeRevisionContent(
     *      java.sql.Connection, org.apache.slide.common.Uri,
     *      org.apache.slide.content.NodeRevisionDescriptor)
     */
    public void removeRevisionContent(Connection connection, Uri uri,
            NodeRevisionDescriptor revisionDescriptor)
            throws ServiceAccessException {
        try {
            PreparedStatement statement = null;
            try {
                statement = connection
                        .prepareStatement("delete from VERSION_CONTENT  where VERSION_ID in ( SELECT VH.VERSION_ID FROM VERSION_HISTORY VH, URI U WHERE REVISION_NO = ? and U.URI_STRING = ?)");
                //"delete vc from VERSION_CONTENT vc, VERSION_HISTORY vh, URI u
                // where vc.VERSION_ID = vh.VERSION_ID and vh.REVISION_NO = ?
                // and vh.URI_ID=u.URI_ID AND u.URI_STRING=?");
                statement.setString(1, revisionDescriptor.getRevisionNumber()
                        .toString());
                statement.setString(2, uri.toString());
                statement.executeUpdate();
            } finally {
                close(statement);
            }
        } catch (SQLException e) {
            throw createException(e, uri.toString());
        }
    }

    /*
     * @see org.apache.slide.store.impl.rdbms.RDBMSAdapter#removeRevisionDescriptor(
     *      java.sql.Connection, org.apache.slide.common.Uri,
     *      org.apache.slide.content.NodeRevisionNumber)
     */
    public void removeRevisionDescriptor(Connection connection, Uri uri,
            NodeRevisionNumber revisionNumber) throws ServiceAccessException {
        PreparedStatement statement = null;
        try {
            try {
                
                
                String sql = "delete from VERSION_LABELS where VERSION_ID in (select vh.VERSION_ID from VERSION_HISTORY vh, URI u where vh.REVISION_NO = ? and vh.URI_ID = u.URI_ID AND u.URI_STRING = ?)";
                statement = connection.prepareStatement(sql);
                //"delete vl from VERSION_LABELS vl, VERSION_HISTORY vh, URI u  where vl.VERSION_ID = vh.VERSION_ID and vh.REVISION_NO = ? and vh.URI_ID = u.URI_ID AND u.URI_STRING = ?");
                statement.setString(1, revisionNumber.toString());
                statement.setString(2, uri.toString());
                statement.executeUpdate();
            } finally {
                close(statement);
            }
            
            try {
                String sql = "delete from PROPERTIES where VERSION_ID in (select vh.VERSION_ID from VERSION_HISTORY vh, URI u where vh.REVISION_NO = ? and vh.URI_ID = u.URI_ID AND u.URI_STRING = ?)";
                statement = connection.prepareStatement(sql);
                //"delete p from PROPERTIES p, VERSION_HISTORY vh, URI u where p.VERSION_ID = vh.VERSION_ID and vh.REVISION_NO = ? and vh.URI_ID = u.URI_ID AND u.URI_STRING = ?");
                statement.setString(1, revisionNumber.toString());
                statement.setString(2, uri.toString());
                statement.executeUpdate();
            } finally {
                close(statement);
            }
        } catch (SQLException e) {
            throw createException(e, uri.toString());
        }
    }

    /*
     * @see org.apache.slide.store.impl.rdbms.RDBMSAdapter#removeRevisionDescriptors(
     *      java.sql.Connection, org.apache.slide.common.Uri)
     */
    public void removeRevisionDescriptors(Connection connection, Uri uri)
            throws ServiceAccessException {
        PreparedStatement statement = null;
        try {
            statement = connection
                    .prepareStatement("delete from VERSION_PREDS where VERSION_ID in ( SELECT VH.VERSION_ID FROM VERSION_HISTORY VH, URI U WHERE REVISION_NO = ? and U.URI_STRING = ?)");
            //"delete vp from VERSION_PREDS vp, VERSION_HISTORY vh, URI u where
            // vp.VERSION_ID = vh.VERSION_ID and vh.URI_ID = u.URI_ID and
            // u.URI_STRING = ?");
            statement.setString(1, uri.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw createException(e, uri.toString());
        } finally {
            close(statement);
        }
    }
  
    protected long getVersionID(Connection connection, String uriString,
            NodeRevisionDescriptor revisionDescriptor) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        long versionID = 0l;
        try {
            statement = connection
                    .prepareStatement("select vh.VERSION_ID from VERSION_HISTORY vh, URI u where vh.URI_ID = u.URI_ID and u.URI_STRING = ? and vh.REVISION_NO = ?");
            statement.setString(1, uriString);
            statement.setString(2, revisionDescriptor.getRevisionNumber()
                    .toString());
            rs = statement.executeQuery();
            if (rs.next()) {
                versionID = rs.getLong(1);
            }
        } finally {
            close(statement, rs);
        }

        return versionID;
    }

    public void createRevisionDescriptor(Connection connection, Uri uri,
            NodeRevisionDescriptor revisionDescriptor)
            throws ServiceAccessException {

        PreparedStatement statement = null;
        try {

            assureVersionInfo(connection, uri, revisionDescriptor);

            for (Enumeration labels = revisionDescriptor.enumerateLabels(); labels
                    .hasMoreElements();) {
                long labelId = assureLabelId(connection, (String) labels
                        .nextElement());
                try {
                    long versionID = getVersionID(connection, uri.toString(),
                            revisionDescriptor);
                    statement = connection
                            .prepareStatement("insert into VERSION_LABELS (VERSION_ID, LABEL_ID) values (?,?)");
                    statement.setLong(1, versionID);
                    statement.setLong(2, labelId);
                    statement.executeUpdate();
                } finally {
                    close(statement);
                }
            }
           
            for (Enumeration properties = revisionDescriptor.enumerateProperties(); properties.hasMoreElements();) {
                try {
                    NodeProperty property = (NodeProperty) properties
                            .nextElement();
                    long versionID = getVersionID(connection, uri.toString(),
                            revisionDescriptor);
                    statement = connection
                            .prepareStatement("insert into PROPERTIES (VERSION_ID,PROPERTY_NAMESPACE,PROPERTY_NAME,PROPERTY_VALUE,PROPERTY_TYPE,IS_PROTECTED) values (?,?,?,?,?,?)");
                    int protectedProperty = property.isProtected() ? 1 : 0;
                    statement.setLong(1, versionID);
                    statement.setString(2, property.getNamespace());
                    statement.setString(3, property.getName());
                    statement.setString(4, property.getValue().toString());
                    statement.setString(5, property.getType());
                    statement.setInt(6, protectedProperty);
                    statement.executeUpdate();
                } finally {
                    close(statement);
                }
            }
        } catch (SQLException e) {
            throw createException(e, uri.toString());
        }
    }

    public void createRevisionDescriptors(Connection connection, Uri uri,
            NodeRevisionDescriptors revisionDescriptors)
            throws ServiceAccessException {

        PreparedStatement statement = null;
        ResultSet res = null;
        try {
            int isVersioned = 0;
            if (revisionDescriptors.isVersioned()) {
							isVersioned = 1;
						}
            boolean revisionExists;
            try {
                statement = connection
                        .prepareStatement("SELECT u.URI_ID FROM VERSION v, URI u WHERE v.URI_ID = u.URI_ID and u.URI_STRING = ?");
                statement.setString(1, uri.toString());
                res = statement.executeQuery();
                revisionExists = res.next();

            } finally {
                close(statement, res);
            }
            if (!revisionExists) {
                try {
                    long id = getID(connection, uri.toString());
                    statement = connection
                            .prepareStatement("insert into VERSION (URI_ID, IS_VERSIONED) values (?,?)");
                    statement.setLong(1, id);
                    statement.setInt(2, isVersioned);
                    statement.executeUpdate();
                } finally {
                    close(statement, res);
                }
            }
            boolean versionHistoryExists = false;
            if (revisionDescriptors.getLatestRevision() != null) {
                try {
                    statement = connection
                            .prepareStatement("SELECT 1 FROM VERSION_HISTORY vh, URI u WHERE vh.URI_ID = u.URI_ID and u.URI_STRING = ? and REVISION_NO = ?");
                    statement.setString(1, uri.toString());
                    statement.setString(2, revisionDescriptors
                            .getLatestRevision().toString());
                    res = statement.executeQuery();
                    versionHistoryExists = res.next();
                } finally {
                    close(statement, res);
                }
            }
            if (!versionHistoryExists
                    && revisionDescriptors.getLatestRevision() != null) {
                try {

                    long[] ids = getBranchIdAndUriID(connection, uri.toString());
                    long uriID = ids[0];
                    long branchID = ids[1];

                    statement = connection
                            .prepareStatement("insert into VERSION_HISTORY (URI_ID, BRANCH_ID, REVISION_NO) values(?,?,?)");
                    statement.setLong(1, uriID);
                    statement.setLong(2, branchID);
                    statement.setString(3,
                            getRevisionNumberAsString(revisionDescriptors
                                    .getLatestRevision()));

                    // FIXME: Create new revisions on the main branch???
                    statement.executeUpdate();
                } finally {
                    close(statement, res);
                }
            }

            // Add revision successors
            Enumeration revisionNumbers = revisionDescriptors
                    .enumerateRevisionNumbers();
            while (revisionNumbers.hasMoreElements()) {
                NodeRevisionNumber nodeRevisionNumber = (NodeRevisionNumber) revisionNumbers
                        .nextElement();

                Enumeration successors = revisionDescriptors
                        .getSuccessors(nodeRevisionNumber);
                while (successors.hasMoreElements()) {
                    try {
                        NodeRevisionNumber successor = (NodeRevisionNumber) successors
                                .nextElement();

                        statement = connection
                                .prepareStatement("insert into VERSION_PREDS (VERSION_ID, PREDECESSOR_ID) "
                                        + " select vr.VERSION_ID, suc.VERSION_ID"
                                        + " FROM URI uri, VERSION_HISTORY  vr, VERSION_HISTORY suc "
                                        + " where vr.URI_ID = uri.URI_ID "
                                        + " and suc.URI_ID = uri.URI_ID "
                                        + " and uri.URI_STRING = ? "
                                        + " and vr.REVISION_NO = ? "
                                        + " and suc.REVISION_NO = ? ");

                        statement.setString(1, uri.toString());
                        statement.setString(2, nodeRevisionNumber.toString());
                        statement.setString(3, successor.toString());
                        statement.executeUpdate();
                    } finally {
                        close(statement);
                    }
                }
            }
            getLogger().log(
                    revisionDescriptors.getOriginalUri()
                            + revisionDescriptors.getInitialRevision(),
                    LOG_CHANNEL, Logger.INFO);

        } catch (SQLException e) {
            throw createException(e, uri.toString());
        }
    }

    /*
     * @see org.apache.slide.store.impl.rdbms.StandardRDBMSAdapter#clearBinding(java.sql.Connection,
     *      org.apache.slide.common.Uri)
     */
    protected void clearBinding(Connection connection, Uri uri)
            throws ServiceAccessException, ObjectNotFoundException,
            SQLException {
        PreparedStatement statement = null;

        // clear this uri from having bindings and being bound
        try {
            statement = connection
                    .prepareStatement("delete from BINDING where URI_ID in ( select URI_ID from  URI  where URI_STRING = ?)");
            //"delete c from BINDING c, URI u where c.URI_ID = u.URI_ID and
            // u.URI_STRING = ?");
            statement.setString(1, uri.toString());
            statement.executeUpdate();
        } finally {
            close(statement);
        }

        try {
            statement = connection
                    .prepareStatement("delete from PARENT_BINDING where URI_ID in ( select URI_ID from  URI  where URI_STRING = ?)");
            //"delete c from PARENT_BINDING c, URI u where c.URI_ID = u.URI_ID
            // and u.URI_STRING = ?");
            statement.setString(1, uri.toString());
            statement.executeUpdate();
        } finally {
            close(statement);
        }
    }
    private long getID(Connection connection, String uriString) throws  SQLException
    {
		PreparedStatement statement = null;
		ResultSet rs = null;
		long uriID=0l;
		try
		{
			statement =
				connection.prepareStatement(
					"select URI_ID from URI where URI_STRING = ?");

			 statement.setString(1,uriString);
			 rs = statement.executeQuery();
			 if(rs.next()) {
				uriID= rs.getLong(1);
			}
		 }
		 finally
		 {
			 close(statement,rs);
		 }

		 return uriID;
	}
    
    private long[] getBranchIdAndUriID(Connection connection, String uriString) throws SQLException
    {
  	PreparedStatement statement = null;
  	ResultSet res = null;
  	long[] ids = new long[2];

  	try
  	{
  		statement =
  			connection.prepareStatement(
  				"select u.URI_ID, b.BRANCH_ID from URI u, BRANCH b where u.URI_STRING = ? and b.BRANCH_STRING = ?");

  		 statement.setString(1, uriString);
  		 statement.setString(2,NodeRevisionDescriptors.MAIN_BRANCH);

  		 res = statement.executeQuery();
  		 if(res.next())
  		 {
  			 ids[0]=res.getLong(1);
  			 ids[1] = res.getLong(2);
  		 }
  	 }
  	 finally
  	 {
  		 close(statement,res);
  	 }

  	 return ids;
    }

}