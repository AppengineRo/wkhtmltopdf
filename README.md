This sample is provided by an NGO from Romania called [Asociatia AppEngine][6]

# Sample 'wkhtmltopdf' application for use with the App Engine Java VM Runtime.

This sample has 2 parts:
1. [Wkhtmltopdf][5] html to pdf transform tool
2. A console like browser based interface to execute commands on the VM inside the Docker enviroment. (ssh doesn't really help you with that)

To test this sample go to: http://appversion-dot-appmodule-dot-appid.appspot.com
run this 2 commands in the the first input (in order to update wkhtmltopdf to the latest version):
1. tar xf wkhtmltox-0.12.1-c22928d_linux-wheezy-amd64.tar.xz -C /
2. cp /wkhtmltox-0.12.1-c22928d/bin/wkhtmltopdf /usr/local/bin

Then write google.com in the url input, or any html in the textarea and transform your html into pdf.

Requires [Apache Maven](http://maven.apache.org) 3.0 or greater, and
JDK 7+ in order to run.  This application needs to be deployed to the
[App Engine VM Runtime][1].

Make sure that you are invited to the [VM Runtime Trusted Tester
Program][2], and have [downloaded the SDK][4].

To build, do the following:

1. Change the value of the `application`, `module` and `version` elements in your `appengine-web.xml` to the app
id of an app that has been whitelisted for the VM Runtime.
2. Run `mvn package`
3. Run the `appcfg.sh` script from the VM Runtime SDK as follows:

        $ $SDK_DIR/bin/appcfg.sh -s preview.appengine.google.com update target/wkhtmltopdf-1.0-SNAPSHOT

4. Visit `http://your-app-id.appspot.com/`.

For further information, consult the [Java App Engine](https://developers.google.com/appengine/docs/java/overview)
documentation.

To see all the available goals for the App Engine plugin, run

    mvn help:describe -Dplugin=appengine

[1]: https://docs.google.com/document/d/1VH1oVarfKILAF_TfvETtPPE3TFzIuWqsa22PtkRkgJ4
[2]: https://groups.google.com/forum/?fromgroups#!topic/google-appengine/gRZNqlQPKys
[3]: https://cloud.google.com/console
[4]: http://commondatastorage.googleapis.com/gae-vm-runtime-tt/vmruntime_sdks.html
[5]: http://wkhtmltopdf.org/
[6]: http://www.appengine.ro/
