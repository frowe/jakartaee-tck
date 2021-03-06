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
 */
/*
 * $Id$
 */

package com.sun.ts.tests.jws.webmethod.webmethod3.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(wsdlLocation = "WEB-INF/wsdl/WebMethodWebServiceService.wsdl")
public class webMethodWebService {

  @WebMethod(operationName = "helloString", action = "urn:HelloString")
  public String hello(@WebParam(name = "name") String name) {
    return "Hello " + name + " to Web Service";
  }

  @WebMethod(operationName = "helloString2", action = "urn:HelloString")
  public String hello2(@WebParam(name = "name") String name, String name2) {
    return "Hello " + name + " to Web Service";
  }

  @WebMethod(exclude = true)
  public String excludeMe() {
    return "Hello to Web Service";
  }

}
