/**
 * @author Dario Valocchi (Ph.D.)
 * @mail d.valocchi@ucl.ac.uk
 * 
 *       Copyright 2016 [Dario Valocchi]
 * 
 *       Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *       except in compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *       Unless required by applicable law or agreed to in writing, software distributed under the
 *       License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *       either express or implied. See the License for the specific language governing permissions
 *       and limitations under the License.
 * 
 */
package sonata.kernel.VimAdaptor.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import sonata.kernel.VimAdaptor.AdaptorMux;
import sonata.kernel.VimAdaptor.ConfigureNetworkCallProcessor;
import sonata.kernel.VimAdaptor.commons.NetworkConfigurePayload;
import sonata.kernel.VimAdaptor.commons.ServiceDeployPayload;
import sonata.kernel.VimAdaptor.commons.VnfRecord;
import sonata.kernel.VimAdaptor.commons.heat.StackComposition;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.VimAdaptor.wrapper.mock.ComputeMockWrapper;
import sonata.kernel.VimAdaptor.wrapper.ovsWrapper.OvsWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class OvsWrapperTest {

  private ServiceDeployPayload data;
  private ArrayList<VnfRecord> records;
  private ObjectMapper mapper;

  @Before
  public void setUp() throws Exception {

    ServiceDescriptor sd;
    StringBuilder bodyBuilder = new StringBuilder();
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/long-chain-demo.yml")), Charset.forName("UTF-8")));
    String line;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    this.mapper = new ObjectMapper(new YAMLFactory());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Unit.class, new UnitDeserializer());
    mapper.registerModule(module);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.setSerializationInclusion(Include.NON_NULL);

    sd = mapper.readValue(bodyBuilder.toString(), ServiceDescriptor.class);

    VnfDescriptor vnfd1;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/1-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd1 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    VnfDescriptor vnfd2;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/2-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd2 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    VnfDescriptor vnfd3;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/3-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd3 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    VnfDescriptor vnfd4;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/4-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd4 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    VnfDescriptor vnfd5;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/5-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd5 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    this.data = new ServiceDeployPayload();
    sd.setInstanceUuid(sd.getInstanceUuid() + "IASFCTEST");
    data.setServiceDescriptor(sd);
    data.addVnfDescriptor(vnfd1);
    data.addVnfDescriptor(vnfd2);
    data.addVnfDescriptor(vnfd3);
    data.addVnfDescriptor(vnfd4);
    data.addVnfDescriptor(vnfd5);

    records = new ArrayList<VnfRecord>();
    for (int i = 1; i <= 5; i++) {
      VnfRecord record;
      bodyBuilder = new StringBuilder();
      in = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File("./YAML/" + i + "-vnf-vnfr.yml")),
              Charset.forName("UTF-8")));
      line = null;
      while ((line = in.readLine()) != null)
        bodyBuilder.append(line + "\n\r");
      record = mapper.readValue(bodyBuilder.toString(), VnfRecord.class);
      records.add(record);
    }

  }

  @Ignore
  public void testOvsWrapper() throws Exception {

    // TODO FIXME Edit this test to reflect the new NetworkWrapper interface.
    VimRepo repoInstance = new VimRepo();
    WrapperBay.getInstance().setRepo(repoInstance);
    String instanceId = data.getNsd().getInstanceUuid();
    String computeUuid1 = "1111-11111-1111";
    String computeUuid2 = "2222-22222-2222";
    String computeUuid3 = "3333-33333-3333";
    String netUuid1 = "aaaa-aaaaa-aaaa";
    String netUuid2 = "bbbb-bbbbb-bbbb";
    String netUuid3 = "cccc-ccccc-cccc";
    // First PoP
    WrapperConfiguration config = new WrapperConfiguration();
    config.setVimEndpoint("x.x.x.x");
    config.setVimVendor("mock");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setTenantName("tenant");
    config.setUuid(computeUuid1);
    config.setWrapperType("compute");
    config.setTenantExtNet("ext-subnet");
    config.setTenantExtRouter("ext-router");
    WrapperRecord record = new WrapperRecord(new ComputeMockWrapper(config), config, null);
    boolean out = repoInstance.writeVimEntry(config.getUuid(), record);
    Assert.assertTrue("Unable to write the compute vim", out);

    config = new WrapperConfiguration();
    config.setVimEndpoint("10.100.32.200");
    config.setVimVendor("ovs");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setTenantName("tenant");
    config.setUuid(netUuid1);
    config.setWrapperType("network");
    config.setTenantExtNet(null);
    config.setTenantExtRouter(null);
    record = new WrapperRecord(new OvsWrapper(config), config, null);
    out = repoInstance.writeVimEntry(config.getUuid(), record);
    repoInstance.writeNetworkVimLink(computeUuid1, netUuid1);

    // Second PoP
    config = new WrapperConfiguration();
    config.setVimEndpoint("x.x.x.x");
    config.setVimVendor("mock");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setTenantName("tenant");
    config.setUuid(computeUuid2);
    config.setWrapperType("compute");
    config.setTenantExtNet("ext-subnet");
    config.setTenantExtRouter("ext-router");
    record = new WrapperRecord(new ComputeMockWrapper(config), config, null);
    out = repoInstance.writeVimEntry(config.getUuid(), record);
    Assert.assertTrue("Unable to write the compute vim", out);

    config = new WrapperConfiguration();
    config.setVimEndpoint("10.100.32.200");
    config.setVimVendor("ovs");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setTenantName("tenant");
    config.setUuid(netUuid2);
    config.setWrapperType("network");
    config.setTenantExtNet(null);
    config.setTenantExtRouter(null);
    record = new WrapperRecord(new OvsWrapper(config), config, null);
    out = repoInstance.writeVimEntry(config.getUuid(), record);
    repoInstance.writeNetworkVimLink(computeUuid2, netUuid2);

    // Third PoP
    config = new WrapperConfiguration();
    config.setVimEndpoint("x.x.x.x");
    config.setVimVendor("mock");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setTenantName("tenant");
    config.setUuid(computeUuid3);
    config.setWrapperType("compute");
    config.setTenantExtNet("ext-subnet");
    config.setTenantExtRouter("ext-router");
    record = new WrapperRecord(new ComputeMockWrapper(config), config, null);
    out = repoInstance.writeVimEntry(config.getUuid(), record);
    Assert.assertTrue("Unable to write the compute vim", out);

    config = new WrapperConfiguration();
    config.setVimEndpoint("10.100.32.200");
    config.setVimVendor("ovs");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setTenantName("tenant");
    config.setUuid(netUuid3);
    config.setWrapperType("network");
    config.setTenantExtNet(null);
    config.setTenantExtRouter(null);
    record = new WrapperRecord(new OvsWrapper(config), config, null);
    out = repoInstance.writeVimEntry(config.getUuid(), record);
    repoInstance.writeNetworkVimLink(computeUuid3, netUuid3);

    
    //Populate VimRepo with Instance data, VNF1 And VNF2 are deployed on PoP1, VNF3 on PoP2, and VNF4 and VNF5 on PoP3
    repoInstance.writeServiceInstanceEntry(instanceId, "1", "stack-1", computeUuid1);
    repoInstance.writeServiceInstanceEntry(instanceId, "1", "stack-1", computeUuid2);
    repoInstance.writeServiceInstanceEntry(instanceId, "1", "stack-1", computeUuid3);
    
    repoInstance.writeFunctionInstanceEntry(data.getVnfdList().get(0).getInstanceUuid(), instanceId, computeUuid1);
    repoInstance.writeFunctionInstanceEntry(data.getVnfdList().get(1).getInstanceUuid(), instanceId, computeUuid1);
    repoInstance.writeFunctionInstanceEntry(data.getVnfdList().get(2).getInstanceUuid(), instanceId, computeUuid2);
    repoInstance.writeFunctionInstanceEntry(data.getVnfdList().get(3).getInstanceUuid(), instanceId, computeUuid3);
    repoInstance.writeFunctionInstanceEntry(data.getVnfdList().get(4).getInstanceUuid(), instanceId, computeUuid3);

    //Prepare environment and create che call processor.
    NetworkConfigurePayload netData = new NetworkConfigurePayload();
    netData.setServiceInstanceId(data.getNsd().getInstanceUuid());
    netData.setNsd(data.getNsd());
    netData.setVnfds(data.getVnfdList());
    netData.setVnfrs(records);
    String message = mapper.writeValueAsString(netData);
    AdaptorMux mux = new AdaptorMux(new LinkedBlockingQueue<ServicePlatformMessage>());
    ServicePlatformMessage spMessage = new ServicePlatformMessage(message, "application/xyaml", "chain.setup", "abla", "chain.setup");
    Thread t = new Thread(new ConfigureNetworkCallProcessor(spMessage,"abla",mux));
    
    t.run();
    
  }

}
