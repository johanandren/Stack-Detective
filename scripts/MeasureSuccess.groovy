import com.markatta.stackdetective.clustering.DistanceMatrix
import com.markatta.stackdetective.model.StackTrace
import com.markatta.stackdetective.distance.DefaultDistanceCalculator
import com.markatta.stackdetective.distance.DistanceCalculator
import com.markatta.stackdetective.distance.cost.IntelligentSubstitutionStrategy
import com.markatta.stackdetective.parse.StackTraceParserFactory

def clustersDir = args[0]
def limit = Integer.parseInt(args[1])



// load and parse each trace
def parser = StackTraceParserFactory.getDefaultTextParser()
def tracesPerCluster = [:]
new File(clustersDir).eachDir { clusterDir ->
    def traces = []
    tracesPerCluster[clusterDir.name] = traces
    clusterDir.eachFile { tracefile ->
        traces << parser.parse(tracefile.text)
    }
}

// add each trace to the distance matrix
def calculator = new DefaultDistanceCalculator(new IntelligentSubstitutionStrategy())
def distanceMatrix = new DistanceMatrix<StackTrace>(calculator)
tracesPerCluster.each { clusterId, traces ->
    traces.each { trace ->
        distanceMatrix.add(trace)
    }
}



// figure out how many over limit in the same cluster
def missedSimiliar = 0
def matchedSimiliar = 0
def falsePositives = 0
tracesPerCluster.each {clusterId, traces -> 
    // distances within cluster
    traces.each { from ->
        traces.each { to ->
            if (from != to) {
                if (distanceMatrix.getDistanceBetween(from, to) > limit) {
                    missedSimiliar++
                } else {
                    matchedSimiliar++
                }
            }
        }
    }
    // distances to traces in other clusters
    tracesPerCluster.each { otherClusterId, otherTraces ->
        if (otherClusterId != clusterId) {
            traces.each { from -> 
                otherTraces.each{ to -> 
                    if (distanceMatrix.getDistanceBetween(from, to) < limit) {
                        falsePositives++
                    }
                }
            }
        }
    }       
}

// figure out how many false positives


println "Missed similiar total: ${missedSimiliar}"
println "Matched similiar total: ${matchedSimiliar}" 
println "False positives: ${falsePositives}"
