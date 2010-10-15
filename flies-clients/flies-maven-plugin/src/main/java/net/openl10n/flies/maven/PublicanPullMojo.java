package net.openl10n.flies.maven;

import java.io.File;

import net.openl10n.flies.client.commands.PublicanPullCommand;
import net.openl10n.flies.client.commands.PublicanPullOptions;

/**
 * [NOT YET IMPLEMENTED] Retrieves translated text from a Flies project version.
 * 
 * @goal retrieve
 * @requiresProject true
 * @author Sean Flanigan <sflaniga@redhat.com>
 */
public class PublicanPullMojo extends ConfigurableProjectMojo implements PublicanPullOptions
{

   /**
    * Base directory for publican files (with subdirectory "pot" and optional
    * locale directories)
    * 
    * @parameter expression="${flies.dstDir}"
    * @required
    */
   private File dstDir;

   public PublicanPullMojo() throws Exception
   {
      super();
   }

   public PublicanPullCommand initCommand()
   {
      return new PublicanPullCommand(this);
   }

   @Override
   public void setDstDir(File dstDir)
   {
      this.dstDir = dstDir;
   }

   @Override
   public File getDstDir()
   {
      return dstDir;
   }

}