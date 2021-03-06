/*
 * XML Type:  ImportRecordsImportResponse
 * Namespace: http://schemas.microsoft.com/crm/2007/WebServices
 * Java type: com.microsoft.schemas.crm._2007.webservices.ImportRecordsImportResponse
 *
 * Automatically generated - do not modify.
 */
package com.microsoft.schemas.crm._2007.webservices.impl;
/**
 * An XML ImportRecordsImportResponse(@http://schemas.microsoft.com/crm/2007/WebServices).
 *
 * This is a complex type.
 */
public class ImportRecordsImportResponseImpl extends com.microsoft.schemas.crm._2007.webservices.impl.ResponseImpl implements com.microsoft.schemas.crm._2007.webservices.ImportRecordsImportResponse
{
    
    public ImportRecordsImportResponseImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ASYNCOPERATIONID$0 = 
        new javax.xml.namespace.QName("http://schemas.microsoft.com/crm/2007/WebServices", "AsyncOperationId");
    
    
    /**
     * Gets the "AsyncOperationId" element
     */
    public java.lang.String getAsyncOperationId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ASYNCOPERATIONID$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "AsyncOperationId" element
     */
    public com.microsoft.wsdl.types.Guid xgetAsyncOperationId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.wsdl.types.Guid target = null;
            target = (com.microsoft.wsdl.types.Guid)get_store().find_element_user(ASYNCOPERATIONID$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "AsyncOperationId" element
     */
    public void setAsyncOperationId(java.lang.String asyncOperationId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ASYNCOPERATIONID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ASYNCOPERATIONID$0);
            }
            target.setStringValue(asyncOperationId);
        }
    }
    
    /**
     * Sets (as xml) the "AsyncOperationId" element
     */
    public void xsetAsyncOperationId(com.microsoft.wsdl.types.Guid asyncOperationId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            com.microsoft.wsdl.types.Guid target = null;
            target = (com.microsoft.wsdl.types.Guid)get_store().find_element_user(ASYNCOPERATIONID$0, 0);
            if (target == null)
            {
                target = (com.microsoft.wsdl.types.Guid)get_store().add_element_user(ASYNCOPERATIONID$0);
            }
            target.set(asyncOperationId);
        }
    }
}
