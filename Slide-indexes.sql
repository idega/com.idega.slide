CREATE INDEX properties_idx1 ON properties(version_id,property_name);
CREATE INDEX properties_idx2 ON properties(version_id,property_namespace,property_name);
CREATE INDEX binding_idx1 ON binding(name);
CREATE INDEX binding_idx2 ON binding(uri_id,child_uuri_id);
CREATE INDEX locks_idx1 ON locks(object_id,lock_id,subject_id,type_id);
CREATE INDEX object_idx1 ON object(class_name,uri_id);
CREATE INDEX parent_bind_idx1 ON parent_binding(name);
CREATE INDEX parent_bind_idx2 ON parent_binding(uri_id,parent_uuri_id);
CREATE INDEX permissions_idx1 ON permissions(object_id,subject_id,action_id);
CREATE INDEX permissions_idx2 ON permissions(succession);
CREATE INDEX permissions_idx3 ON permissions(object_id);
CREATE INDEX uri_idx1 ON uri(uri_string);
CREATE INDEX uri_idx2 ON uri(uri_id,uri_string);


Tables:
URI
VERSION
VERSION_PREDS
VERSION_LABELS
VERSION_HISTORY
VERSION_CONTENT
PROPERTIES
OBJECT
LOCKS
LINKS
LABEL
BRANCH
BINDING
PARENT_BINDING
PERMISSIONS

Selects, object_id and subject_id and action_id are all uri_id
select * from
URI where uri_string = '?'
select * from
VERSION_ where version_id = ?
select * from
VERSION_LABELS where version_id = ?
select * from
VERSION_PREDS where version_id = ?
select * from
VERSION_HISTORY where version_id = ?
select * from
PROPERTIES where version_id = '?'
select unique property_name from PROPERTIES 

select * from
OBJECT where uri_id=?
select * from
LOCKS where object_id=?
select * from
LINKS where uri_id = ?
select * from
LABEL where label_id=? , label_string=?
select * from
BRANCH where branch_string = ?
select * from
BINDING where uri_id=23
select * from
BINDING where uri_id=62;
select * from
PARENT_BINDING where uri_id = 62;
select * from PERMISSIONS where object_id = 23


select * from URI where uri_string = '/files/public';
select * from
VERSION where uri_id in (select uri_id from
URI where uri_string = '/files/public');
select * from
VERSION_LABELS where version_id in (select version_id from
VERSION_HISTORY where uri_id in (select uri_id from
URI where uri_string = '/files/public'));
select * from
VERSION_PREDS where version_id in (select version_id from
VERSION_HISTORY where uri_id in (select uri_id from
URI where uri_string = '/files/public'));
select * from
VERSION_HISTORY where uri_id in (select uri_id from
URI where uri_string = '/files/public');
select * from
PROPERTIES where version_id in (select version_id from
VERSION_HISTORY where uri_id in (select uri_id from
URI where uri_string = '/files/public'));
select * from
OBJECT where uri_id in (select uri_id from
URI where uri_string = '/files/public');
select * from
LOCKS where object_id in (select uri_id from
URI where uri_string = '/files/public');;
select * from
LINKS where uri_id in (select uri_id from
URI where uri_string = '/files/public');
select * from
LABEL where label_id in (select uri_id from
URI where uri_string = '/files/public');
select * from 
BINDING where  uri_id in (select uri_id from
URI where uri_string = '/files/public');
select * from
PARENT_BINDING where uri_id in (select uri_id from
URI where uri_string = '/files/public');
select * from PERMISSIONS where object_id in (select uri_id from
URI where uri_string = '/files/public');


delete
from
   BINDING
where
   BINDING.URI_ID in (
      select
         URI_ID
      from
         URI
      where
         URI.URI_STRING = ?
   )



