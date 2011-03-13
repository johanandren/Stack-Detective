/**
 * Copyright (C) 2011 Johan Andren <johan@markatta.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markatta.stackdetective.parse;

import com.markatta.stackdetective.model.Entry;
import com.markatta.stackdetective.model.Segment;
import com.markatta.stackdetective.model.StackTrace;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johan
 */
public class DefaultTextParserTest {

    @Test
    public void testParseAnotherStackTrace() {
        DefaultTextParser instance = new DefaultTextParser();
        StackTrace result = instance.parse("java.lang.RuntimeException: Error running test app\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:61)\n"
                + "Caused by: java.lang.ArrayIndexOutOfBoundsException: 130\n"
                + "	at com.markatta.stackdetective.FavourBeginDistanceCalculator.calculateDistance(FavourBeginDistanceCalculator.java:38)\n"
                + "	at com.markatta.stackdetective.TestApp.main(TestApp.java:51)\n");
        assertEquals(2, result.numberOfSegments());


        // checkout segment 1
        Segment segment1 = result.getSegments().get(0);
        assertEquals("java.lang.RuntimeException", segment1.getExceptionType());
        assertEquals("Error running test app", segment1.getExceptionText());

        // one entry in segment 1
        assertEquals(1, segment1.getEntries().size());
        Entry entry11 = segment1.getEntries().get(0);
        assertEquals("com.markatta.stackdetective.TestApp", entry11.getFqClassName());
        assertEquals("main", entry11.getMethodName());
        assertEquals("TestApp.java", entry11.getFileName());
        assertEquals(61, entry11.getLineNumber());

        // checkout segment 2
        Segment segment2 = result.getSegments().get(1);
        assertEquals("java.lang.ArrayIndexOutOfBoundsException", segment2.getExceptionType());
        assertEquals("130", segment2.getExceptionText());

        // entries of segment 2
        assertEquals(2, segment2.getEntries().size());
        Entry entry21 = segment2.getEntries().get(0);
        assertEquals("com.markatta.stackdetective.FavourBeginDistanceCalculator", entry21.getFqClassName());
        assertEquals("calculateDistance", entry21.getMethodName());
        assertEquals("FavourBeginDistanceCalculator.java", entry21.getFileName());
        assertEquals(38, entry21.getLineNumber());

        Entry entry22 = segment2.getEntries().get(1);
        assertEquals("com.markatta.stackdetective.TestApp", entry22.getFqClassName());
        assertEquals("main", entry22.getMethodName());
        assertEquals("TestApp.java", entry22.getFileName());
        assertEquals(51, entry22.getLineNumber());

        assertEquals("java.lang.ArrayIndexOutOfBoundsException", result.getRootExceptionType());

        assertEquals(3, result.flatten().size());
    }

