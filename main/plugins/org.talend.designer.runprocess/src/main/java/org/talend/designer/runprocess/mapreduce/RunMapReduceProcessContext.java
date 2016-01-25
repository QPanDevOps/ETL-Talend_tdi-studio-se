// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.runprocess.mapreduce;

import org.talend.core.GlobalServiceRegister;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.language.LanguageManager;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.IProcess2;
import org.talend.core.model.properties.Property;
import org.talend.core.service.IRemoteRunprocessorService;
import org.talend.designer.runprocess.IProcessor;
import org.talend.designer.runprocess.RunProcessContext;

/**
 * Created by Marvin Wang on Mar 5, 2013.
 */
public class RunMapReduceProcessContext extends RunProcessContext {

    /**
     * DOC marvin RunRemoteMapReduceProcessContext constructor comment.
     * 
     * @param process
     */
    public RunMapReduceProcessContext(IProcess2 process) {
        super(process);
    }

    @Override
    protected IProcessor getProcessor(IProcess process, Property property) {
        ECodeLanguage currentLanguage = LanguageManager.getCurrentLanguage();
        if (currentLanguage == ECodeLanguage.PERL) {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(IRemoteRunprocessorService.class)) {
                IRemoteRunprocessorService service = (IRemoteRunprocessorService) GlobalServiceRegister.getDefault().getService(
                        IRemoteRunprocessorService.class);
                return service.createRemotePerlProcessor(process, property, true);
            } else {
                throw new RuntimeException("Language not found");
            }
        } else if (currentLanguage == ECodeLanguage.JAVA) {
            return new MapReduceJavaProcessor(process, property, true);
        } else {
            throw new RuntimeException("Language not found");
        }
    }

}
