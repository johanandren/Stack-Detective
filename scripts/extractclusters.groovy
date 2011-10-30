#!/usr/bin/env groovy
// usage: script server webapp-basurl dump-to-dir

@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.1' )
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def server = args[0]
def baseurl = args[1]
def targetDir = args[2]


def clusterClient = new HTTPBuilder(server)
clusterClient.request(GET, JSON) {
    uri.path = "${baseurl}/json/clusters"
    
    response.success = {response, clusters-> 
        // loop over clusters
        clusters.each{ cluster ->
            def clusterDir = "${targetDir}/${cluster.id}"
            def clusterDirPath = new File(clusterDir)
            if (clusterDirPath.exists()) {
                throw new RuntimeException("${clusterDir} already exists");
            }
            clusterDirPath.mkdirs()
 
            cluster.stacktraces.each { trace -> 
                fetchTrace(server, baseurl, trace.id, "${clusterDir}/${trace.id}")
            }

        }
    }

}



def fetchTrace(def server, def baseurl, def traceid, def path) {
    def traceClient = new HTTPBuilder(server)
    traceClient.request(GET, JSON) {
        uri.path = "${baseurl}/json/trace/id/${traceid}"
    
        response.success = {response, trace ->
            def traceFile = new File(path)
            traceFile << trace.stacktrace
            print "."
        }
    }

}
