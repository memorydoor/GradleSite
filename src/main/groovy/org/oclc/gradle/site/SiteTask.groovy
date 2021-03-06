package org.oclc.gradle.site

import org.apache.maven.doxia.site.decoration.Banner
import org.apache.maven.doxia.site.decoration.DecorationModel
import org.apache.maven.doxia.site.decoration.io.xpp3.DecorationXpp3Reader
import org.apache.maven.doxia.siterenderer.Renderer
import org.apache.maven.doxia.siterenderer.SiteRenderingContext
import org.codehaus.plexus.ContainerConfiguration
import org.codehaus.plexus.DefaultContainerConfiguration
import org.codehaus.plexus.DefaultPlexusContainer
import org.codehaus.plexus.PlexusContainer
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created with IntelliJ IDEA.
 * User: chengy
 * Date: 10/4/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
class SiteTask extends DefaultTask {

    @InputDirectory
    File inputDir = getProject().file("src/site")

    @OutputDirectory
    File outputDir = getProject().file("out/site")

    @TaskAction
    def generateSite() {
        InputStream inputStream = null

        OutputStream outputStream = null;

        try {
            // read this file into InputStream
            inputStream = getClass().getResourceAsStream("/skinjar/maven-default-skin-1.1.jar")

            // write the inputStream to a FileOutputStream
            outputStream =
                new FileOutputStream(new File(outputDir, "maven-default-skin-1.1.jar"));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        ContainerConfiguration containerConfiguration = new DefaultContainerConfiguration()
        PlexusContainer container = new DefaultPlexusContainer(containerConfiguration);

        Renderer renderer = container.lookup(Renderer.ROLE)

        def reader = new DecorationXpp3Reader();
        DecorationModel decoration = reader.read(new FileReader(new File(inputDir, "site.xml")))

        def projectName = project.name

        Banner bannerLeft = new Banner()
        bannerLeft.name = projectName
        decoration.bannerLeft = bannerLeft


        final Map<String, String> templateProp = new HashMap<String, String>();
        templateProp.put( "outputEncoding", "UTF-8" );
        templateProp.put( "project", project)
        SiteRenderingContext ctxt = renderer.createContextForSkin( new File(outputDir, "maven-default-skin-1.1.jar"), templateProp, decoration,
                projectName, Locale.getDefault())
        ctxt.setUsingDefaultTemplate( true );
        ctxt.addSiteDirectory( inputDir );
        ctxt.setValidate( false );

        renderer.render(renderer.locateDocumentFiles(ctxt).values(), ctxt, outputDir)
    }

}
