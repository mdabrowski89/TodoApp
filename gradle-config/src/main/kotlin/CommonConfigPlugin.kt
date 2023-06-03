import org.gradle.api.Plugin
import org.gradle.api.Project

class CommonConfigPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        // no-op
    }
}

object Deps {
    val fragment_ktx = "androidx.fragment:fragment-ktx:1.5.7"

}


