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
package sonata.kernel.adaptor.wrapper;

/**
 * 
 */
public class WrapperStatusUpdate {

  private String SID;
  private String status;
  private String body;

  public WrapperStatusUpdate(String sid, String status, String body) {
    this.SID = sid;
    this.status = status;
    this.body = body;
  }

  public String getSID() {
    return SID;
  }

  public String getStatus() {
    return status;
  }

  public String getBody() {
    return body;
  }

}
