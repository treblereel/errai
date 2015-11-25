import org.jboss.errai.jms.generator.DiscoveryStrategy;
import org.jboss.errai.jms.util.ErraiJMSMDBUtil;
import org.junit.Test;

import junit.framework.TestCase;




public class ErraiJMSMDBUtilTest extends TestCase {
  
  @Test
  public void testPrintMessage() {
    DiscoveryStrategy[] rootDiscoveryStrategies = ErraiJMSMDBUtil.findDiscoveryStrategies();
    for(DiscoveryStrategy discoveryStrategy:  rootDiscoveryStrategies){
   //   System.out.println(discoveryStrategy.);
    }
    
    System.out.println("size : " + rootDiscoveryStrategies.length);
  }

}
