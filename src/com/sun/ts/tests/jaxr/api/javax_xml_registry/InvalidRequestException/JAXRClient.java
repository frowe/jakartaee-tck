/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * @(#)JAXRClient.java	1.15 03/05/16
 */

/*
 * @(#)JAXRClient.java	1.9 02/04/30
 */
package com.sun.ts.tests.jaxr.api.javax_xml_registry.InvalidRequestException;

import com.sun.ts.lib.util.*;
import com.sun.ts.lib.porting.*;
import com.sun.ts.lib.harness.*;
import com.sun.ts.tests.jaxr.common.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.*;
import com.sun.javatest.Status;

import javax.xml.registry.*;
import javax.xml.registry.infomodel.*;
import java.net.PasswordAuthentication;

public class JAXRClient extends JAXRCommonClient {

  // ================================================
  com.sun.ts.tests.jaxr.common.JAXR_Util util = null;

  public static void main(String[] args) {
    JAXRClient theTests = new JAXRClient();
    Status s = theTests.run(args, System.out, System.err);
    s.exit();
  }

  /* Test setup */
  /*
   * @class.setup_props: jaxrPassword; jaxrUser; jaxrPassword2; jaxrUser2;
   * registryURL; queryManagerURL; authenticationMethod; providerCapability;
   * jaxrConnectionFactoryLookup; jaxrSecurityCredentialType; jaxrJNDIResource;
   * jaxrAlias; jaxrAlias2; jaxrAliasPassword; jaxrAlias2Password;
   */
  public void setup(String[] args, Properties p) throws Fault {
    try {
      super.setup(args, p);

    } catch (Exception e) {
      throw new Fault("setup failed:", e);
    }
  }

  public void cleanup() throws Fault {
    try {
      super.cleanup(); // close connection
    } catch (Exception e) {
      TestUtil.logErr("Cleanup error: " + e.toString());
      TestUtil.printStackTrace(e);
    } finally {
      // print out messages
      if (debug != null)
        TestUtil.logTrace(debug.toString());
    }
  }

  /*
   * @testName: invalidRequestException_ConstructorTest1
   *
   * @assertion_ids: JAXR:JAVADOC:33;
   *
   * @assertion: InvalidRequestException Constructs a JAXRException object with
   * no reason or embedded Throwable
   *
   * @test_Strategy: Create and verify InvalidRequestException.
   *
   */
  public void invalidRequestException_ConstructorTest1() throws Fault {
    String testName = "invalidRequestException_ConstructorTest1()";
    try {
      debug.add("Create and verify a InvalidRequestException instance\n");
      InvalidRequestException e = new InvalidRequestException();
      if (!(e instanceof InvalidRequestException))
        throw new Fault(
            testName + "failed to create InvalidRequestException instance");
      else
        debug.add("InvalidRequestException created successfully\n");
    } catch (Exception ue) {
      TestUtil.logErr("Caught unexpected exception: " + ue.getMessage());
      TestUtil.printStackTrace(ue);
      throw new Fault(testName + "failed", ue);
    }
  } // end of test method

  /*
   * @testName: invalidException_ConstructorTest2
   *
   *
   * @assertion_ids: JAXR:JAVADOC:34;
   *
   * @assertion: InvalidRequestException(reason) - Constructs a JAXRException
   * object with the given String as the reason for the exception being thrown
   *
   * @test_Strategy: Create and verify InvalidRequestException, verify reason.
   *
   */
  public void invalidException_ConstructorTest2() throws Fault {
    String testName = "invalidException_ConstructorTest2()";
    String reason = "test reason";
    boolean pass = true;
    try {
      debug.add("Create and verify a InvalidRequestException(reason)\n");
      InvalidRequestException e = new InvalidRequestException(reason);
      if (!(e instanceof InvalidRequestException)) {
        pass = false;
        debug.add(
            testName + " failed to create InvalidRequestException instance\n");
      } else
        debug.add("InvalidRequestException created successfully\n");

      if (!(e.getMessage().equals(reason))) {
        pass = false;
        debug.add(testName + " failed to return correct reason\n");
      } else
        debug.add("InvalidRequestException returned reason successfully\n");

    } catch (Exception ue) {
      TestUtil.logErr("Caught unexpected exception: " + ue.getMessage());
      TestUtil.printStackTrace(ue);
      throw new Fault(testName + "failed", ue);
    }
    if (!pass)
      throw new Fault(testName + " failed");
  } // end of test method

  /*
   * @testName: invalidException_ConstructorTest3
   *
   *
   * @assertion_ids: JAXR:JAVADOC:35;
   *
   * @assertion: InvalidRequestException(reason, cause) - Constructs a
   * JAXRException object with the given String as the reason for the exception
   * being thrown and the given Throwable object as an embedded Throwable.
   *
   * @test_Strategy: Create and verify InvalidRequestException, verify reason
   * and cause.
   *
   */
  public void invalidException_ConstructorTest3() throws Fault {
    String testName = "invalidException_ConstructorTest3()";
    String reason = "test reason";
    String cause = "test cause";
    boolean pass = true;
    try {
      Throwable tCause = new Throwable(cause);
      debug.add("Create and verify a InvalidRequestException(reason,cause)\n");
      InvalidRequestException e = new InvalidRequestException(reason, tCause);
      if (!(e instanceof InvalidRequestException)) {
        pass = false;
        debug.add(
            testName + " failed to create InvalidRequestException instance\n");
      } else
        debug.add("InvalidRequestException created successfully\n");
      if (!(e.getMessage().equals(reason))) {
        pass = false;
        debug.add(testName + " failed to return correct reason\n");
      } else
        debug.add("InvalidRequestException returned reason successfully\n");
      Throwable t = e.getCause();
      debug.add("Cause is: " + t.getMessage() + "\n");
      if (!(t.getMessage().equals(cause))) {
        pass = false;
        debug.add(testName + " failed to return valid Throwable\n");
      }
    } catch (Exception ue) {
      TestUtil.logErr("Caught unexpected exception: " + ue.getMessage());
      TestUtil.printStackTrace(ue);
      throw new Fault(testName + " failed", ue);
    }
    if (!pass)
      throw new Fault(testName + " failed");

  } // end of test method

  /*
   * @testName: invalidException_ConstructorTest4
   *
   *
   * @assertion_ids: JAXR:JAVADOC:36;
   *
   * @assertion: InvalidRequestException(cause) - Constructs a JAXRException
   * object initialized with the given Exception object.
   *
   * @test_Strategy: Create and verify InvalidRequestException, verify cause.
   *
   */
  public void invalidException_ConstructorTest4() throws Fault {
    String testName = "invalidException_ConstructorTest4";
    String cause = "test cause";
    boolean pass = true;
    try {
      Throwable tCause = new Throwable(cause);
      debug.add("Create and verify a InvalidRequestException(cause)\n");
      InvalidRequestException e = new InvalidRequestException(tCause);

      if (!(e instanceof InvalidRequestException)) {
        pass = false;
        debug.add(
            testName + "failed to create  InvalidRequestException instance\n");
      } else
        debug.add("InvalidRequestException created successfully\n");

      Throwable t = e.getCause();
      debug.add("Cause is: " + t.getMessage() + "\n");
      if (!(t.getMessage().equals(cause))) {
        pass = false;
        debug.add(testName + " failed to return valid Throwable\n");
      }
    } catch (Exception ue) {
      TestUtil.logErr("Caught unexpected exception: " + ue.getMessage());
      TestUtil.printStackTrace(ue);
      throw new Fault(testName + "failed", ue);
    }
    if (!pass)
      throw new Fault(testName + " failed");

  } // end of test method

} // end of class
