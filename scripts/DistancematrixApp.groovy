import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL 


def clustersDir = args[0]

def swing = new SwingBuilder()

swing.edt {
    frame(title: 'Avståndsmatris', size:[400,400], show: true) {
        borderLayout()
        tabbedPane() {
            new File(clustersDir).eachDir { clusterDir ->
                panel(name: clusterDir.getName()) {
                    label(clusterDir.getAbsolutePath())
                }
            }
        }
    }
}


def distanceMatrixFor(def path) {



}
