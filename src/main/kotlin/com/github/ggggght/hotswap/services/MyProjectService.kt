package com.github.ggggght.hotswap.services

import com.intellij.openapi.project.Project
import com.github.ggggght.hotswap.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