    @Test
    public void stackTraceWithTheWordAtInMessage() {
        DefaultTextParser instance = new DefaultTextParser();
        StackTrace result = instance.parse("se.daby.SomeOtherException: Does not work at all\n"
                + "  at c.d.e.YadaYada.yada(YadaYada.java:23)\n"
                + "   at f.k.YipYip.yip(YipYip.java:45)");
        assertEquals(1, result.numberOfSegments());
    }

    
    @Ignore(value="cannot get it to work...")
    @Test
    public void stackTraceFromHell() {
        DefaultTextParser instance = new DefaultTextParser();
        StackTrace result = instance.parse("se.databyran.prosang.model.exceptions.ProSangRuntimeException: Det gick inte att genomföra oprationen\n\n"
                + "  at se.databyran.prosang.client.core.util.form.FinalServerOperation.doOperation(FinalServerOperation.java:186)\n"
                + "  at se.databyran.prosang.client.core.util.form.EJBPresentationModel$5.run(EJBPresentationModel.java:260)\n"
                + "  at java.lang.Thread.run(Thread.java:680)\n"
                + "Caused by: se.databyran.prosang.client.donor.shell.gui.UnableToSaveDonorException: javax.ejb.EJBTransactionRolledbackException\n\n"
                + "  at se.databyran.prosang.client.donor.shell.gui.SaveDonorOperation.operation(SaveDonorOperation.java:126)\n"
                + "  at se.databyran.prosang.client.donor.shell.gui.SaveDonorOperation.operation(SaveDonorOperation.java:22)\n"
                + "  at se.databyran.prosang.client.core.util.form.FinalServerOperation.doOperation(FinalServerOperation.java:157)\n"
                + "  ... 2 more Caused by: javax.ejb.EJBTransactionRolledbackException\n"
                + "  at org.jboss.ejb3.tx.Ejb3TxPolicy.handleInCallerTx(Ejb3TxPolicy.java:87)\n"
                + "  at org.jboss.aspects.tx.TxPolicy.invokeInCallerTx(TxPolicy.java:130)\n"
                + "  at org.jboss.aspects.tx.TxInterceptor$Required.invoke(TxInterceptor.java:195)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPropagationInterceptor.invoke(TxPropagationInterceptor.java:95)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessInstanceInterceptor.invoke(StatelessInstanceInterceptor.java:62)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:166)\n"
                + "  at org.jboss.ejb3.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:115)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.AuthenticationInterceptor.invoke(AuthenticationInterceptor.java:77)\n"
                + "  at org.jboss.ejb3.security.Ejb3AuthenticationInterceptor.invoke(Ejb3AuthenticationInterceptor.java:110)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.ENCPropagationInterceptor.invoke(ENCPropagationInterceptor.java:46)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.asynchronous.AsynchronousInterceptor.invoke(AsynchronousInterceptor.java:106)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessContainer.localInvoke(StatelessContainer.java:240)\n"
                + "  at org.jboss.ejb3.stateless.StatelessContainer.localInvoke(StatelessContainer.java:210)\n"
                + "  at org.jboss.ejb3.stateless.StatelessLocalProxy.invoke(StatelessLocalProxy.java:84)\n"
                + "  at $Proxy1473.saveOrUpdate(Unknown Source)\n"
                + "  at se.databyran.prosang.ejb.donor.DonorAdministrationBean.save(DonorAdministrationBean.java:384)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:112)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:166)\n"
                + "  at se.databyran.prosang.model.util.ExceptionLogInterceptor.intercept(ExceptionLogInterceptor.java:32)\n"
                + "  at sun.reflect.GeneratedMethodAccessor449.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.ejbprofiler.EJBProfilerInterceptor.aroundInvoke(EJBProfilerInterceptor.java:73)\n"
                + "  at sun.reflect.GeneratedMethodAccessor444.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.prosang.model.core.settings.SettingsInjectInterceptor.injectPrincipal(SettingsInjectInterceptor.java:50)\n"
                + "  at sun.reflect.GeneratedMethodAccessor443.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at org.jboss.ejb3.interceptor.EJB3InterceptorsInterceptor.invoke(EJB3InterceptorsInterceptor.java:63)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.entity.TransactionScopedEntityManagerInterceptor.invoke(TransactionScopedEntityManagerInterceptor.java:54)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.AllowedOperationsInterceptor.invoke(AllowedOperationsInterceptor.java:47)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPolicy.invokeInCallerTx(TxPolicy.java:126)\n"
                + "  at org.jboss.aspects.tx.TxInterceptor$Required.invoke(TxInterceptor.java:195)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPropagationInterceptor.invoke(TxPropagationInterceptor.java:86)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessInstanceInterceptor.invoke(StatelessInstanceInterceptor.java:62)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:166)\n"
                + "  at org.jboss.ejb3.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:115)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.AuthenticationInterceptor.invoke(AuthenticationInterceptor.java:77)\n"
                + "  at org.jboss.ejb3.security.Ejb3AuthenticationInterceptor.invoke(Ejb3AuthenticationInterceptor.java:110)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.ENCPropagationInterceptor.invoke(ENCPropagationInterceptor.java:46)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.asynchronous.AsynchronousInterceptor.invoke(AsynchronousInterceptor.java:106)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessContainer.dynamicInvoke(StatelessContainer.java:304)\n"
                + "  at org.jboss.aop.Dispatcher.invoke(Dispatcher.java:106)\n"
                + "  at org.jboss.aspects.remoting.AOPRemotingInvocationHandler.invoke(AOPRemotingInvocationHandler.java:82)\n"
                + "  at org.jboss.remoting.ServerInvoker.invoke(ServerInvoker.java:771)\n"
                + "  at org.jboss.remoting.transport.socket.ServerThread.processInvocation(ServerThread.java:573)\n"
                + "  at org.jboss.remoting.transport.socket.ServerThread.dorun(ServerThread.java:373)\n"
                + "  at org.jboss.remoting.transport.socket.ServerThread.run(ServerThread.java:166) Caused by: javax.persistence.OptimisticLockException\n"
                + "  at org.hibernate.ejb.AbstractEntityManagerImpl.wrapStaleStateException(AbstractEntityManagerImpl.java:627)\n"
                + "  at org.hibernate.ejb.AbstractEntityManagerImpl.throwPersistenceException(AbstractEntityManagerImpl.java:588)\n"
                + "  at org.hibernate.ejb.AbstractEntityManagerImpl.merge(AbstractEntityManagerImpl.java:244)\n"
                + "  at org.jboss.ejb3.entity.TransactionScopedEntityManager.merge(TransactionScopedEntityManager.java:188)\n"
                + "  at se.databyran.prosang.model.util.GenericFacadeBean.update(GenericFacadeBean.java:134)\n"
                + "  at se.databyran.prosang.model.donor.DonorFacadeBean.update(DonorFacadeBean.java:189)\n"
                + "  at se.databyran.prosang.model.donor.DonorFacadeBean.saveOrUpdate(DonorFacadeBean.java:282)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:112)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:166)\n"
                + "  at se.databyran.ejbprofiler.EJBProfilerInterceptor.aroundInvoke(EJBProfilerInterceptor.java:73)\n"
                + "  at sun.reflect.GeneratedMethodAccessor444.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.prosang.model.core.settings.SettingsInjectInterceptor.injectPrincipal(SettingsInjectInterceptor.java:50)\n"
                + "  at sun.reflect.GeneratedMethodAccessor443.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.prosang.model.util.PrincipalInjectorInterceptor.injectPrincipal(PrincipalInjectorInterceptor.java:40)\n"
                + "  at sun.reflect.GeneratedMethodAccessor447.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at org.jboss.ejb3.interceptor.EJB3InterceptorsInterceptor.invoke(EJB3InterceptorsInterceptor.java:63)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.entity.TransactionScopedEntityManagerInterceptor.invoke(TransactionScopedEntityManagerInterceptor.java:54)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.AllowedOperationsInterceptor.invoke(AllowedOperationsInterceptor.java:47)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPolicy.invokeInCallerTx(TxPolicy.java:126)\n"
                + "  at org.jboss.aspects.tx.TxInterceptor$Required.invoke(TxInterceptor.java:195)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPropagationInterceptor.invoke(TxPropagationInterceptor.java:95)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessInstanceInterceptor.invoke(StatelessInstanceInterceptor.java:62)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:166)\n"
                + "  at org.jboss.ejb3.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:115)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.AuthenticationInterceptor.invoke(AuthenticationInterceptor.java:77)\n"
                + "  at org.jboss.ejb3.security.Ejb3AuthenticationInterceptor.invoke(Ejb3AuthenticationInterceptor.java:110)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.ENCPropagationInterceptor.invoke(ENCPropagationInterceptor.java:46)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.asynchronous.AsynchronousInterceptor.invoke(AsynchronousInterceptor.java:106)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessContainer.localInvoke(StatelessContainer.java:240)\n"
                + "  at org.jboss.ejb3.stateless.StatelessContainer.localInvoke(StatelessContainer.java:210)\n"
                + "  at org.jboss.ejb3.stateless.StatelessLocalProxy.invoke(StatelessLocalProxy.java:84)\n"
                + "  at $Proxy1473.saveOrUpdate(Unknown Source)\n"
                + "  at se.databyran.prosang.ejb.donor.DonorAdministrationBean.save(DonorAdministrationBean.java:384)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:112)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:166)\n"
                + "  at se.databyran.prosang.model.util.ExceptionLogInterceptor.intercept(ExceptionLogInterceptor.java:32)\n"
                + "  at sun.reflect.GeneratedMethodAccessor449.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.ejbprofiler.EJBProfilerInterceptor.aroundInvoke(EJBProfilerInterceptor.java:73)\n"
                + "  at sun.reflect.GeneratedMethodAccessor444.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.prosang.model.core.settings.SettingsInjectInterceptor.injectPrincipal(SettingsInjectInterceptor.java:50)\n"
                + "  at sun.reflect.GeneratedMethodAccessor443.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at org.jboss.ejb3.interceptor.EJB3InterceptorsInterceptor.invoke(EJB3InterceptorsInterceptor.java:63)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.entity.TransactionScopedEntityManagerInterceptor.invoke(TransactionScopedEntityManagerInterceptor.java:54)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.AllowedOperationsInterceptor.invoke(AllowedOperationsInterceptor.java:47)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPolicy.invokeInCallerTx(TxPolicy.java:126)\n"
                + "  at org.jboss.aspects.tx.TxInterceptor$Required.invoke(TxInterceptor.java:195)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPropagationInterceptor.invoke(TxPropagationInterceptor.java:86)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessInstanceInterceptor.invoke(StatelessInstanceInterceptor.java:62)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:166)\n"
                + "  at org.jboss.ejb3.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:115)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.AuthenticationInterceptor.invoke(AuthenticationInterceptor.java:77)\n"
                + "  at org.jboss.ejb3.security.Ejb3AuthenticationInterceptor.invoke(Ejb3AuthenticationInterceptor.java:110)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.ENCPropagationInterceptor.invoke(ENCPropagationInterceptor.java:46)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.asynchronous.AsynchronousInterceptor.invoke(AsynchronousInterceptor.java:106)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessContainer.dynamicInvoke(StatelessContainer.java:304)\n"
                + "  at org.jboss.aop.Dispatcher.invoke(Dispatcher.java:106)\n"
                + "  at org.jboss.aspects.remoting.AOPRemotingInvocationHandler.invoke(AOPRemotingInvocationHandler.java:82)\n"
                + "  at org.jboss.remoting.ServerInvoker.invoke(ServerInvoker.java:771)\n"
                + "  at org.jboss.remoting.transport.socket.ServerThread.processInvocation(ServerThread.java:573)\n"
                + "  at org.jboss.remoting.transport.socket.ServerThread.dorun(ServerThread.java:373)\n"
                + "  at org.jboss.remoting.transport.socket.ServerThread.run(ServerThread.java:166)\n"
                + "  at org.jboss.remoting.MicroRemoteClientInvoker.invoke(MicroRemoteClientInvoker.java:163)\n"
                + "  at org.jboss.remoting.Client.invoke(Client.java:1634)\n"
                + "  at org.jboss.remoting.Client.invoke(Client.java:548)\n"
                + "  at org.jboss.aspects.remoting.InvokeRemoteInterceptor.invoke(InvokeRemoteInterceptor.java:62)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.ClientTxPropagationInterceptor.invoke(ClientTxPropagationInterceptor.java:67)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.SecurityClientInterceptor.invoke(SecurityClientInterceptor.java:53)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.remoting.IsLocalInterceptor.invoke(IsLocalInterceptor.java:74)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessRemoteProxy.invoke(StatelessRemoteProxy.java:107)\n"
                + "  at $Proxy19.save(Unknown Source)\n"
                + "  at se.databyran.prosang.client.donor.shell.gui.SaveDonorOperation.operation(SaveDonorOperation.java:114)\n"
                + "  at se.databyran.prosang.client.donor.shell.gui.SaveDonorOperation.operation(SaveDonorOperation.java:22)\n"
                + "  at se.databyran.prosang.client.core.util.form.FinalServerOperation.doOperation(FinalServerOperation.java:157)\n"
                + "  at se.databyran.prosang.client.core.util.form.EJBPresentationModel$5.run(EJBPresentationModel.java:260)\n"
                + "  at java.lang.Thread.run(Thread.java:680)\n"
                + "  at org.jboss.aspects.remoting.InvokeRemoteInterceptor.invoke(InvokeRemoteInterceptor.java:74)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.ClientTxPropagationInterceptor.invoke(ClientTxPropagationInterceptor.java:67)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.SecurityClientInterceptor.invoke(SecurityClientInterceptor.java:53)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.remoting.IsLocalInterceptor.invoke(IsLocalInterceptor.java:74)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessRemoteProxy.invoke(StatelessRemoteProxy.java:107)\n"
                + "  at $Proxy19.save(Unknown Source)\n"
                + "  at se.databyran.prosang.client.donor.shell.gui.SaveDonorOperation.operation(SaveDonorOperation.java:114)\n"
                + "  at se.databyran.prosang.client.donor.shell.gui.SaveDonorOperation.operation(SaveDonorOperation.java:22)\n"
                + "  at se.databyran.prosang.client.core.util.form.FinalServerOperation.doOperation(FinalServerOperation.java:157)\n"
                + "  at se.databyran.prosang.client.core.util.form.EJBPresentationModel$5.run(EJBPresentationModel.java:260)\n"
                + "  at java.lang.Thread.run(Thread.java:680) Caused by: org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [se.databyran.prosang.model.donor.Donor#18519096]\n"
                + "  at org.hibernate.event.def.DefaultMergeEventListener.entityIsDetached(DefaultMergeEventListener.java:418)\n"
                + "  at org.hibernate.event.def.DefaultMergeEventListener.onMerge(DefaultMergeEventListener.java:234)\n"
                + "  at org.hibernate.event.def.DefaultMergeEventListener.onMerge(DefaultMergeEventListener.java:84)\n"
                + "  at org.hibernate.impl.SessionImpl.fireMerge(SessionImpl.java:705)\n"
                + "  at org.hibernate.impl.SessionImpl.merge(SessionImpl.java:689)\n"
                + "  at org.hibernate.impl.SessionImpl.merge(SessionImpl.java:693)\n"
                + "  at org.hibernate.ejb.AbstractEntityManagerImpl.merge(AbstractEntityManagerImpl.java:235)\n"
                + "  at org.jboss.ejb3.entity.TransactionScopedEntityManager.merge(TransactionScopedEntityManager.java:188)\n"
                + "  at se.databyran.prosang.model.util.GenericFacadeBean.update(GenericFacadeBean.java:134)\n"
                + "  at se.databyran.prosang.model.donor.DonorFacadeBean.update(DonorFacadeBean.java:189)\n"
                + "  at se.databyran.prosang.model.donor.DonorFacadeBean.saveOrUpdate(DonorFacadeBean.java:282)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:112)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:166)\n"
                + "  at se.databyran.ejbprofiler.EJBProfilerInterceptor.aroundInvoke(EJBProfilerInterceptor.java:73)\n"
                + "  at sun.reflect.GeneratedMethodAccessor444.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.prosang.model.core.settings.SettingsInjectInterceptor.injectPrincipal(SettingsInjectInterceptor.java:50)\n"
                + "  at sun.reflect.GeneratedMethodAccessor443.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.prosang.model.util.PrincipalInjectorInterceptor.injectPrincipal(PrincipalInjectorInterceptor.java:40)\n"
                + "  at sun.reflect.GeneratedMethodAccessor447.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at org.jboss.ejb3.interceptor.EJB3InterceptorsInterceptor.invoke(EJB3InterceptorsInterceptor.java:63)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.entity.TransactionScopedEntityManagerInterceptor.invoke(TransactionScopedEntityManagerInterceptor.java:54)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.AllowedOperationsInterceptor.invoke(AllowedOperationsInterceptor.java:47)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPolicy.invokeInCallerTx(TxPolicy.java:126)\n"
                + "  at org.jboss.aspects.tx.TxInterceptor$Required.invoke(TxInterceptor.java:195)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPropagationInterceptor.invoke(TxPropagationInterceptor.java:95)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessInstanceInterceptor.invoke(StatelessInstanceInterceptor.java:62)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:166)\n"
                + "  at org.jboss.ejb3.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:115)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.AuthenticationInterceptor.invoke(AuthenticationInterceptor.java:77)\n"
                + "  at org.jboss.ejb3.security.Ejb3AuthenticationInterceptor.invoke(Ejb3AuthenticationInterceptor.java:110)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.ENCPropagationInterceptor.invoke(ENCPropagationInterceptor.java:46)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.asynchronous.AsynchronousInterceptor.invoke(AsynchronousInterceptor.java:106)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessContainer.localInvoke(StatelessContainer.java:240)\n"
                + "  at org.jboss.ejb3.stateless.StatelessContainer.localInvoke(StatelessContainer.java:210)\n"
                + "  at org.jboss.ejb3.stateless.StatelessLocalProxy.invoke(StatelessLocalProxy.java:84)\n"
                + "  at $Proxy1473.saveOrUpdate(Unknown Source)\n"
                + "  at se.databyran.prosang.ejb.donor.DonorAdministrationBean.save(DonorAdministrationBean.java:384)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
                + "  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:112)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:166)\n"
                + "  at se.databyran.prosang.model.util.ExceptionLogInterceptor.intercept(ExceptionLogInterceptor.java:32)\n"
                + "  at sun.reflect.GeneratedMethodAccessor449.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.ejbprofiler.EJBProfilerInterceptor.aroundInvoke(EJBProfilerInterceptor.java:73)\n"
                + "  at sun.reflect.GeneratedMethodAccessor444.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at se.databyran.prosang.model.core.settings.SettingsInjectInterceptor.injectPrincipal(SettingsInjectInterceptor.java:50)\n"
                + "  at sun.reflect.GeneratedMethodAccessor443.invoke(Unknown Source)\n"
                + "  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)\n"
                + "  at java.lang.reflect.Method.invoke(Method.java:597)\n"
                + "  at org.jboss.ejb3.interceptor.InvocationContextImpl.proceed(InvocationContextImpl.java:118)\n"
                + "  at org.jboss.ejb3.interceptor.EJB3InterceptorsInterceptor.invoke(EJB3InterceptorsInterceptor.java:63)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.entity.TransactionScopedEntityManagerInterceptor.invoke(TransactionScopedEntityManagerInterceptor.java:54)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.AllowedOperationsInterceptor.invoke(AllowedOperationsInterceptor.java:47)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPolicy.invokeInCallerTx(TxPolicy.java:126)\n"
                + "  at org.jboss.aspects.tx.TxInterceptor$Required.invoke(TxInterceptor.java:195)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.tx.TxPropagationInterceptor.invoke(TxPropagationInterceptor.java:86)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessInstanceInterceptor.invoke(StatelessInstanceInterceptor.java:62)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:166)\n"
                + "  at org.jboss.ejb3.security.RoleBasedAuthorizationInterceptor.invoke(RoleBasedAuthorizationInterceptor.java:115)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.aspects.security.AuthenticationInterceptor.invoke(AuthenticationInterceptor.java:77)\n"
                + "  at org.jboss.ejb3.security.Ejb3AuthenticationInterceptor.invoke(Ejb3AuthenticationInterceptor.java:110)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.ENCPropagationInterceptor.invoke(ENCPropagationInterceptor.java:46)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.asynchronous.AsynchronousInterceptor.invoke(AsynchronousInterceptor.java:106)\n"
                + "  at org.jboss.aop.joinpoint.MethodInvocation.invokeNext(MethodInvocation.java:101)\n"
                + "  at org.jboss.ejb3.stateless.StatelessContainer.dynamicInvoke(StatelessContainer.java:304)\n"
                + "  at org.jboss.aop.Dispatcher.invoke(Dispatcher.java:106)\n"
                + "  at org.jboss.aspects.remoting.AOPRemotingInvocationHandler.invoke(AOPRemotingInvocationHandler.java:82)\n"
                + "  at org.jboss.remoting.ServerInvoker.invoke(ServerInvoker.java:771)\n"
                + "  at org.jboss.remoting.transport.socket.ServerThread.processInvocation(ServerThread.java:573)\n"
                + "  at org.jboss.remoting.transport.socket.ServerThread.dorun(ServerThread.java:373)\n"
                + "  at org.jboss.remoting.transport.socket.ServerThread.run(ServerThread.java:166)");

        assertEquals(2, result.numberOfSegments());
    }
}
