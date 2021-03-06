package sylvanas.container.startup;

import org.apache.commons.digester.Digester;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import sylvanas.component.digester.sylvanas.SylvanasXML;
import sylvanas.component.lifecycle.LifecycleException;
import sylvanas.component.resource.Resource;
import sylvanas.container.Container;
import sylvanas.container.Context;
import sylvanas.container.Host;
import sylvanas.util.Constants;

import java.io.File;
import java.io.IOException;

/**
 <Sylvanas>
     <Connector port="8887" protocol="HTTP/1.2" connectionTimeout="20000"/>
     <Connector port="8888" protocol="HTTP/1.1" connectionTimeout="20000"/>
         <Host>
             <Valve className="sylvanas.container.pipeline.TestValve">
                 <init-param>
                     <param-name>charset</param-name>
                     <param-value>UTF-8</param-value>
                 </init-param>
             </Valve>
             <Context path="/index/abc" docBase="Sylvanas">
                 <Valve className="sylvanas.container.pipeline.TestValve">
                     <init-param>
                         <param-name>charset</param-name>
                         <param-value>UTF-9</param-value>
                 </init-param>
                 </Valve>
             </Context>
         </Host>
 </Sylvanas>
 *
 *
 *
 */
public class HostConfig {

    private Host host;

    private SylvanasXML xml = new SylvanasXML();

    private String appBase = Constants.APP_BASE;

    public HostConfig(Host host){

        this.host = host;
        config();
    }


    public void config(){

        //TODO: parseXML();

        File file = new File(appBase);
        File[] projects = file.listFiles(File::isDirectory);

        if (projects == null) {
           return;
        }

        for (File project : projects) {
            deployDirectories(project);
        }

    }

    public void undeploy(String projectName) throws LifecycleException,IOException {

        Context undeploy = null;
        for (Container container : host.getChildren()) {
            if (container.getName().equals(projectName)){
                undeploy = (Context)container;
            }
        }

        if (undeploy!=null) {

            undeploy.stop();
            host.removeChild(undeploy);

            FileUtils.deleteDirectory(undeploy.getDocBase());

        }
    }

    public void deployOutsideDir(File dir) throws LifecycleException,IOException{

        File appDir = new File(Constants.APP_BASE+dir.getName());
        FileUtils.copyDirectory(dir,appDir,true);
        Context context = deployDirectories(appDir);
        context.init();
        context.start();
    }


    public Context deployDirectories(File dir){
        Context context = createContext();
        context.setDocBase(dir);
        context.setPath("/"+dir.getName());
        context.setName(dir.getName());

        System.out.println("INFO: app deployed: " + context.getDocBase());

        host.addChild(context);
        context.setParent(host);

        host.getMapper().addContext(context,context.getPath());

        return context;
    }


    public Context createContext(){
        return new Context();
    }

    public void parseXML(Resource resource){

        Digester digester = createDigester();
        try {

            digester.parse(resource.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        digester.push(this);
    }

    public Digester createDigester(){
        Digester digester = new Digester();
        digester.setValidating(true);
        digester.push(this);

        digester.addObjectCreate("Sylvanas","sylvanas.component.digester.sylvanas.SylvanasXML","className");
        digester.addSetProperties("Sylvanas");
        digester.addSetNext("Sylvanas",
                "setSylvanasXML",
                "sylvanas.component.digester.sylvanas.SylvanasXML");

        digester.addObjectCreate("Sylvanas/Connector",
                "sylvanas.component.digester.sylvanas.ConnectorDef","className");
        digester.addSetProperties("Sylvanas/Connector");

        digester.addSetNext("Sylvanas/Connector",
                "addConnector",
                "sylvanas.component.digester.sylvanas.ConnectorDef");

        digester.addObjectCreate("Sylvanas/Host",
                "sylvanas.component.digester.sylvanas.HostDef","className");
        digester.addSetProperties("Sylvanas/Host");

        digester.addSetNext("Sylvanas/Host",
                "addHost",
                "sylvanas.component.digester.sylvanas.HostDef");

        digester.addObjectCreate("Sylvanas/Host/Valve",
                "","className");
        digester.addSetProperties("Sylvanas/Host/Valve");

        digester.addSetNext("Sylvanas/Host/Valve",
                "addValve",
                "sylvanas.container.pipeline.Valve");

        digester.addCallMethod("Sylvanas/Host/Valve/init-param",
                "addInitParameter", 2);
        digester.addCallParam("Sylvanas/Host/Valve/init-param/param-name",
                0);
        digester.addCallParam("Sylvanas/Host/Valve/init-param/param-value",
                1);

        digester.addObjectCreate("Sylvanas/Host/Context",
                "sylvanas.component.digester.sylvanas.ContextDef","className");
        digester.addSetProperties("Sylvanas/Host/Context");

        digester.addSetNext("Sylvanas/Host/Context",
                "addContext",
                "sylvanas.component.digester.sylvanas.ContextDef");


        digester.addObjectCreate("Sylvanas/Host/Context/Valve",
                "","className");
        digester.addSetProperties("Sylvanas/Host/Context/Valve");

        digester.addSetNext("Sylvanas/Host/Context/Valve",
                "addValve",
                "sylvanas.container.pipeline.Valve");

        digester.addCallMethod("Sylvanas/Host/Context/Valve/init-param",
                "addInitParameter", 2);
        digester.addCallParam("Sylvanas/Host/Context/Valve/init-param/param-name",
                0);
        digester.addCallParam("Sylvanas/Host/Context/Valve/init-param/param-value",
                1);

        return digester;
    }

    public SylvanasXML getXml() {
        return xml;
    }

    public void setXml(SylvanasXML xml) {
        this.xml = xml;
    }


}
